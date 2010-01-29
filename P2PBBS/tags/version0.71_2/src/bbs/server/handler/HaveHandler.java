package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Have要求（指定したトピックを保持しているかどうか）の処理
 * @author nishio
 *
 */
public class HaveHandler implements HttpHandler {
	
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	
	public HaveHandler(BBSManager manager) {
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
		if( splitStr.length == 5 ) {
			//正しい要求
			String categoryID = splitStr[3];
			String topicID = splitStr[4];
			
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), path.length(),
					"HAVE");
			
			//レスポンス
			String res = manager.have(categoryID, topicID);
			//response 200 OK
			he.sendResponseHeaders(200, res.getBytes().length);
			os.write(res.getBytes());
			
			//返信データログ記録
			monitorManager.addMessageLog("TOPIC_SEND",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), res.getBytes().length,
					"HAVE");
			
			logger.info("Have request from " + he.getRemoteAddress().getAddress().getHostAddress() +
					" category: " + categoryID + " topic: " + topicID);
		}else {
			//間違った要求 400 Bad Request
			byte[] res = "Bad have Request.".getBytes();
			he.sendResponseHeaders(400, res.length);
			os.write(res);
			
			logger.info("Bad have Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}
		
		//接続を切る
		os.close();
	}
}
