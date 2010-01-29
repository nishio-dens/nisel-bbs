package bbs.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import ow.util.concurrent.ConcurrentNonBlockingThreadPoolExecutor;

import bbs.client.handler.ClientReadHandler;
import bbs.dht.InitDHTException;
import bbs.manager.BBSManager;
import bbs.server.handler.BackupHandler;
import bbs.server.handler.CanBackupHandler;
import bbs.server.handler.CanManageHandler;
import bbs.server.handler.DeleteHandler;
import bbs.server.handler.GetHandler;
import bbs.server.handler.GetLocalHandler;
import bbs.server.handler.HaveHandler;
import bbs.server.handler.IsManageHandler;
import bbs.server.handler.ManageHandler;
import bbs.server.handler.NotFoundHandler;
import bbs.server.handler.PingHandler;
import bbs.server.handler.ReadHandler;
import bbs.server.handler.StatusHandler;
import bbs.server.handler.WriteHandler;

import com.sun.net.httpserver.HttpServer;

/**
 * 
 * @author nishio
 *
 */
public class BBSServer {
	//HTTPサーバ
	private HttpServer server;
	//BBSManager
	private BBSManager manager;

	public BBSServer() {
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
	public void start(String initNodeAddress, String statControllerAddress,
			String selfNodeAddress, int dhtPort, int serverPort) throws InitDHTException, IOException {
			this.manager = new BBSManager(initNodeAddress, statControllerAddress, selfNodeAddress, dhtPort);
			this.server = HttpServer.create(new InetSocketAddress( serverPort ), 0);

			//待ちうけハンドラーを設定
			server.createContext("/", new NotFoundHandler());
			//ping
			server.createContext("/command/ping", new PingHandler(manager));
			//have
			server.createContext("/command/have/", new HaveHandler(manager));
			//get
			server.createContext("/command/get/", new GetHandler(manager));
			//read
			server.createContext("/command/read/", new ReadHandler(manager));
			//getlocal
			server.createContext("/command/getlocal/", new GetLocalHandler(manager));
			//canbackup
			server.createContext("/command/canbackup/", new CanBackupHandler(manager));
			//backup
			server.createContext("/command/backup/", new BackupHandler(manager));
			//write
			server.createContext("/command/write/", new WriteHandler(manager));
			//canmanage
			server.createContext("/command/canmanage/", new CanManageHandler(manager));
			//ismanage
			server.createContext("/command/ismanage/", new IsManageHandler(manager));
			//manage
			server.createContext("/command/manage/", new ManageHandler(manager));
			//status
			server.createContext("/command/status", new StatusHandler(manager));
			//delete
			server.createContext("/command/delete", new DeleteHandler(manager));
			
			//クライアント機能
			//トピック一覧，及びトピック取得
			server.createContext("/read/", new ClientReadHandler(manager, serverPort) );
			
			//executor
			server.setExecutor(new ConcurrentNonBlockingThreadPoolExecutor(0, 10,
					3L, TimeUnit.SECONDS,
					new DaemonThreadFactory()));
			
			//サーバ起動
			server.start();
	}
	
	/**
	 * 
	 * @author nishio
	 *
	 */
	private final static class DaemonThreadFactory implements ThreadFactory {
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("deamonThread");
			t.setDaemon(true);

			return t;
		}
	}
}
