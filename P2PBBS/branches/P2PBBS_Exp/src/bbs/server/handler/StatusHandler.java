package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import bbs.manager.BBSManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * ノード情報取得要求の処理
 * @author nishio
 *
 */
public class StatusHandler implements HttpHandler{
	
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	
	public StatusHandler(BBSManager manager) {
		this.manager = manager;
	}
	
	public void handle(HttpExchange he) throws IOException {
		//要求元のIP
		String remoteAddress = he.getRemoteAddress().getAddress().getHostAddress();
		//要求元ポート
		int remotePort = he.getRemoteAddress().getPort();
		
		logger.info("Status request from " + remoteAddress + ":" + remotePort);
		//返信用BODY
		OutputStream os = he.getResponseBody();
		//ステータス
		byte[] statusData = manager.status().getBytes();

		//response 200 OK
		he.sendResponseHeaders(200, statusData.length);
		os.write(statusData);
		
		//接続を切る
		os.close();
	}
}