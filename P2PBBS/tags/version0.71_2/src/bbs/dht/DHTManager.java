package bbs.dht;

import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Logger;

import ow.dht.DHT;
import ow.dht.DHTConfiguration;
import ow.dht.DHTFactory;
import ow.dht.ValueInfo;
import ow.id.ID;
import ow.id.IDAddressPair;
import ow.id.IDUtility;
import ow.messaging.Message;
import ow.messaging.MessageHandler;
import ow.messaging.Signature;
import ow.messaging.Tag;
import ow.messaging.util.MessagingUtility;
import ow.routing.RoutingAlgorithmConfiguration;
import ow.routing.RoutingAlgorithmFactory;
import ow.routing.RoutingAlgorithmProvider;
import ow.routing.RoutingException;
import ow.routing.RoutingService;
import ow.routing.RoutingServiceFactory;
import ow.routing.RoutingServiceProvider;
import ow.stat.MessagingCollector;
import ow.stat.StatConfiguration;
import ow.stat.StatFactory;

/**
 * DHTの操作を行います.
 * @author nishio
 *
 */
public class DHTManager {
	//ルーティングスタイル
	private static final String ROUTING_STYLE = "Iterative";
	//ルーティングアルゴリズム
	private static final String ROUTING_ALGORITHM = "Chord";
	//トランスポート
	private static final String TRANSPORT = "UDP";
	//データ保存TTL
	private final static long DEFAULT_TTL = 1 * 24 * 60 * 60 * 1000;	// 1 day
	//DHT
	private DHT<String> dht = null;
	//DHT設定データ
	private DHTConfiguration config = null;
	//ApplicationVersion
	private static final short APPLICATION_VERSION = (short)0x10000;
	//DHTログ
	private static final Logger logger = Logger.getLogger( "bbs.dht" );
	//CyberLink UPnPを利用するか
	private boolean cyberLink = false;
	
	/**
	 * DHTを初期化
	 * @param initNodeAddress 初期ノードアドレス
	 * @param statControllerAddress ステータス送信先アドレス
	 * @param selfNodeAddress 自分のノードのアドレス
	 * @param port DHT待ち受けポート
	 * @throws InitDHTException 
	 */
	public void init(String initNodeAddress, String statControllerAddress,
			String selfNodeAddress, int port) throws InitDHTException {
		//DHTの設定
		config = DHTFactory.getDefaultConfiguration();
		config.setRoutingStyle( ROUTING_STYLE );
		config.setRoutingAlgorithm( ROUTING_ALGORITHM );
		config.setSelfPort( port );
		config.setMessagingTransport( TRANSPORT );
		config.setDoUPnPNATTraversal(cyberLink);
		
		if( selfNodeAddress != null ) {
			String d[] = selfNodeAddress.split(":");
			if( d[0] != null ) {
				config.setSelfAddress( d[0] );
			}
			if( d.length == 2 && d[1] != null ) {
				try {
					config.setSelfPort( Integer.parseInt(d[1]) );
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		//メッセージピープここから
		/*//RoutingService routingSvc = null;
		StatConfiguration statConf = StatFactory.getDefaultConfiguration();
		//通信方式
		statConf.setMessagingTransport(config.getMessagingTransport());
		
		MessagingUtility.HostAndPort hostAndPort = 
			MessagingUtility.parseHostnameAndPort(config.getSelfAddress(), config.getSelfPort());
		statConf.setSelfAddress(hostAndPort.getHostName());
		statConf.setSelfPort(hostAndPort.getPort());
		//UPnP
		statConf.setDoUPnPNATTraversal(false);
		try {
			MessagingCollector messagingCollector = 
				StatFactory.getMessagingCollector(statConf);
			//フックしたメッセージの処理
			messagingCollector.getMessageReceiver().addHandler(
					new MessageHandler() {
						public Message process(Message msg)
						throws Exception {
							//showReceivedMessage(msg);
							System.out.println("Message Handler(TAG :" +
									Tag.getNameByNumber(msg.getTag()) + ")");
							Serializable[] s = msg.getContents();
							for(Serializable m : s) {
								if( m != null && m.toString() != null ) {
									System.out.println("message : " + m.toString());
								}
							}
							if( msg.getSource() != null ) {
								System.out.println( "source :" + msg.getSource() );
							}
							return null;
						}
					});
			//RoutingService作成
			RoutingServiceProvider svcProvider = 
				RoutingServiceFactory.getProvider( config.getRoutingStyle() );
			RoutingAlgorithmProvider algoProvider = 
				RoutingAlgorithmFactory.getProvider( config.getRoutingAlgorithm() );
			routingSvc = svcProvider.getService(svcProvider.getDefaultConfiguration(),
					messagingCollector.getMessagingProvider(),
					messagingCollector.getMessageReceiver(),
					algoProvider, algoProvider.getDefaultConfiguration(), dht.getSelfIDAddressPair().getID());
			// アルゴリズム作成。
			RoutingAlgorithmConfiguration algoConfig = algoProvider.getDefaultConfiguration();
			algoProvider.initializeAlgorithmInstance(algoConfig, routingSvc);
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		//メッセージピープここまで

		//int usePort = PORT; //実際に使用したポート
		//PORT から  PORT + PORT_RANGE の間であいているポートを探す
		//for(int i=0; ; i++) {
		try {
			//DHT生成 application versionを設定しないと正しく通信できない？
			dht = DHTFactory.getDHT(Signature.APPLICATION_ID_DHT_SHELL,
					APPLICATION_VERSION,
					config);
			//dht = DHTFactory.getDHT(config);
		}
		catch (Exception e) {
			/*StringBuilder sb = new StringBuilder();
			sb.append("初期化前設定:\n");
			sb.append("  hostname:port:     ").append(config.getSelfAddress()+":"+config.getSelfPort()).append('\n');
			sb.append("  transport type:    ").append(config.getMessagingTransport()).append('\n');
			sb.append("  routing algorithm: ").append(config.getRoutingAlgorithm()).append('\n');
			sb.append("  routing style:     ").append(config.getRoutingStyle()).append('\n');
			sb.append("  directory type:    ").append(config.getDirectoryType()).append('\n');
			sb.append("  working directory: ").append(config.getWorkingDirectory()).append('\n');
			//System.out.print(sb);
			logger.info( sb.toString() );*/
			//System.err.println("DHT生成に失敗しました． port :" + config.getContactPort());
			//e.printStackTrace();

			//return false;
			//config.setSelfPort( PORT + i );
			//if( i == PORT_RANGE ) {
			//System.err.println("DHT生成に失敗しました.");
			e.printStackTrace();
			logger.severe("DHT生成に失敗しました.");
			throw new InitDHTException("DHT初期化に失敗しました.");
			//}
			//}finally{
			//	if( dht == null ) {
			//		continue;
			//	}
			//	usePort = PORT + i;
			//	break;
		}
		//}

		//初期ノードが指定されていた場合
		if ( initNodeAddress != null ) {
			try {
				dht.joinOverlay( initNodeAddress, config.getSelfPort());
			}catch (IOException e) {
				logger.warning("オーバーレイネットワークへ参加できませんでした．");
				e.printStackTrace();
				throw new InitDHTException("オーバーレイネットワークに参加できませんでした．");
			}catch (Exception e) {
				logger.warning("オーバーレイネットワークへ参加できませんでした．");
				e.printStackTrace();
				throw new InitDHTException("オーバーレイネットワークに参加できませんでした．");
			}
		}
		logger.info("DHT初期化成功.");

		StringBuilder sb = new StringBuilder();
		sb.append("DHT configuration:\n");
		sb.append("  hostname:port:     ").append(dht.getSelfIDAddressPair().getAddress()).append('\n');
		sb.append("  transport type:    ").append(config.getMessagingTransport()).append('\n');
		sb.append("  routing algorithm: ").append(config.getRoutingAlgorithm()).append('\n');
		sb.append("  routing style:     ").append(config.getRoutingStyle()).append('\n');
		sb.append("  directory type:    ").append(config.getDirectoryType()).append('\n');
		sb.append("  working directory: ").append(config.getWorkingDirectory()).append('\n');
		//System.out.print(sb);
		logger.info( sb.toString() );

		/*//stat controller 自分のステータスを特定のアドレスに送るか？
		if( statControllerAddress != null ) {
			StatConfiguration statConfig = StatFactory.getDefaultConfiguration();
			// provides the default port number of stat collector

			MessagingUtility.HostAndPort hostAndPort =
				MessagingUtility.parseHostnameAndPort(
						statControllerAddress, statConfig.getSelfPort());

			try {
				dht.setStatCollectorAddress(hostAndPort.getHostName(), hostAndPort.getPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}*/
		//dht.getRoutingService().getMessagingProvider().setSelfAddress("0.0.0.0");
		//時間待ち
		try { Thread.sleep(2000); } catch (InterruptedException e) {}
		//TTLセット
		dht.setTTLForPut(DEFAULT_TTL);
	}
	
	/**
	 * DHT上にkey/valueを配置
	 * @param key
	 * @param value
	 * @param ttl
	 * @throws IOException データが投入できなかった場合にIOExceptionを投げる
	 */
	public void put(String key, String value, long ttl) throws IOException {
		if( dht == null ) {
			throw new NullPointerException("DHTが初期化されていません.");
		}
		//ルーティングアルゴリズムが何ビットで構成されているか
		int idSize = dht.getRoutingAlgorithmConfiguration().getIDSizeInByte();

		try {
			//TTLをセット
			dht.setTTLForPut(ttl);
			//keyIDの取得
			//ID key = ID.getHashcodeBasedID( keyStr , idSize);
			ID keyID = IDUtility.parseID(key, idSize);
			//DHTに投入
			dht.put(keyID, value);
		}catch (Exception e) {
			throw new IOException("DHT上にデータをPUTできませんでした．");
		}
	}
	
	/**
	 * DHT上から値を取得する
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public Set<String> get(String key) throws IOException {
		//ルーティングアルゴリズムが何ビットで構成されているか
		int idSize = dht.getRoutingAlgorithmConfiguration().getIDSizeInByte();
		Set<String> values = null;
		try{
			//キー
			ID keyID = IDUtility.parseID( key, idSize);
			//dhtからキーに対する値を取得
			Set<ValueInfo<String>> valueSet = dht.get(keyID);
			//データが発見できた場合
			if (valueSet != null && !valueSet.isEmpty()) {
				for (ValueInfo<String> s: valueSet) {
					if( values == null ) {
						values = new HashSet<String>();
					}
					values.add( s.getValue() );
				}
			}
		}catch (RoutingException e) {
			throw new IOException("Routing failed. DHTから値を取得できませんでした．");
		}
		return values;
	}
	
	/**
	 * DHT上からTTLとVALUEのペアを取得
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public SortedMap<Long, String> getTTLValuePair(String key) throws IOException {
		//ルーティングアルゴリズムが何ビットで構成されているか
		int idSize = dht.getRoutingAlgorithmConfiguration().getIDSizeInByte();
		SortedMap<Long, String> values = null;
		try{
			//キー
			ID keyID = IDUtility.parseID( key, idSize);
			//dhtからキーに対する値を取得
			Set<ValueInfo<String>> valueSet = dht.get(keyID);
			
			//データが発見できた場合
			if (valueSet != null && !valueSet.isEmpty()) {
				for (ValueInfo<String> s: valueSet) {
					if( values == null ) {
						//降順
						values = new TreeMap<Long, String>(new Comparator<Long>(){
							@Override
							public int compare(Long o1, Long o2) {
								return o1.compareTo(o2) * -1;
							}					
						});
					}
					values.put(s.getTTL(), s.getValue());
				}
			}
		}catch (RoutingException e) {
			throw new IOException("Routing failed. DHTから値を取得できませんでした．");
		}
		return values;
	}
	
	/**
	 * 自身のサクセッサノードを取得
	 * @return
	 */
	public IDAddressPair[] getSuccessor() {
		//自分のサクセッサを取得 (succ[0]は自分自身なので注意）
		IDAddressPair[] succ = dht.getRoutingService().getRoutingAlgorithm().rootCandidates(
				dht.getSelfIDAddressPair().getID(), 20);
		return succ;
	}
	
	/**
	 * 自身のアドレス取得
	 * @return
	 */
	public IDAddressPair getSelfAddress() {
		IDAddressPair[] succ = dht.getRoutingService().getRoutingAlgorithm().rootCandidates(
				dht.getSelfIDAddressPair().getID(), 1);
		return succ[0];
	}
	
}
