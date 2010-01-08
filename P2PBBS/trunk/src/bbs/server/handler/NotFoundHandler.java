package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * 要求URLが存在しない場合
 * @author Nishio
 *
 */
public class NotFoundHandler implements HttpHandler{
	
	private Logger logger = Logger.getLogger("bbs.server.handler");
	
	public NotFoundHandler() {
	}
	
	public void handle(HttpExchange he) throws IOException {
		logger.log(Level.INFO, "Request Data Not Found from "+ he.getRemoteAddress().getAddress().getHostAddress() + 
				" Request Method: " + he.getRequestMethod() + " URI: " + he.getRequestURI() );

		//返信用BODY
		OutputStream os = he.getResponseBody();

		//response 400 Bad Request
		byte[] badRequest = "Bad Request.".getBytes();
		he.sendResponseHeaders(400, badRequest.length);
		os.write(badRequest);
		
		//接続を切る
		os.close();
	}
}

