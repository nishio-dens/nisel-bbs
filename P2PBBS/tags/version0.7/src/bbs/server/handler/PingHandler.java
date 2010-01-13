package bbs.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * ping要求の処理
 * @author nishio
 *
 */
public class PingHandler implements HttpHandler{
	
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();
	
	public PingHandler(BBSManager manager) {
		this.manager = manager;
	}
	
	public void handle(HttpExchange he) throws IOException {
		//要求元のIP
		String remoteAddress = he.getRemoteAddress().getAddress().getHostAddress();
		//要求元ポート
		int remotePort = he.getRemoteAddress().getPort();
		
		logger.info("Ping request from " + remoteAddress + ":" + remotePort);
		//返信用BODY
		OutputStream os = he.getResponseBody();

		byte[] res = manager.ping(remoteAddress).getBytes();
		//response 200 OK
		he.sendResponseHeaders(200, res.length);
		os.write( res );
		
		//受信データログ記録
		monitorManager.addMessageLog("TOPIC_RECEIVE",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), 0,
				"PING");
		//送信データログ記録
		monitorManager.addMessageLog("TOPIC_SEND",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), res.length,
				"PING");
		
		//接続を切る
		os.close();
	}
}
