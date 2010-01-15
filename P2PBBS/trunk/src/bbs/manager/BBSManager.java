package bbs.manager;

import java.io.StringReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import bbs.BBSConfiguration;
import bbs.comment.BadPasswordException;
import bbs.comment.CannotCreateTopicException;
import bbs.comment.CommentElement;
import bbs.comment.CommentElementXMLParser;
import bbs.comment.CommentNotFoundException;
import bbs.comment.LocalTopicManager;
import bbs.comment.MaxLengthOfAuthorException;
import bbs.comment.MaxLengthOfCommentException;
import bbs.comment.MaxLengthOfMailException;
import bbs.comment.MaxNumberOfCommentException;
import bbs.comment.TopicManager;
import bbs.comment.TopicNotFoundException;
import bbs.dht.DHTManager;
import bbs.dht.InitDHTException;
import bbs.http.HttpClientSocket;
import bbs.http.HttpClientSocket2;
import bbs.http.RawHttpClientSocket;
import bbs.monitor.MessageLog;
import bbs.monitor.MonitorManager;
import bbs.util.HTMLEncode;
import bbs.util.Pair;
import bbs.xml.BBSXMLParser;

/**
 * P2P掲示板機能管理
 * @author nishio
 *
 */
public class BBSManager {
	
	private TopicManager topicManager = null; //自身が管理するトピック
	private TopicManager topicBackupManager = null; //自身が管理するバックアップ用トピック
	private LocalTopicManager localTopicManager = null; //自分のHDDに保存するトピック
	private TopicActivityManager topicActivityManager = null; //トピックの活性度を管理
	
	//DHTの管理
	private DHTManager dhtManager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	//デフォルト文字セット
	private String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	//トピックをファイルに保存するか
	private boolean SAVE_TOPIC = BBSConfiguration.SAVE_TOPIC_TO_LOCAL;
	
	//自身の管理するトピックをアップデート
	private TopicManageNodeUpdater topicUpdater = null;
	private TopicBackupNodeUpdater topicBackupUpdater = null;
	
	/**
	 * 
	 * @param initNodeAddress 
	 * @param statControllerAddress
	 * @param selfNodeAddress
	 * @param port
	 */
	public BBSManager(String initNodeAddress, String statControllerAddress,
			String selfNodeAddress, int port) throws InitDHTException {
		this.topicManager = new TopicManager();
		this.topicBackupManager = new TopicManager();
		this.localTopicManager = new LocalTopicManager();
		this.topicActivityManager = new TopicActivityManager();
		
		//DHT初期化
		this.dhtManager = new DHTManager();
		this.dhtManager.init(initNodeAddress, statControllerAddress, selfNodeAddress, port);
		
		this.topicUpdater = new TopicManageNodeUpdater(topicManager,
				dhtManager, topicActivityManager);
		this.topicBackupUpdater = new TopicBackupNodeUpdater(topicManager,
				topicBackupManager, dhtManager, topicActivityManager);
	}
	
	/**
	 * TopicBackup管理Managerにトピックの複製を追加
	 * @param categoryID
	 * @param topicID
	 * @param body
	 * @return
	 */
	public boolean backup(String categoryID, String topicID, String body) {
		try {
			//受信したデータをコメント単位に分解
			List<CommentElement> comments = CommentElementXMLParser
					.XMLToCommentElement("<topic>" + body + "</topic>");
			//受信したコメントをバックアップに追加
			for(CommentElement comment : comments ) {
				topicBackupManager.add(topicID, categoryID, comment);
			}
			return true;
		} catch (SAXParseException e) {
			e.printStackTrace();
		} catch (MaxNumberOfCommentException e) {
			e.printStackTrace();
		} catch (MaxLengthOfCommentException e) {
			e.printStackTrace();
		} catch (MaxLengthOfAuthorException e) {
			e.printStackTrace();
		} catch (MaxLengthOfMailException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * バックアップ管理ノードとなれるかどうか
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public boolean canBackup(String categoryID, String topicID) {
		return true;
	}
	
	/**
	 * トピック管理ノードになることができるかどうか
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public boolean canManage(String categoryID, String topicID) {
		//TODO: 自身が管理するノードが限界に達しているかどうかを調べる
		return true;
	}
	
	/**
	 * コメントの削除を行う
	 * @param categoryID
	 * @param topicID
	 * @param password
	 * @param commentNo　0が指定されるとトピック自体を削除
	 * @param first trueで始めに削除要求を受けたノード
	 * @return
	 */
	public boolean delete(String categoryID, String topicID, String password,
			int commentNo, boolean first) {
		//削除要求が正しく実行できたか
		boolean deleteAccept = true;
		try {
			//もし自身でトピックデータを保持していた場合
			if( first == true ) {
				//自分はトピックの管理者である．
				// DHTから管理ノード取得
				SortedMap<Long, String> manageNodes = this.dhtManager
						.getTTLValuePair(categoryID + topicID);
				// もっともTTLの大きなValueを取得
				// ぬるぽ対策
				if (manageNodes == null || manageNodes.size() == 0) {
					// manage node not found.
					//TODO:何か処理を入れる
				} else {
					// もっともTTLの大きなデータから管理ノード情報をとってくる
					String manageNodeInfo = manageNodes.get(manageNodes
							.firstKey());
					// 管理ノードのアドレス取得
					LinkedList<String> manageNodeAddress = BBSXMLParser
							.manageNodeXMLToIPAddress("<manage>"
									+ manageNodeInfo + "</manage>");
					// 自分自身のアドレス
					String myAddress = this.dhtManager.getSelfAddress().getAddress().getHostAddress()
							+ ":"
							+ this.dhtManager.getSelfAddress().getAddress().getPort();
					// データ削除
					for (String access : manageNodeAddress) {
						if( access.equals(myAddress) ) {
							//自分自身の時
							try {
								if (commentNo == 0 || commentNo == 1) {
									this.topicManager.remove(topicID,
											categoryID, password);
									this.topicBackupManager.remove(topicID,
											categoryID, password);
								} else {
									this.topicManager.fill(topicID, categoryID,
											commentNo, password);
									this.topicBackupManager.fill(topicID,
											categoryID, commentNo, password);
								}
							} catch (BadPasswordException e) {
								//パスワードが間違っていたとき
								deleteAccept = false;
							}
						}else {
							// 管理ノードに送信
							HttpClientSocket sock = new HttpClientSocket();
							String body = "category=" + categoryID + "&topic=" + topicID
								+ "&password=" + password + "&no=" + commentNo + "&first=0";
							try {
								String ret = sock.sendAndReceive("http://" + access
										+ "/command/delete/" ,"POST", body);
								if( ret.startsWith("DELETE_REJECT") ) {
									deleteAccept = false;
								}
							} catch (Exception e) {
								// 接続失敗 無視
							}
						}
					}
				}
			}else {
				//自分はトピックのバックアップを保持している
				//パスワードによる削除
				try {
					if (commentNo <= 0 || commentNo == 1) {
						this.topicManager.remove(topicID, categoryID, password);
						this.topicBackupManager.remove(topicID, categoryID,
								password);
					} else {
						this.topicManager.fill(topicID, categoryID, commentNo,
								password);
						this.topicBackupManager.fill(topicID, categoryID,
								commentNo, password);
					}
				} catch (BadPasswordException e) {
					deleteAccept = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteAccept;
	}
	
	/**
	 * 自身が管理しているトピック取得
	 * @param categoryID
	 * @param topicID
	 * @param commentStartNo
	 * @param commentEndNo
	 * @return トピックデータ
	 */
	public String get(String categoryID, String topicID, int commentStartNo,
			int commentEndNo) {
		// TopicManager, TopicBackupManagerで保持しているコメントを返す
		Set<CommentElement> comments = null;
		if (commentStartNo == 0 && commentEndNo == 0) {
			comments = topicBackupManager.get(topicID, categoryID);
			if (comments == null) {
				comments = topicManager.get(topicID, categoryID);
			}
		} else if (commentStartNo > 0 && commentEndNo == 0) {
			comments = topicBackupManager.get(topicID, categoryID, commentStartNo);
			if (comments == null) {
				comments = topicManager
						.get(topicID, categoryID, commentStartNo);
			}
		} else if (commentStartNo > 0 && commentEndNo > 0) {
			comments = topicBackupManager.get(topicID, categoryID, commentStartNo,
					commentEndNo);
			if (comments == null) {
				comments = topicManager.get(topicID, categoryID,
						commentStartNo, commentEndNo);
			}
		}

		String ret = null;
		if (comments == null) {
			ret = "<?xml version=\"1.0\" encoding=\""
				+ DEFAULT_ENCODING + "\" ?><topic>\n</topic>";
		} else {
			StringBuffer buf = new StringBuffer("<?xml version=\"1.0\" encoding=\""
				+ DEFAULT_ENCODING + "\" ?><topic>\n");
			for (CommentElement comment : comments) {
				buf.append(TopicManager.commentElementToXML(comment) + "\n");
			}
			buf.append("</topic>");
			ret = buf.toString();
			
			//トピック活性度を更新
			this.topicActivityManager.updateFromViewcount(categoryID, topicID, 1);
		}
		return ret;
	}
	
	/**
	 * ローカルで保持しているトピック一覧を返す
	 * @param categoryID
	 * @return
	 */
	public String getLocal(String categoryID) {
		String[] topics = this.localTopicManager.getTopics(categoryID);
		if( topics == null) {
			return "Not Found.";
		}
		StringBuffer buf = new StringBuffer("");
		for( String topic : topics ) {
			//トピックIDはかならず40文字 + 4文字
			if( topic.length() == 40 + 4) {
				buf.append( topic.substring(0, 40) + "\n");
			}
		}
		return buf.toString();
	}
	
	/**
	 * ローカルで保持しているトピック取得
	 * @param categoryID
	 * @param topicID
	 * @param commentStartNo
	 * @param commentEndNo
	 * @return
	 */
	public String getLocal(String categoryID, String topicID, int commentStartNo, int commentEndNo) {
		//localTopicManagerで管理しているトピック取得
		String ret = null;
		try {
			if (commentStartNo == 0 && commentEndNo == 0) {
				ret = localTopicManager.getComment(topicID, categoryID);
			} else if (commentStartNo > 0 && commentEndNo == 0) {
				ret = localTopicManager.getComment(topicID, categoryID,
						commentStartNo);
			} else if (commentStartNo > 0 && commentEndNo > 0) {
				ret = localTopicManager.getComment(topicID, categoryID,
						commentStartNo, commentEndNo);
			}
		} catch (CommentNotFoundException e) {
			ret = "";
		}

		ret = "<topic>\n" + ret + "</topic>";
		return ret;
	}
	
	/**
	 * have要求を受けた場合の処理
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public String have(String categoryID, String topicID) {
		int no = 0;
		//指定したトピックをバックアップしているか
		//管理している場合は，現在保持しているコメント数を取得
		no = this.topicBackupManager.size(topicID, categoryID);
		//トピックの活性度を取得
		int activity = this.topicActivityManager.getActivity(categoryID, topicID);
		if( no <= 0 ) {
			//指定したトピックを管理しているか
			no = this.topicManager.size(topicID, categoryID);
		}else {
			//バックアップ管理の場合は活性度を一度リセットする
			this.topicActivityManager.resetActivity(categoryID, topicID);
		}
		return "I_HAVE " + categoryID + " " + topicID + " " + no + " " + activity;
	}
	
	/**
	 * 自身がトピック管理ノードかどうか
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public boolean isManage(String categoryID, String topicID) {
		Set<Pair<String, String>> topics = topicManager.getTopics();
		if( topics == null ) {
			return false;
		}
		return topics.contains( new Pair<String, String>(topicID, categoryID) );
	}
	
	/**
	 * 自身がトピック管理ノードとなる
	 * @param categoryID
	 * @param topicID
	 * @param comment
	 * @return
	 * @throws CannotCreateTopicException 
	 * @throws MaxLengthOfMailException 
	 * @throws MaxLengthOfAuthorException 
	 * @throws MaxLengthOfCommentException 
	 * @throws MaxNumberOfCommentException 
	 */
	public void manage(String categoryID, String topicID, CommentElement comment)
	throws MaxNumberOfCommentException, MaxLengthOfCommentException, MaxLengthOfAuthorException, MaxLengthOfMailException, CannotCreateTopicException {
		//TODO: IDの決め方を考える
		String id = "testid";
		//トピック作成
		topicManager.createTopic(topicID, categoryID, comment.getAuthor(), comment.getMailAddress(),
				id, comment.getMessage(), comment.getTitle(), comment.getPassword());
		//トピック管理ノード情報アップデート
		topicUpdater.update(categoryID, topicID);
	}
	
	/**
	 * ping要求を受けた場合の処理
	 * @param address 要求元のアドレス
	 * @return
	 */
	public String ping(String address) {
		return "PONG " + address;
	}
	
	/**
	 * トピック一覧をネットワーク上から取得する
	 * @param categoryID
	 * @return
	 */
	public String read(String categoryID) {
		//返信用
		StringBuffer buf = new StringBuffer("<?xml version=\"1.0\" encoding=\""
				+ DEFAULT_ENCODING + "\" ?><root>");
		try {
			// DHTからトピック情報取得
			SortedMap<Long, String> topicInfo = this.dhtManager
					.getTTLValuePair(categoryID);
			// ぬるぽ対策
			if (topicInfo == null || topicInfo.size() == 0) {
				// manage node not found.
			} else {
				//トピック情報をXMLへと変換
				StringBuffer tempTopics = new StringBuffer("<root>");
				Set<Long> keys = topicInfo.keySet();
				
				for (Long key : keys) {
					String value = topicInfo.get(key);
					if (value != null) {
						tempTopics.append("<topic>" + value + "</topic>\n");
					}
				}
				tempTopics.append("</root>");
				//XMLデータからTopicInformationElementへと変換
				Set<TopicInformationElement> topics = 
					BBSXMLParser.topicInformationXMLToElement(tempTopics.toString(), categoryID);
				//重複トピックを削除して再びXMLへと戻す
				for(TopicInformationElement topic : topics) {
					buf.append("<topic><tid>" + topic.getTid() + "</tid>"
							+ "<title>" + topic.getTitle() + "</title>"
							+ "<num>" + topic.getNumOfComment() + "</num>"
							+ "<activity>" + topic.getActivity()
							+ "</activity></topic>\n");
				}
			}
		} catch (Exception e) {
			buf = new StringBuffer("Error.");
			e.printStackTrace();
		}
		buf.append("</root>");
		return buf.toString();
	}
	
	/**
	 * トピックをネットワーク上から取得する
	 * @param categoryID
	 * @param topicID
	 * @param commentStartNo
	 * @param commentEndNo
	 * @return
	 */
	public String read(String categoryID, String topicID, int commentStartNo, int commentEndNo) {
		//TODO:基本的にローカルIPからのみ要求受付
		// DHTからトピック管理ノード取得など取得
		String receive = "";
		try {
			//もし自身でトピックデータを保持していた場合
			if( this.topicManager.contains(topicID, categoryID) || 
					this.topicBackupManager.contains(topicID, categoryID) ) {
				//自身の保持するデータを返す
				receive = get(categoryID, topicID, commentStartNo, commentEndNo);
			}else {
				// DHTから管理ノード取得
				SortedMap<Long, String> manageNodes = this.dhtManager
						.getTTLValuePair(categoryID + topicID);
				// もっともTTLの大きなValueを取得
				// ぬるぽ対策
				if (manageNodes == null || manageNodes.size() == 0) {
					// manage node not found.
				} else {
					// もっともTTLの大きなデータから管理ノード情報をとってくる
					String manageNodeInfo = manageNodes.get(manageNodes
							.firstKey());
					// 管理ノードのアドレス取得
					LinkedList<String> manageNodeAddress = BBSXMLParser.manageNodeXMLToIPAddress("<manage>"
							+ manageNodeInfo + "</manage>");
					//ネットワーク上から現在存在するコメント数を取得
					String numOfExistCommentsStr = BBSXMLParser.manageNodeXMLToNumOfComments("<manage>"
							+ manageNodeInfo + "</manage>");
					//トピックのコメントデータ量を取得
					String topicCommentDataSizeStr = BBSXMLParser.manageNodeXMLToTopicMessageSize("<manage>"
							+ manageNodeInfo + "</manage>");
					int numOfExistComments = 0;
					int topicCommentDataSize = 0;
					try {
						if( numOfExistCommentsStr != null ) {
							numOfExistComments = Integer.parseInt(numOfExistCommentsStr);
						}
						if( topicCommentDataSizeStr != null ) {
							topicCommentDataSize = Integer.parseInt(topicCommentDataSizeStr);
						}
					}catch(NumberFormatException e) {
						e.printStackTrace();
					}
					//自身が現在保持しているコメント数
					int numOfLocalComments = this.localTopicManager.size(topicID, categoryID);
					//ネットワーク上に存在しているコメント数が自身の保持しているコメントより少ないとき
					if( numOfLocalComments < numOfExistComments ) {
						//他ノードへコメントを取得する際，どの番号のコメントから取得を行うか
						int getCommentStartNo = numOfLocalComments + 1;
						if( getCommentStartNo < commentStartNo ) {
							getCommentStartNo = commentStartNo;
						}
						// 管理ノード取得
						String manageNode = manageNodeAddress.get(0);
						manageNodeAddress.remove(0);
						// データ取得を行うノードをシャッフル
						Collections.shuffle(manageNodeAddress);
						// トピック管理ノードを一番最後に追加
						manageNodeAddress.add(manageNode);
						// データ取得
						for (String access : manageNodeAddress) {
							// 管理ノードに送信
							HttpClientSocket sock = new HttpClientSocket();
							try {
								receive = sock.sendAndReceive("http://" + access
										+ "/command/get/" + categoryID + "/"
										+ topicID + "/" + getCommentStartNo + "-"
										+ commentEndNo, "GET", "");
							} catch (Exception e) {
								// 接続失敗 無視
							}
							break;
						}
					}else if( numOfLocalComments == numOfExistComments) {
						//ネットワーク上に存在するコメント数とローカルにあるコメント数がまったく同じ時
						int localDataSize = localTopicManager.calcMessageDataSize(topicID, categoryID);
						//データのサイズが違うときは，データの削除があった可能性がある
						if( localDataSize != topicCommentDataSize) {
							// 管理ノード取得
							String manageNode = manageNodeAddress.get(0);
							manageNodeAddress.remove(0);
							// データ取得を行うノードをシャッフル
							Collections.shuffle(manageNodeAddress);
							// トピック管理ノードを一番最後に追加
							manageNodeAddress.add(manageNode);
							// データ取得
							for (String access : manageNodeAddress) {
								// 管理ノードに送信
								HttpClientSocket sock = new HttpClientSocket();
								try {
									receive = sock.sendAndReceive("http://" + access
											+ "/command/get/" + categoryID + "/"
											+ topicID + "/0-"
											+ commentEndNo, "GET", "");
								} catch (Exception e) {
									// 接続失敗 無視
								}
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ローカルにトピック保存
		if( receive.length() != 0 ) {
			//System.out.println("RECEIVE:" + receive);
			try {
				List<CommentElement> comments = CommentElementXMLParser
						.XMLToCommentElement(receive);
				for( CommentElement comment : comments ) {
					this.localTopicManager.addComment(topicID, categoryID, comment);
				}
				if( this.SAVE_TOPIC ) {
					//トピックをローカルに保存する
					this.localTopicManager.garbageCollection();
				}				
			} catch (SAXParseException e) {
				e.printStackTrace();
			}
		}
		
		String retValue = null;
		if( this.localTopicManager.size(topicID, categoryID) <= 0 ) {
			retValue = "Not Found.";
		}else {
			//ローカルからトピックデータを取得
			retValue = getLocal( categoryID, topicID, commentStartNo, commentEndNo);
		}
		return retValue;
	}
	
	/**
	 * ノードのログを返す
	 * @return
	 */
	public String status() {
		StringBuffer buf = new StringBuffer("");
		//日付表示方法
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/hh:mm:ss");
		//メッセージ一覧の取得と表示
		for(String name : this.monitorManager.getMessageNames()) {
			buf.append("MESSAGE:" + name + "\n");
			Set<MessageLog> logs = this.monitorManager.getMessages(name);
			for (MessageLog log : logs) {
				buf.append(log.getAddress() + "," + log.getType() + ","
						+ log.getSize() + ","
						+ dateFormat.format(log.getDate()) + "\n");
			}
			buf.append("\n");
		}
		return buf.toString();
	}

	/**
	 * ネットワーク上にあるトピックにコメントを書き込む
	 * 
	 * @param categoryID
	 * @param topicID
	 * @param comment
	 * @param password
	 * @return
	 * @throws TopicNotFoundException
	 * @throws MaxLengthOfMailException
	 * @throws MaxLengthOfAuthorException
	 * @throws MaxLengthOfCommentException
	 * @throws MaxNumberOfCommentException
	 */
	public boolean write(String categoryID, String topicID,
			CommentElement comment, String password)
			throws MaxNumberOfCommentException, MaxLengthOfCommentException,
			MaxLengthOfAuthorException, MaxLengthOfMailException,
			TopicNotFoundException {
		// 自分自身がトピック管理ノードだった場合
		if (isManage(categoryID, topicID)) {
			// コメント追加
			this.topicManager.add(topicID, categoryID, comment.getAuthor(),
					comment.getMailAddress(), comment.getId(), comment
							.getMessage(), comment.getPassword());
			// TODO:ここを入れるとうまく動かない たぶん同時に接続が起こるから
			// this.topicUpdater.update(categoryID, topicID);
			// 書き込み成功
			return true;
		} else {
			try {
				// DHTから管理ノード取得
				SortedMap<Long, String> manageNodes = this.dhtManager
						.getTTLValuePair(categoryID + topicID);
				// もっともTTLの大きなValueを取得
				// ぬるぽ対策
				if (manageNodes == null || manageNodes.size() == 0) {
					// manage node not found.
				} else {
					String manageNodeInfo = manageNodes.get(manageNodes
							.firstKey());
					// 管理ノードのアドレス取得
					String manageNodeAddress = BBSXMLParser
							.manageNodeXMLToIPAddress(
									"<manage>" + manageNodeInfo + "</manage>")
							.get(0);

					// 管理ノードにデータ書き込み
					// bodyを再び生成
					String body = "category="
							+ URLEncoder.encode(categoryID, DEFAULT_ENCODING)
							+ "&topic="
							+ URLEncoder.encode(topicID, DEFAULT_ENCODING)
							+ "&author="
							+ URLEncoder.encode(comment.getAuthor(),
									DEFAULT_ENCODING)
							+ "&mail="
							+ URLEncoder.encode(comment.getMailAddress(),
									DEFAULT_ENCODING)
							+ "&message="
							+ URLEncoder.encode(comment.getMessage(),
									DEFAULT_ENCODING) + "&password="
							+ URLEncoder.encode(password, DEFAULT_ENCODING)
							+ "\n";
					// 管理ノードに送信
					HttpClientSocket sock = new HttpClientSocket();
					String receive = sock.sendAndReceive("http://"
							+ manageNodeAddress + "/command/write/", "POST",
							body);
					// String receive =
					// sock.sendAndReceive("/command/write/", "127.0.0.1", 3997,
					// "POST", body);
					if (receive.startsWith("ACCEPT")) {
						return true;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}
}
