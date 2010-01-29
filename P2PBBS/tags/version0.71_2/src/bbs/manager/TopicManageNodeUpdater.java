package bbs.manager;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import bbs.comment.CommentElement;
import bbs.comment.TopicManager;
import bbs.dht.DHTManager;
import bbs.http.HttpClientSocket;
import bbs.http.RawHttpClientSocket;
import bbs.monitor.MonitorManager;
import bbs.util.HTMLEncode;
import bbs.util.Pair;
import bbs.xml.BBSXMLParser;

import ow.id.IDAddressPair;
import ow.messaging.*;

/**
 * 一定間隔でトピック管理ノードの更新を行う
 * 
 * @author nishio
 * 
 */
public class TopicManageNodeUpdater implements Runnable {
	private TopicManager topicManager = null;
	private DHTManager dhtManager = null;
	//トピック活性度
	private TopicActivityManager topicActivityManager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();

	// トピック情報更新間隔
	private long topicUpdateInterval = 30 * 1000/*3 * 1000*/;
	// トピック管理ノード情報更新間隔
	//private int topicManageNodeUpdateInterval = 10 * 60 * 1000;
	// バックアップトピック管理ノード数最大(動的に変化)
	private int numOfMaxBackupNode = 5;
	//バックアップトピック管理ノード最低限の数
	private final int numOfMinBackupNode = 2;
	//バックアップトピック管理ノードの数を決めるための定数(TODO 要検討）
	private int backupParam = 90 * 120;
	// バックアップコメントを管理しているノードのアドレス
	// 自分から距離が近い順に並んでいる
	private List<MessagingAddress> manageNodeAddress = null;
	//トピック管理ノード情報のTTL (デフォルトでは2分）
	private long topicManageNodeInfoTTL = 2 * 60 * 1000/*60 * 1000*/;
	//トピック情報のTTL
	private long topicInfoTTL = topicUpdateInterval * 3;
	//retry回数
	private int numOfRetry = 2;

	// 自身のスレッド
	private Thread thread = null;

	private static final Logger logger = Logger.getLogger("bbs.topic");

	/**
	 * 
	 * @param topicManager
	 * @param dhtManager
	 */
	public TopicManageNodeUpdater(TopicManager topicManager,
			DHTManager dhtManager, TopicActivityManager topicActivityManager) {
		this.topicManager = topicManager;
		this.dhtManager = dhtManager;
		this.topicActivityManager = topicActivityManager;

		this.manageNodeAddress = new LinkedList<MessagingAddress>();
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		for (;;) {
			try {
				// バックアップトピック管理ノードのアップデート
				updateManageNodeAddress();
				// バックアップデータをバックアップトピック管理ノードに送信
				Set<Pair<String, String>> topics = topicManager.getTopics();
				if (topics != null) {
					for (Pair<String, String> topic : topics) {
						String topicID = topic.getFirst();
						String categoryID = topic.getSecond();

						this.update(categoryID, topicID);
					}
				}
				//活性度の更新
				this.topicActivityManager.update();				
				// しばらく休憩
				//TODO: トピック管理ノード情報の更新間隔と，バックアップデータ送信間隔を別のものにする？
				Thread.sleep(this.topicUpdateInterval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * トピック管理ノードアドレスの更新を行う
	 */
	private void updateManageNodeAddress() {
		//トピックバックアップ管理を行うノードの数
		this.numOfMaxBackupNode = this.topicActivityManager.maxActivity();
		if( this.numOfMaxBackupNode < this.numOfMinBackupNode ) {
			this.numOfMaxBackupNode = this.numOfMinBackupNode;
		}
		// トピックバックアップ管理ノードが十分に存在する時
		if (manageNodeAddress.size() > this.numOfMaxBackupNode) {
			return;
		}
		IDAddressPair[] succ = dhtManager.getSuccessor();

		// 最大 numOfBackupNode個のサクセッサアドレスを登録
		// succ[0]は自分自身
		for (int i = 1; i < succ.length
				&& manageNodeAddress.size() < numOfMaxBackupNode; i++) {
			// 既に登録済みだったら無視
			if (manageNodeAddress.contains(succ[i].getAddress())) {
				continue;
			} else {
				for(int j=0; j < numOfRetry; j++ ) {
					try {
						// サクセッサにPINGを送る
						HttpClientSocket sock = new HttpClientSocket();
						String url = "http://"
								+ succ[i].getAddress().getHostAddress() + ":"
								+ succ[i].getAddress().getPort() + "/command/ping";
						
						//送信データログ記録
						monitorManager.addMessageLog("TOPIC_SEND",
								succ[i].getAddress().getHostAddress()
								+ ":" + succ[i].getAddress().getPort(), 0,
								"PING");
						
						sock.sendAndReceive(url, "GET", "");
						
						//受信データログ記録
						monitorManager.addMessageLog("TOPIC_RECEIVE",
								succ[i].getAddress().getHostAddress()
								+ ":" + succ[i].getAddress().getPort(), 0,
								"PING");
						
						// バックアップトピック管理ノード候補を発見
						manageNodeAddress.add(succ[i].getAddress());
						logger.info("バックアップトピック管理ノード候補 " + succ[i].getAddress()
								+ "を発見しました.");
						break;
					} catch (UnknownHostException e) {
						// 無視
					} catch (IOException e) {
						// 無視
					}
				}
			}
		}
	}
	
	/**
	 * 指定したホストにトピックのバックアップを送信する
	 * @param categoryID
	 * @param topicID
	 * @param fromNo
	 * @param hostAddress
	 * @param port
	 * @return 成功したか失敗したか
	 */
	private boolean sendBackup(String categoryID, String topicID, int fromNo,
			String hostAddress, int port) {
		HttpClientSocket sock = new HttpClientSocket();
		//RawHttpClientSocket sock = new RawHttpClientSocket();
		try {
			SortedSet<CommentElement> comments = this.topicManager.get(topicID,
					categoryID, fromNo);
			if (comments == null || comments.size() <= 0) {
				throw new IOException("コメントが存在しません．");
			}
			// 送信するコメントデータ
			StringBuffer buf = new StringBuffer("");
			for (CommentElement comment : comments) {
				//Messageに改行が入らないバグがあるためこのようにした
				CommentElement sendComment = new CommentElement(
						comment.getNo(), comment.getAuthor(), comment
								.getMailAddress(), comment.getId(), comment
								.getDate(), HTMLEncode.replaceSpace(HTMLEncode
								.replaceNewline(comment.getMessage())));
				sendComment.setTitle(comment.getTitle());
				buf.append(TopicManager.commentElementToXML(sendComment));
			}
			// データ送信先
			String url = "http://" + hostAddress + ":" + port
					+ "/command/backup/" + categoryID + "/" + topicID;
			
			//送信データログ記録
			monitorManager.addMessageLog("TOPIC_SEND",
					hostAddress + ":" + port, buf.length(), "BACKUP");
			
			//データ送信
			sock.sendAndReceive(url, "POST", buf.toString());
			
			//受信データログ記録
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					hostAddress + ":" + port, 0, "BACKUP");
			
			//sock.sendAndReceive("/command/backup/" + categoryID + "/" + topicID,
			//		hostAddress, port, "POST", buf.toString());
			
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			logger.info("バックアップトピックの送信に失敗しました．送信先=" + hostAddress + ":" + port);
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("バックアップトピックの送信に失敗しました．送信先=" + hostAddress + ":" + port);
		}
		return false;
	}
	
	/**
	 * DHT上にトピック管理ノードの情報を登録する
	 * key = categoryID+topicID
	 * @param categoryID
	 * @param topicID
	 */
	private void putTopicManageNodeToDHT(String categoryID, String topicID) {
		StringBuffer buf = new StringBuffer();
		// 保持しているコメント数
		int manageCommentNum = this.topicManager.size(topicID, categoryID);
		buf.append("<num>" + manageCommentNum + "</num>\n");
		buf.append("<activity>"
				+ this.topicActivityManager.getActivity(categoryID, topicID)
				+ "</activity>");
		//コメントデータ量
		int dataSize = this.topicManager.calcMessageDataSize(topicID, categoryID);
		buf.append("<size>" + dataSize + "</size>");

		// 自分のアドレス取得
		IDAddressPair pair = this.dhtManager.getSelfAddress();
		buf.append("<node id=\"1\"><ip>" + pair.getAddress().getHostAddress()
				+ ":" + pair.getAddress().getPort() + "</ip></node>");
		//トピックバックアップ管理ノード情報
		//TODO バックアップノード数をここでアクセス数によって変える
		for (int i = 0; i < manageNodeAddress.size(); i++) {
			MessagingAddress address = this.manageNodeAddress.get(i);
			buf.append("<node id=\"" + (i + 2) + "\"><ip>"
					+ address.getHostAddress() + ":" + address.getPort()
					+ "</ip></node>");
		}
		// DHTにデータ投入
		try {
			this.dhtManager.put(categoryID + topicID, buf.toString(),
					this.topicManageNodeInfoTTL);
		} catch (IOException e) {
			logger.log(Level.WARNING, "DHT上へのトピック管理ノード情報の登録に失敗しました．"
					+ "category: " + categoryID + " topic: " + topicID);
		}
	}
	
	/**
	 * DHTにトピックの情報を登録する
	 * @param categoryID
	 * @param topicID
	 */
	private void putTopicInfoToDHT(String categoryID, String topicID) {
		StringBuffer buf = new StringBuffer("");
		//トピック識別子及びトピックタイトル登録
		//No1のコメントのみ取得
		Set<CommentElement> comments = this.topicManager.get(topicID, categoryID, 1, 2);
		try{
			String title = null;
			//タイトル取得
			for(CommentElement comment : comments) {
				title = comment.getTitle();
			}
			if( title == null ) {
				throw new Exception("タイトル取得に失敗しました．");
			}
			buf.append("<tid>" + topicID + "</tid><title>" + HTMLEncode.encode(title) + "</title>");
			// 保持しているコメント数
			int manageCommentNum = this.topicManager.size(topicID, categoryID);
			buf.append("<num>" + manageCommentNum + "</num>\n");
			//活性度
			int activity = this.topicActivityManager.getActivity(categoryID, topicID);
			buf.append("<activity>" + activity + "</activity>");
			// DHTにデータ登録
			this.dhtManager.put(categoryID, buf.toString(), this.topicInfoTTL);
		} catch (IOException e) {
			logger.log(Level.WARNING, "DHT上へのトピック情報の登録に失敗しました．"
					+ "category: " + categoryID + " topic: " + topicID);
		} catch (Exception e) {
			logger.log(Level.WARNING,
					"トピックタイトルを取得できませんでした．DHT上へトピック情報を登録できませんでした．"
							+ "category: " + categoryID + " topic: " + topicID);
		}
	}

	/**
	 * 指定したトピックの更新作業を行う．
	 * 
	 * @param categoryID
	 * @param topicID
	 */
	public void update(String categoryID, String topicID) {
		// 指定したトピックの管理ノードではない
		if (this.topicManager.contains(categoryID, topicID)) {
			return;
		}

		// 管理ノード競合時の対策
		boolean solve = solveConflict(categoryID, topicID);
		if( solve == false ) {
			return;
		}

		// 自分のサクセッサにデータ保持数を尋ねる
		if (this.manageNodeAddress != null && this.manageNodeAddress.size() > 0) {
			//トピックの活性度を取得
			int ac = topicActivityManager.getActivity(categoryID, topicID);
			//トピックの活性度からいくつのノードにデータを渡すかを決める
			int numOfBackupNode = ac / this.backupParam;
			//最低限の数
			if( numOfBackupNode < this.numOfMinBackupNode ) {
				numOfBackupNode = this.numOfMinBackupNode;
			}
			// コメント管理ノードにデータ保持数を問い合わせる
			for (int i = 0; i < manageNodeAddress.size() && i < numOfBackupNode; i++) {
				// backup管理ノードのアドレス
				MessagingAddress dest = manageNodeAddress.get(i);
				//System.out.println("Category:" + categoryID + " Topic: " + topicID + " backupNum:" + i);
				// haveメッセージ送信，トピックバックアップ管理ノードが
				// 最新のトピックを保持しているかどうかを尋ねる
				HttpClientSocket haveSock = new HttpClientSocket();
				String receiveData = null;
				
				for(int j=0; j < numOfRetry; j++ ) {
					try {
						String url = "http://" + dest.getHostAddress() + ":"
								+ dest.getPort() + "/command/have/" + categoryID
								+ "/" + topicID;
						//送信データログ記録
						monitorManager.addMessageLog("TOPIC_SEND",
								dest.getHostAddress() + ":" + dest.getPort(), 0, "HAVE");
						//送受信
						receiveData = haveSock.sendAndReceive(url, "POST", "");
						int len = 0;
						if( receiveData != null ) {
							len = receiveData.length();
						}
						//受信データログ記録
						monitorManager.addMessageLog("TOPIC_RECEIVE",
								dest.getHostAddress() + ":" + dest.getPort(), len, "HAVE");
						break;
					} catch (UnknownHostException e) {
						logger.info("不明なURLです．URL=" + "http://"
								+ dest.getHostAddress() + ":" + dest.getPort()
								+ "/command/have");
					} catch (IOException e) {
						logger.info("コネクションがタイムアウトしました．URL=" + "http://"
								+ dest.getHostAddress() + ":" + dest.getPort()
								+ "/command/have");
					}
				}

				// 指定したノードが見つからなかった
				if (receiveData == null) {
					logger.info("バックアップトピック管理ノード :" + dest.toString()
							+ " が見つかりませんでした.");
					manageNodeAddress.remove(i);
					i--; // 一つ前のインデックスに戻す
					continue;
				}
				String[] haveStr = receiveData.split(" |\n");
				try {
					if (haveStr != null && haveStr.length == 5
							&& haveStr[0].equals("I_HAVE")
							&& haveStr[1].equals(categoryID)
							&& haveStr[2].equals(topicID)) {
						// 相手がいくつのコメントを保持しているか
						int numOfComment = Integer.parseInt(haveStr[3]);

						// TODO: もし相手の方が自分より保持しているコメントが多かったらどうする？
						
						//トピックの活性度を取得
						int activity = Integer.parseInt(haveStr[4]);
						this.topicActivityManager.updateFromActivity(categoryID, topicID, activity);

						// 相手の持っているコメント数が自身の管理しているコメント数より少ない時
						int ihave = topicManager.size(topicID, categoryID);
						if (numOfComment < ihave) {
							// 足りない分を相手へ送信する
							boolean r = sendBackup(categoryID, topicID, numOfComment,
									dest.getHostAddress(), dest.getPort() );

							if( r ) {
								logger.info(dest.toString() + "に Category="
										+ categoryID + " Topic=" + topicID
										+ " のバックアップコメントを送信しました．");
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.info(dest.toString() + "は不正なノードです．");
					manageNodeAddress.remove(i);
					i--; // 一つ前のインデックスに戻す
				}
			}
		}
		//dhtへトピック管理ノード情報の登録
		putTopicManageNodeToDHT(categoryID, topicID);
		//dhtへトピック情報の登録
		putTopicInfoToDHT(categoryID, topicID);
	}
	
	/**
	 * トピック管理ノードの衝突回避
	 * @param categoryID
	 * @param topicID
	 * @return true=衝突を回避, false=衝突が起こっていた,かつ自身は管理ノードではなくなった
	 */
	private boolean solveConflict(String categoryID, String topicID) {
		// DHTから管理ノード取得
		SortedMap<Long, String> manageNodes = null;
		//衝突回避
		boolean solve = true;
		try {
			// DHTからトピックを保持しているノード情報を取得
			manageNodes = this.dhtManager.getTTLValuePair(categoryID + topicID);
			// ぬるぽ対策
			if (manageNodes != null && manageNodes.size() != 0) {
				// 一番TTLの大きい情報を使う
				String manageNodeInfo = manageNodes.get(manageNodes.firstKey());
				if (manageNodeInfo != null) {
					// トピック管理ノードのアドレス
					String address = BBSXMLParser.manageNodeXMLToIPAddress(
							"<manage>" + manageNodeInfo + "</manage>").get(0);
					// 自分のアドレスを取得
					String selfAddress = this.dhtManager.getSelfAddress()
							.getAddress().getHostAddress()
							+ ":"
							+ this.dhtManager.getSelfAddress().getAddress()
									.getPort();

					// トピック管理ノードが自分自身ではない時，衝突回避
					if (address != null && !address.equals(selfAddress)) {
						HttpClientSocket haveSock = new HttpClientSocket();
						String receiveData = null;
						
						logger.info("カテゴリ:" + categoryID + " トピック:" + topicID
								+ "の衝突を検出しました．");

						// 重複した管理ノードにどれだけのコメントを保持しているか尋ねる
						String url = "http://" + address + "/command/have/"
								+ categoryID + "/" + topicID;
						receiveData = haveSock.sendAndReceive(url, "POST", "");

						// 重複した管理ノードから返事があった
						if (receiveData != null) {
							String[] haveStr = receiveData.split(" |\n");
							if (haveStr != null && haveStr.length == 4
									&& haveStr[0].equals("I_HAVE")
									&& haveStr[1].equals(categoryID)
									&& haveStr[2].equals(topicID)) {
								// 相手がいくつのコメントを保持しているか
								int numOfComment = Integer.parseInt(haveStr[3]);
								// 相手の持っているコメント数が自身の管理しているコメント数より多いとき
								int ihave = topicManager.size(topicID,
										categoryID);
								//TODO もしかしたら以下のコードがうまく動いていないかもしれない
								//要検討
								if (numOfComment >= ihave) {
									//自身は管理ノードではなくなる
									solve = false;
									//トピック削除
									this.topicManager.remove(topicID, categoryID);

									logger.info("カテゴリ:" + categoryID + " トピック:"
											+ topicID + "の管理ノードではなくなります．");
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			//無視
		} catch (Exception e) {
			e.printStackTrace();
		}
		return solve;
	}

	/**
	 * topicUpdateIntervalを取得します。
	 * @return topicUpdateInterval
	 */
	public long getTopicUpdateInterval() {
	    return topicUpdateInterval;
	}

	/**
	 * topicUpdateIntervalを設定します。
	 * @param topicUpdateInterval topicUpdateInterval
	 */
	public void setTopicUpdateInterval(long topicUpdateInterval) {
	    this.topicUpdateInterval = topicUpdateInterval;
	}
}
