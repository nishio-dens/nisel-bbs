package bbs.monitor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import bbs.BBSConfiguration;
import bbs.comment.CommentElement;
import bbs.util.MultiSortedMap;
import bbs.util.Pair;

/**
 * 
 * @author nishio
 *
 */
public class MonitorManager {
	//自分自身
	private static MonitorManager monitorManager = null;
	//通信量のログを管理
	private List<MessageLog> messageLog =
		new LinkedList<MessageLog>();
	//ログ番号
	private static int logNum = 0;
	//ログの名前
	private static String logFilename = "";
	//ログ最大保存件数
	private static int maxTraffic = BBSConfiguration.NUM_OF_MAX_TRAFFIC_LOG;
	//ログ保存先ディレクトリ
	private static String logDirectory = BBSConfiguration.TRAFFIC_LOG_DIRECTORY;
	//ログをローカルに保存するか
	private static boolean saveLog = BBSConfiguration.SAVE_TRAFFIC_TO_LOCAL;
	
	//Singleton
	private MonitorManager() {
	}
	
	/**
	 * モニタ用クラスを取得
	 * @return
	 */
	public static MonitorManager getMonitorManager() {
		if( monitorManager == null ) {
			monitorManager = new MonitorManager();
			logNum = 0;
			logFilename = "LOG" + new Date().getTime();
		}
		return monitorManager;
	}
	
	/**
	 * 通信メッセージ保存
	 * @param messageName 通信メッセージ名
	 * @param address 受信相手または送信相手のアドレス
	 * @param messageSize
	 */
	public void addMessageLog(String messageName, String address, int messageSize) {
		messageLog.add(new MessageLog(messageName, address, messageSize,
				new Date()) );
		//ログをファイルに保存してメモリから削除
		if( messageLog.size() > maxTraffic ) {
			this.removeHalfMessageFromMemory( saveLog );
		}
	}
	
	/**
	 * 
	 * @param messageName
	 * @param address
	 * @param messageSize
	 * @param type
	 */
	public void addMessageLog(String messageName, String address, int messageSize, String type) {
		messageLog.add(new MessageLog(messageName, address, messageSize,
				new Date(),type) );
		//ログをファイルに保存してメモリから削除
		if( messageLog.size() > maxTraffic ) {
			this.removeHalfMessageFromMemory( saveLog );
		}
	}
	
	/**
	 * 通信メッセージ取得
	 * @return
	 */
	public List<MessageLog> getMessages() {
		return this.messageLog;
	}
	
	/**
	 * 通信メッセージ取得
	 * @param messageName
	 * @return
	 */
	public Set<MessageLog> getMessages(String messageName) {
		Set<MessageLog> logs = new TreeSet<MessageLog>();
		for( MessageLog log : this.messageLog ) {
			if( log.getMessageName().equals(messageName) ) {
				logs.add(log);
			}
		}
		return logs;
	}
	
	/**
	 * 指定した時刻内にある通信メッセージを取得
	 * @param messageName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public Set<MessageLog> getMessages(String messageName, Date startDate, Date endDate) {
		Set<MessageLog> logs = getMessages(messageName);
		Set<MessageLog> retLogs = null;
		if( logs != null ) {
			for(MessageLog log : logs ) {
				Date d = log.getDate();
				//ログの記録時間がstartDateより後でendDateより前か
				if( d.after(startDate) && d.before(endDate) ) {
					if( retLogs == null ) {
						retLogs = new TreeSet<MessageLog>();
					}
					retLogs.add(log);
				}
			}
		}
		return retLogs;
	}
	
	/**
	 * 指定した時間から現在までのログを取得
	 * @param messageName
	 * @param startDate
	 * @return
	 */
	public Set<MessageLog> getMessages(String messageName, Date startDate) {
		return getMessages(messageName, startDate, new Date() );
	}
	
	/**
	 * 指定された時刻の通信量を取得
	 * @param messageName
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public long getMessageSize(String messageName, Date startDate, Date endDate) {
		Set<MessageLog> logs = getMessages(messageName, startDate, endDate);
		long messageSize = 0;
		
		for(MessageLog log : logs ) {
			messageSize += log.getSize();
		}
		return messageSize;
	}
	
	/**
	 * 指定した時刻からの通信量を取得
	 * @param messageName
	 * @param startDate
	 * @return
	 */
	public long getMessageSize(String messageName, Date startDate) {
		return getMessageSize(messageName, startDate);
	}
	
	/**
	 * すべての通信量を取得
	 * @param messageName
	 * @return
	 */
	public long getMessageSize(String messageName) {
		return getMessageSize(messageName, new Date(0), new Date() );
	}
	
	/**
	 * 保存された通信メッセージ名一覧を取得
	 * @return
	 */
	public Set<String> getMessageNames() {
		Set<String> keys = new HashSet<String>();
		for( MessageLog log : messageLog) {
			keys.add( log.getMessageName() );
		}
		return keys;
	}
	
	/**
	 * メモリからログの半分を削除
	 * @param fileOutput ファイルへログを出力するかどうか
	 */
	private void removeHalfMessageFromMemory(boolean fileOutput) {
		// ログの半分の量
		int half = messageLog.size() / 2;
		// ログディレクトリを作成
		if (fileOutput) {
			try {
				File logDir = new File("./" + logDirectory);
				if (!logDir.exists()) {
					// ディレクトリが存在しないので作成する
					if (logDir.mkdir() == false) {
						throw new IOException(logDirectory
								+ "ディレクトリを作成できませんでした．");
					}
				}
				// ログの初めの半分を保持する
				List<MessageLog> logs = new LinkedList<MessageLog>( messageLog.subList(0, half));
				// ファイル名 ./logFilename + logNum.log
				String filename = "./" + logDirectory + "/" + logFilename
						+ ".log." + logNum;
				//ファイル出力用
				FileOutputStream fos = new FileOutputStream(filename);
				OutputStreamWriter osw = new OutputStreamWriter(fos);
				BufferedWriter bw = new BufferedWriter(osw);
				// ファイル書き込みデータ
				StringBuffer buf = new StringBuffer("");
				// 日付表示方法
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd/hh:mm:ss");

				for (MessageLog log : logs) {
					buf.append(log.getMessageName() + "," + log.getAddress()
							+ "," + log.getType() + "," + log.getSize() + ","
							+ dateFormat.format(log.getDate()) + "\n");
				}
				// ファイルへ保存
				bw.write(buf.toString());
				bw.close();
				osw.close();
				fos.close();
				// ログ番号を一つあげる
				logNum += 1;
			} catch (Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
		// ログの半分を保持する
		messageLog = new LinkedList<MessageLog>( messageLog.subList(half, messageLog.size() ) );
	}
	
}
