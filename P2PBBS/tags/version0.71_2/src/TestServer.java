import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import ow.util.concurrent.*;

/**
 * 
 * @author nishio
 *
 */
public class TestServer {
	//HTTPサーバ
	private HttpServer server;

	public TestServer() {
		this.server = null;
	}

	/**
	 * サーバ初期化
	 * @param initNodeAddress 初期ノード
	 * @param statControllerAddress ネットワーク状態を送信するアドレス
	 * @param selfNodeAddress 自身のアドレス
	 * @param dhtPort DHT待ち受けポート（UDP)
	 * @param serverPort サーバ待ち受けポート（TCP)
	 * @throws InitDHTException 
	 * @throws IOException 
	 */
	public void start() throws  IOException {
			this.server = HttpServer.create(new InetSocketAddress( 8080 ), 0);

			//待ちうけハンドラーを設定
			server.createContext("/", new NotFoundHandler());
			//ping
			server.createContext("/test1", new TestHandler());
			/*server.setExecutor(new ConcurrentBlockingThreadPoolExecutor(0, 10,
					2L, TimeUnit.SECONDS,
					new DaemonThreadFactory()));*/ 
			server.setExecutor(new ConcurrentNonBlockingThreadPoolExecutor(0, 10,
					2L, TimeUnit.SECONDS,
					new DaemonThreadFactory()));//best
			//サーバ起動
			server.start();
	}
	
	private class NotFoundHandler implements HttpHandler{		
		public void handle(HttpExchange he) throws IOException {
			OutputStream os = he.getResponseBody();
			
			//やや重い処理
			LinkedList<String> test = new LinkedList<String>();
			for(int i=0; i < 30000; i++) {
				test.add("AAAAAA");
			}
			System.out.println("NOT FOUND HANDLER ADD OK");
			for(int i=0; i < 30000; i++) {
				test.get(i);
			}
			System.out.println("NOT FOUND HANDLER GET OK");
			byte[] res = "NOTFOUND".getBytes();
			//response 200 OK
			he.sendResponseHeaders(200, res.length);
			os.write( res );

			//接続を切る
			os.close();
		}
	}

	private class TestHandler implements HttpHandler{
	
		public void handle(HttpExchange he) throws IOException {
			OutputStream os = he.getResponseBody();
			
			//重い処理
			LinkedList<String> test = new LinkedList<String>();
			for(int i=0; i < 100000; i++) {
				test.add("AAAAAA");
			}
			System.out.println("TEST HANDLER ADD OK");
			for(int i=0; i < 100000; i++) {
				test.get(i);
			}
			System.out.println("TEST HANDLER GET OK");
			byte[] res = "TESTTEST".getBytes();
			//response 200 OK
			he.sendResponseHeaders(200, res.length);
			os.write( res );

			//接続を切る
			os.close();
		}
	}
	
	private final static class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("TEST");
			t.setDaemon(true);

			return t;
		}
	}

}
