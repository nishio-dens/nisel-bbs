package bbs.gui;

import java.awt.Desktop;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import bbs.http.HttpClientSocket;
import bbs.server.BBSServer;

/**
 * GUI部分の実装
 * @author nishio
 *
 */
public class GUIManager {
	private TrayIcon trayIcon = null;
	//UPnP機能
	private UPnPGUIManager upnpManager = null;
	//P2PBBS
	private BBSServer server = new BBSServer();
	//メイン画面
	private JFrame mainFrame = null;
	//情報表示
	private JLabel informationLabel = null;
	//P2PBBS起動タスク
	private Thread startTaskThread = null;
	private StartTask startTask = null;

	/**
	 * P2PBBS起動タスク
	 * @author nishio
	 *
	 */
	private class StartTask implements Runnable {

		private String initNodeAddress = null;
		private String selfAddress = null;
		private String selfPort = null;
		private boolean upnp = false;

		/**
		 *
		 * @param initNodeAddress
		 * @param selfAddress
		 * @param selfPort
		 * @param upnp
		 */
		public StartTask(String initNodeAddress, String selfAddress, String selfPort, boolean upnp) {
			this.initNodeAddress = initNodeAddress;
			this.selfAddress = selfAddress;
			this.selfPort = selfPort;
			this.upnp = upnp;
		}

		@Override
		public void run() {
			int port = 3997;

			if( initNodeAddress.length() <= 1) {
				initNodeAddress = null;
			}
			if( selfAddress.length() <= 1) {
				selfAddress = null;
			}
			try{
				port = Integer.parseInt(selfPort);
			}catch(Exception e2) {
				port = 3997;
			}
			//エラー削除しておく
			if( upnpManager != null ) {
				upnpManager.setError(false);
			}

			//UPnP関係の処理
			if( upnp ) {
				if( upnpManager == null ) {
					upnpManager = new UPnPGUIManager(informationLabel);
				}
				//ルータ情報取得開始
				upnpManager.discover();
				while( true ) {
					if( upnpManager.isFinishDiscover() || upnpManager.isError() ) {
						break;
					}
					//busy wait回避
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if( !upnpManager.isError() ) {
					//正常にルータ情報が取得できた場合のみポートマッピング
					upnpManager.addPortMapping(port, "TCP", port, selfAddress, "P2PBBS");
					upnpManager.addPortMapping(port, "UDP", port, selfAddress, "P2PBBS");
				}
				while( true ) {
					if( upnpManager.isFinishAddPortMapping() || upnpManager.isError() ) {
						break;
					}
					//busy wait回避
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			if( upnpManager != null && upnpManager.isError() ) {
				JOptionPane.showMessageDialog(null, "UPnPエラーが発生しました．"+
						"UPnP機能は利用できません．", "UPnP Error",
						JOptionPane.ERROR_MESSAGE);
				//メイン画面表示
				mainFrame.setVisible(true);
				return;
			}

			try {
				//グローバルアドレスの取得
				String globalAddress = getGlobalSelfAddress(selfAddress, initNodeAddress, upnp);
				if (globalAddress == null) {
					JOptionPane.showMessageDialog(null,
							"自身のIPアドレスがローカルIPアドレスです．ローカルIPでは外部との通信はできません．",
							"Self Address Error", JOptionPane.ERROR_MESSAGE);
				}
				//IPに不正文字が含まれていたら削除
				globalAddress = globalAddress.replace("\r", "");
				globalAddress = globalAddress.replace("\n", "");
				//P2PBBS起動
				server.start(initNodeAddress, null, globalAddress, port, port);
				trayIcon.displayMessage("接続成功", "DHTネットワークへの接続に成功しました．",
						TrayIcon.MessageType.INFO);
				//ブラウザ起動
				try {
					Desktop desktop = Desktop.getDesktop();			 
					desktop.browse(new URI("http://localhost:" + port + "/read/"));
				}catch(Exception e2) {
					JOptionPane.showMessageDialog(null, "ブラウザが起動できませんでした．\n" +
							"ブラウザを起動してhttp://localhost:" +
							port + "/read/ へとアクセスしてください．", "Browser Not Found",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, "エラーが発生しました．" + e2, "Error",
						JOptionPane.ERROR_MESSAGE);
				e2.printStackTrace();
				//メイン画面表示
				mainFrame.setVisible(true);
			}
		}

		/**
		 * ネットワーク上で利用する自分のアドレスを取得
		 * @param selfAddress
		 * @param initNodeAddress 初期ノードアドレス
		 * @param upnp UPnPを利用するか
		 * @return nullの場合アドレスを正しく取得できなかった
		 * @throws UnknownHostException
		 */
		private String getGlobalSelfAddress(String selfAddress,
				String initNodeAddress, boolean upnp) throws UnknownHostException {
			//セルフアドレス
			String address = null;

			// 自分のアドレスがグローバルIPアドレスの場合
			if (isGlobalAddress(selfAddress)) {
				// そのままIPアドレスを利用
				address = selfAddress;
			} else {
				//自分のアドレスがプライベートアドレスの場合

				// 接続先初期ノードがいるか？
				if (initNodeAddress != null) {
					// 接続先が存在
					// pingを使ってグローバルIP取得
					HttpClientSocket sock = new HttpClientSocket();
					String url = "http://" + initNodeAddress + "/command/ping";
					try {
						String pong = sock.sendAndReceive(url, "GET", "");
						//返信してきた値は「PONG IPアドレス」のはず
						String[] ret = pong.split(" ");
						// selfAddressをグローバルIPとする
						address = ret[1];
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					if( upnp ) {
						//UPnPを利用している場合
						address = upnpManager.getExternalAddress();
					}
				}
			}
			return address;
		}

		/**
		 * 指定したアドレスがグローバルアドレスかどうか
		 * @param address
		 * @return
		 * @throws UnknownHostException
		 */
		private boolean isGlobalAddress(String address) throws UnknownHostException {
			boolean isLocal = false;
			InetAddress ia = InetAddress.getByName(address);
			isLocal = ia.isAnyLocalAddress();
			isLocal |= ia.isLinkLocalAddress();
			isLocal |= ia.isLoopbackAddress();
			isLocal |= ia.isMulticastAddress();
			isLocal |= ia.isSiteLocalAddress();
			return !isLocal;
		}

		/**
		 * ポートマッピング削除
		 */
		public void deletePortMapping() {
			int port = 3997;
			if( upnpManager != null && upnpManager.isFinishAddPortMapping() && !upnpManager.isError() ) {
				try{
					port = Integer.parseInt(selfPort);
				}catch(Exception e2) {
					port = 3997;
				}
				upnpManager.deletePortMapping(port, "TCP");
				upnpManager.deletePortMapping(port, "UDP");
				while( true ) {
					if( upnpManager.isFinishDeletePortMapping() || upnpManager.isError() ) {
						break;
					}
					//busy wait回避
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 *
	 * @param trayIcon
	 * @param frame
	 * @param informationLabel
	 */
	public GUIManager(TrayIcon trayIcon, JFrame frame, JLabel informationLabel) {
		this.trayIcon = trayIcon;
		this.mainFrame = frame;
		this.informationLabel = informationLabel;
	}

	/**
	 * 起動開始
	 * @param initNodeAddress
	 * @param selfAddress
	 * @param selfPort
	 * @param upnp
	 * @return
	 */
	public void start(String initNodeAddress, String selfAddress, String selfPort, boolean upnp) {
		this.startTask = new StartTask(initNodeAddress, selfAddress, selfPort, upnp);
		startTaskThread = new Thread( startTask );
		startTaskThread.start();
		//メイン画面を見えなくする
		mainFrame.setVisible(false);
	}

	/**
	 * 終了するときに呼び出す
	 */
	public void stop() {
		if( this.startTask != null) {
			this.startTask.deletePortMapping();
		}
	}

}
