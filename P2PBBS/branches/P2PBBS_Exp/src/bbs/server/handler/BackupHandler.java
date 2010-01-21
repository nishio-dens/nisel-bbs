package bbs.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.BBSConfiguration;
import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class BackupHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	
	private static final String DEFAULT_CHARACTER_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	
	public BackupHandler(BBSManager manager) {
		this.manager = manager;
	}
	
	public void handle(HttpExchange he) throws IOException {
		//返信用BODY
		OutputStream os = null;
		//要求URI
		String path = he.getRequestURI().getPath();
		//delim = /
		String[] splitStr = path.split("/");
		//返信メッセージ
		byte[] responseBody = null;
		int responseCode = 200;
		
		//command backup カテゴリ識別子 トピック識別子へと分解
		if( splitStr.length == 5 ) {
			//正しい要求
			String categoryID = splitStr[3];
			String topicID = splitStr[4];
			//要求取得
			InputStream is = he.getRequestBody();
			//入力ストリームを取得
			BufferedReader in = new BufferedReader( 
					new InputStreamReader(is, DEFAULT_CHARACTER_ENCODING ) );

			//データ読み取り
			String line;
			StringBuffer buf = new StringBuffer();
			while ((line = in.readLine()) != null) {
				buf.append(line);
			}
			//System.out.println("RESPONSE:" + buf.toString());
			//レスポンス
			boolean res = manager.backup(categoryID, topicID, buf.toString());
			//受信データログ記録
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), buf.length(),
					"BACKUP");

			if( res ) {
				responseBody = "ACCEPT".getBytes();
			}else {
				responseBody = "REJECT".getBytes();
			}
			
			logger.info("backup request from " + he.getRemoteAddress().getAddress().getHostAddress() +
					" category: " + categoryID + " topic: " + topicID);
		}else {
			//間違った要求 400 Bad Request
			//he.sendResponseHeaders(400, 0);
			responseCode = 400;
			responseBody = "Bad backup Request.".getBytes();
			
			logger.info("Bad backup Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}
		
		// response Header
		he.sendResponseHeaders(responseCode, responseBody.length);
		// body 送信
		os = he.getResponseBody();
		if (responseBody != null) {
			os.write(responseBody);
		}
		
		//送信データログ記録
		monitorManager.addMessageLog("TOPIC_SEND",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), responseBody.length,
				"BACKUP");

		// 接続を切る
		os.close();
	}
}
