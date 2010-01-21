package bbs.manager;

import java.io.IOException;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import ow.id.IDAddressPair;
import ow.messaging.MessagingAddress;
import bbs.comment.CommentElement;
import bbs.comment.TopicManager;
import bbs.dht.DHTManager;
import bbs.http.HttpClientSocket;
import bbs.monitor.MonitorManager;
import bbs.util.HTMLEncode;
import bbs.util.Pair;
import bbs.xml.BBSXMLParser;

/**
 * トピックバックアップ管理ノードの更新作業
 * @author nishio
 *
 */
public class TopicBackupNodeUpdater implements Runnable {
	private TopicManager topicManager = null;
	private TopicManager topicBackupManager = null;
	private DHTManager dhtManager = null;
	private TopicActivityManager topicActivityManager = null;

	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	
	// トピック情報更新間隔
	private long topicUpdateInterval = 30 * 1000/* 3 * 1000*/;
	//retry回数
	private int numOfRetry = 2;
	//トピック管理ノードの情報 key=categoryID+topicID, value=IPaddress
	private Map<String, List<String>> manageNodeAddress;

	// 自身のスレッド
	private Thread thread = null;

	private static final Logger logger = Logger.getLogger("bbs.topic");

	/**
	 * 
	 * @param topicManager
	 * @param topicBackupManager
	 * @param dhtManager
	 */
	public TopicBackupNodeUpdater(TopicManager topicManager,
			TopicManager topicBackupManager, DHTManager dhtManager, TopicActivityManager topicActivityManager) {
		this.topicManager = topicManager;
		this.topicBackupManager = topicBackupManager;
		this.dhtManager = dhtManager;
		this.topicActivityManager = topicActivityManager;
		
		this.manageNodeAddress = new HashMap<String, List<String>>();

		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		for (;;) {
			try {
				Set<Pair<String, String>> topics = this.topicBackupManager.getTopics();
				//トピック管理ノード情報更新
				for(Pair<String, String> topic : topics) {
					updateManageNodeInfo( topic.getSecond(), topic.getFirst() );
					update(topic.getSecond(), topic.getFirst());
				}
				// しばらく休憩
				//TODO: トピック管理ノード情報の更新間隔と，バックアップデータ送信間隔を別のものにする？
				Thread.sleep(this.topicUpdateInterval);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * トピックを保持しているノードの情報を更新
	 * @param categoryID
	 * @param topicID
	 */
	public void updateManageNodeInfo(String categoryID, String topicID) {
		//DHTから管理ノード取得
		SortedMap<Long, String> manageNodes = null;
		try {
			//DHTからトピックを保持しているノード情報を取得
			manageNodes = this.dhtManager
					.getTTLValuePair(categoryID + topicID);
			//ぬるぽ対策
			if( manageNodes == null || manageNodes.size() == 0) {
				//nothing
			}else {
				//一番TTLの大きい情報を使う
				String manageNodeInfo = manageNodes.get( manageNodes.firstKey() );
				//トピック管理ノード
				List<String> address = BBSXMLParser.manageNodeXMLToIPAddress(
						"<manage>" + manageNodeInfo + "</manage>");
				if( address != null ) {
					manageNodeAddress.put(categoryID + topicID, address);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * トピック活性度をDHTから取得
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public String getActivityFromDHT(String categoryID, String topicID) {
		//DHTから管理ノード取得
		String activity = null;
		//DHTから管理ノード取得
		SortedMap<Long, String> manageNodes = null;
		try {
			//DHTからトピックを保持しているノード情報を取得
			manageNodes = this.dhtManager
					.getTTLValuePair(categoryID + topicID);
			//ぬるぽ対策
			if( manageNodes == null || manageNodes.size() == 0) {
				//nothing
			}else {
				//一番TTLの大きい情報を使う
				String manageNodeInfo = manageNodes.get( manageNodes.firstKey() );
				//トピック管理ノード
				activity = BBSXMLParser.manageNodeXMLToActivity(
						"<manage>" + manageNodeInfo + "</manage>");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if( activity == null ) {
			activity = "0";
		}
		return activity;
	}

	/**
	 * 指定したトピックの更新作業を行う．
	 * もしトピック管理ノードがネットワーク上にいなかったら自分が管理ノードとなる
	 * 
	 * @param categoryID
	 * @param topicID
	 */
	public void update(String categoryID, String topicID) {
		//トピックを管理しているノードアドレス
		List<String> manageNodes = this.manageNodeAddress.get(categoryID + topicID);
		if( manageNodes != null ) {
			// 自分のアドレスを取得
			String selfAddress = this.dhtManager.getSelfAddress().getAddress().getHostAddress()
					+ ":"
					+ this.dhtManager.getSelfAddress().getAddress().getPort();
			for(String node : manageNodes) {
				if( node.equals(selfAddress) ) {
					//トピック管理ノードは自分自身
					//昇格処理
					promote(categoryID, topicID);
					break;
				}
				boolean ping = false;
				// トピック管理ノードにPINGを送る
				for(int i=0; i < numOfRetry; i++) {
					try {
						HttpClientSocket sock = new HttpClientSocket();
						String url = "http://" + node + "/command/ping";
						
						//送信データログ記録
						monitorManager.addMessageLog("TOPIC_SEND", node, 0, "PING");
						//ping送信
						sock.sendAndReceive(url, "GET", "");
						//送信データログ記録
						monitorManager.addMessageLog("TOPIC_RECEIVE", node, 0, "PING");
						ping = true; //ping成功
						break;
					} catch (UnknownHostException e) {
						//無視
						logger.info("トピック管理ノード:" + node + "から応答がありませんでした．");
					} catch (IOException e) {
						//無視
						logger.info("トピック管理ノード:" + node + "から応答がありませんでした．");
					}
				}
				//pingが失敗したら，次の管理ノード候補へとpingを送る
				if( ping == false ) {
					continue;
				}else {
					break;
				}
			}
		}
	}
	
	/**
	 * バックアップ管理ノードからトピック管理ノードへと昇格
	 * @param categoryID
	 * @param topicID
	 */
	public void promote(String categoryID, String topicID) {
		SortedSet<CommentElement> comments = this.topicBackupManager.get(topicID, categoryID);
		if( comments != null ) {
			try{
				//自身が管理ノードへと昇格
				for( CommentElement comment : comments ) {
					this.topicManager.add(topicID, categoryID, comment);
				}
				//バックアップ管理ノードではなくなる
				this.topicBackupManager.remove(topicID, categoryID);
				//トピックの活性度をDHTから取得し設定
				String activity = this.getActivityFromDHT(categoryID, topicID);
				Integer act = Integer.parseInt(activity);
				this.topicActivityManager.updateFromActivity(categoryID, topicID, act);
			}catch(Exception e) {
				e.printStackTrace();
			}
			logger.info("カテゴリ:" + categoryID + " トピック:" + topicID + "の管理ノードとなります．");
		}
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
