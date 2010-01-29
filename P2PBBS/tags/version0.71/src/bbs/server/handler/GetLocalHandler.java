package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.BBSConfiguration;
import bbs.manager.BBSManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class GetLocalHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//文字コード
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	
	public GetLocalHandler(BBSManager manager) {
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
					response = manager.getLocal(categoryID, topicID, start, end);
				}else {
					throw new Exception("Bad Request.");
				}

				//response 200 OK
				he.sendResponseHeaders(200, response.getBytes( DEFAULT_ENCODING ).length);
				os.write(response.getBytes( DEFAULT_ENCODING ));

				logger.info("getlocal request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID + " topic: " + topicID );
			}else if( splitStr.length == 4 ) {
				//正しい要求
				String categoryID = splitStr[3];
				String response = null;
		
				response = manager.getLocal(categoryID);

				//response 200 OK
				he.sendResponseHeaders(200, response.getBytes( DEFAULT_ENCODING ).length);
				os.write(response.getBytes());

				logger.info("getlocal request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID);
			}else {
				throw new Exception("Bad Request.");
			}
		}catch(Exception e) {
			//間違った要求 400 Bad Request
			byte[] res = "Bad getlocal Request.".getBytes();
			he.sendResponseHeaders(400, res.length);
			os.write(res);
			
			logger.info("Bad getlocal Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}finally {
			//接続を切る
			os.close();
		}
	}
}
