package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.manager.BBSManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class CanManageHandler implements HttpHandler {
	
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	
	public CanManageHandler(BBSManager manager) {
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
			//レスポンス
			boolean res = manager.canManage(categoryID, topicID);
			//response 200 OK
			he.sendResponseHeaders(200, "AAAAAA".getBytes().length);
			if( res ) {
				os.write("ACCEPT".getBytes());
			}else {
				os.write("REJECT".getBytes());
			}
			logger.info("canmanage request from " + he.getRemoteAddress().getAddress().getHostAddress() +
					" category: " + categoryID + " topic: " + topicID);
		}else {
			//間違った要求 400 Bad Request
			byte[] res = "Bad canmanage Request.".getBytes();
			he.sendResponseHeaders(400, res.length);
			os.write(res);
			
			logger.info("Bad canmanage Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}
		
		//接続を切る
		os.close();
	}
}
