package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class IsManageHandler implements HttpHandler {
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	
	public IsManageHandler(BBSManager manager) {
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
			
			//返信データログ記録
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), 0,
					"ISMANAGE");
			
			//レスポンス
			boolean res = manager.isManage(categoryID, topicID);
			byte[] response = null;
			if( res ) {
				response = "YES".getBytes();
			}else {
				response = "NO".getBytes();
			}
			//response 200 OK
			he.sendResponseHeaders(200, response.length);
			os.write(response);
			
			//返信データログ記録
			monitorManager.addMessageLog("TOPIC_SEND",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), response.length,
					"ISMANAGE");
			
			logger.info("ismanage request from " + he.getRemoteAddress().getAddress().getHostAddress() +
					" category: " + categoryID + " topic: " + topicID);
		}else {
			//間違った要求 400 Bad Request
			he.sendResponseHeaders(400, 0);
			os.write("Bad ismanage Request.".getBytes());
			
			logger.info("Bad ismanage Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}
		
		//接続を切る
		os.close();
	}
}
