package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.BBSConfiguration;
import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ReadHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	//文字コード
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	
	public ReadHandler(BBSManager manager) {
		this.manager = manager;
	}
	
	public void handle(HttpExchange he) throws IOException {
		//返信用BODY
		OutputStream os = he.getResponseBody();
		//要求URI
		String path = he.getRequestURI().getPath();
		//delim = /
		String[] splitStr = path.split("/");
		
		//command have カテゴリ識別子 トピック識別子へと分解
		try {
			//受信データログ記録
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), path.length(),
					"READ");
			
			if( splitStr.length == 6 ) {
				//正しい要求
				String categoryID = splitStr[3];
				String topicID = splitStr[4];
				String commentNo = splitStr[5];
				//コメント番号
				String[] splitStr2 = commentNo.split("-");
				//レスポンス
				String response = null;
				if( splitStr2.length == 2 ) {
					int start = Integer.parseInt( splitStr2[0] );
					int end = Integer.parseInt( splitStr2[1] );				
					response = manager.read(categoryID, topicID, start, end);
				}else {
					throw new Exception("Bad Request.");
				}

				//response 200 OK
				he.sendResponseHeaders(200, response.getBytes( DEFAULT_ENCODING ).length);
				os.write(response.getBytes( DEFAULT_ENCODING ));
				
				//送信データログ記録
				monitorManager.addMessageLog("TOPIC_SEND",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), response.getBytes( DEFAULT_ENCODING ).length,
						"READ");

				logger.info("read request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID + " topic: " + topicID );
			}else if( splitStr.length == 4 ) {
				//正しい要求
				String categoryID = splitStr[3];
				String response = null;
		
				response = manager.read(categoryID);

				//response 200 OK
				he.sendResponseHeaders(200, response.getBytes( DEFAULT_ENCODING ).length);
				os.write(response.getBytes( DEFAULT_ENCODING ));
				
				//送信データログ記録
				monitorManager.addMessageLog("TOPIC_SEND",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), response.getBytes( DEFAULT_ENCODING ).length,
						"READ");

				logger.info("read request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID);
			}else {
				throw new Exception("Bad Request.");
			}
		}catch(Exception e) {
			//間違った要求 400 Bad Request
			byte[] res = "Bad read Request.".getBytes( DEFAULT_ENCODING );
			he.sendResponseHeaders(400, res.length);
			os.write(res.length);
			
			logger.info("Bad read Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}finally {
			//接続を切る
			os.close();
		}
	}
}
