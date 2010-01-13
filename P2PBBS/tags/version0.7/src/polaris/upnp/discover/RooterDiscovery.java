package polaris.upnp.discover;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import polaris.upnp.RooterInformation;

/**
 * マルチキャストを利用してルータの情報を取得する
 * @author nishio
 *
 */
public class RooterDiscovery implements Runnable{
	//ルータ発見マルチキャスト先
	private static final String discoveryAddress = "239.255.255.250";
	//ルータから情報を受け取るポート defaultでは19100とする
	private int sourcePort = 19100;

	//ルータ発見に利用するメッセージ1
	private static final String rooterDiscoveryMessage1 =
		"M-SEARCH * HTTP/1.1\r\n" +
		"HOST: 239.255.255.250:1900\r\n" +
		"MAN: \"ssdp:discover\"\r\n" +
		"MX: 3\r\n" +
		"ST: urn:schemas-upnp-org:service:WANPPPConnection:1\r\n" + //search target
		//"ST: upnp:rootdevice\r\n" +
		"\r\n";
	//ルータ発見に利用するメッセージ2
	private static final String rooterDiscoveryMessage2 =
		"M-SEARCH * HTTP/1.1\r\n" +
		"HOST: 239.255.255.250:1900\r\n" +
		"MAN: \"ssdp:discover\"\r\n" +
		"MX: 3\r\n" +
		"ST: urn:schemas-upnp-org:service:WANIPConnection:1\r\n" +
		"\r\n";
//	//ネットワークから離脱する際に送信するマルチキャストメッセージ
//	private static final String byebyeMessage =
//		"NOTIFY * HTTP/1.1\r\n" +
//		"HOST: 239.255.255.250:1900\r\n" +
//		"MX: 3\r\n" +
//		"NTS: \"ssdp:byebye\"\r\n" +
//		"\r\n";
	//SSDP port番号
	private static final int ssdpPort = 1900;
	//ルータ情報受信タイムアウト時間
	private int discoveryTimeout = 10 * 1000; //10秒
	//ルータ情報受信失敗したかどうか
	private boolean error = false;
	//エラーの情報
	private String errorString = null;
	//ルータ情報取得に利用するスレッド
	private Thread informationGetterThread = null;
	//情報通知リスナー
	private List<RooterDiscoveryListener> listeners = new ArrayList<RooterDiscoveryListener>();
	//ルータ発見リトライ回数
	private int numDiscoverRetry = 3;
	//ルータ情報
	private RooterInformation rooterInfo = null;
	
	private static Logger logger = Logger.getLogger("polaris.upnp");

	/**
	 * ルータのイベントが変化した際にお知らせするリスナーを登録
	 * @param listener
	 */
	public void addRooterDiscoveryListener(RooterDiscoveryListener listener) {
		listeners.add( listener );
	}

	/**
	 * ルータの情報取得開始
	 */
	public void discover() {
		informationGetterThread = new Thread( this );
		informationGetterThread.start();
	}

	/**
	 * エラーが発生している際はエラーメッセージを返す．そうでない場合はnullを返す．
	 * @return
	 */
	public String getErrorInformation() {
		if( this.error ) {
			//エラーが発生しているときは，エラー内容を返す
			return this.errorString;
		}
		return null;
	}

	/**
	 * ルータの情報を取得します．もし未取得の場合はnullを返します．
	 * @return
	 */
	public RooterInformation getRooterInformation() {
		return this.rooterInfo;
	}

	/**
	 * エラーが発生しているかどうか
	 * @return
	 */
	public boolean isError() {
		return this.error;
	}
	
	/**
	 * ルータからパケットを受け取る際に利用するポート番号を設定
	 * @param port
	 */
	public void setSourcePort(int port) {
		this.sourcePort = port;
	}
	
	/**
	 * ルータからパケットを受け取る際に利用するポート番号
	 * @return
	 */
	public int getSourcePort() {
		return this.sourcePort;
	}

	@Override
	public void run() {
		try {
			int numOfTry = 0; //ルータ送信パケット送信回数
			this.rooterInfo = null; //ルータ情報は未受信状態
			removeErrorInformation(); //エラー情報削除

			InetAddress group = InetAddress.getByName( discoveryAddress );
			MulticastSocket socket = new MulticastSocket( sourcePort );
			//マルチキャストグループに参加
			socket.joinGroup( group );
			//タイムアウト時間をセット
			socket.setSoTimeout( discoveryTimeout );
			//送信データ
			DatagramPacket discoverPacket1 = new DatagramPacket(rooterDiscoveryMessage1.getBytes(),
					rooterDiscoveryMessage1.length(), group, ssdpPort);
			DatagramPacket discoverPacket2 = new DatagramPacket(rooterDiscoveryMessage2.getBytes(),
					rooterDiscoveryMessage2.length(), group, ssdpPort);

			for( numOfTry = 0; numOfTry < numDiscoverRetry; numOfTry++ ) {
				//ルータ発見用パケット送信
				socket.send(discoverPacket1);
				socket.send(discoverPacket2);

				byte[] receiveData = new byte[10000];
				//データ受信
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

				try {
					socket.receive( receivePacket );
				}catch( SocketTimeoutException e) {
					//タイムアウトになったら
					continue;
				}

				String receiveStr = new String(receiveData);
				if( receiveStr.startsWith("HTTP/1.1 200 OK") || receiveStr.startsWith("HTTP/1.0 200 OK")) {
					//受信成功
					//System.out.println("RECEIVE OK.\r\n" + receiveStr);
					logger.info( "情報取得パケット: " + receiveStr );
					//パケットからルータ情報を取得
					rooterInfo = getRooterInformation(receiveStr);
					
					break;
				}else {
					//受信失敗，もう一度パケット送信
					continue;
				}			
			}
			//byebyeメッセージをマルチキャスト
//			DatagramPacket byebyePacket = new DatagramPacket(byebyeMessage.getBytes(),
//					byebyeMessage.length(), group, ssdpPort);
//			socket.send( byebyePacket );
			//マルチキャストグループから離脱
			socket.leaveGroup( group );

			if( rooterInfo == null || numOfTry == numDiscoverRetry ) {
				//発見に失敗
				setErrorInformation(numOfTry + "回ルータ発見用パケットを送信しましたが，応答がありませんでした．");
			}

		} catch (UnknownHostException e) {
			//指定したグループが見つからなかった時
			setErrorInformation("指定したマルチキャストグループが見つかりませんでした.\n" + e.getLocalizedMessage() );
			e.printStackTrace();
		} catch (IOException e) {
			//ソケットが開けなかった時
			setErrorInformation("ソケットがオープンできませんでした.\n" + e.getLocalizedMessage() );
			e.printStackTrace();
		} finally{
			//ルータ情報通知
			notifyRooterDiscovery();
		}
	}

	/**
	 * 受信パケットからルータ情報を抜き出します
	 * @param packetData
	 * @return
	 */
	private RooterInformation getRooterInformation(String packetData) {
		//データを改行ごとに分割
		String[] elements = packetData.split("\r\n");
		RooterInformation info = new RooterInformation();

		if( elements != null ) {
			for(String element : elements) {
				//さらに要素を": "で2分割
				String[] keyValue = element.split(":", 2);
				if( keyValue != null && keyValue.length == 2) {
					//keyをすべて大文字に直す
					String key = keyValue[0].toUpperCase();
					String value = keyValue[1].trim();

					if( key.startsWith("LOCATION") ) {
						info.setLocation(value);
					}else if( key.startsWith("ST") ) {
						info.setSearchTarget(value);
					}else if( key.startsWith("USN") ) {
						info.setUniqueServiceName(value);
					}
				}
			}
		}

		return info;
	}

	/**
	 * ルータの情報に変化があった場合に通知する
	 */
	private void notifyRooterDiscovery() {
		for(RooterDiscoveryListener listener : listeners ) {
			listener.rooterInformationChanged( new RooterDiscoveryEvent(this) );
		}
	}

	/**
	 * エラー情報を削除
	 */
	private void removeErrorInformation() {
		this.error = false;
		this.errorString = null;
	}

	/**
	 * エラー情報をセットする
	 * @param message
	 */
	private void setErrorInformation(String message) {
		this.error = true;
		this.errorString = message;
	}
}
