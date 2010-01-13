package polaris.upnp.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import polaris.upnp.RooterPortMappingEntry;
import polaris.upnp.device.RooterDeviceServiceElement;
import polaris.upnp.http.HttpClientSocket;

/**
 * ルータが利用可能な制御をルータにリクエストします
 * @author nishio
 *
 */
public class RooterControl {

	//ルータ制御アドレス
	private String rooterControlAddress = null;
	private List<RooterDeviceServiceElement> rooterServices = null;
	private int timeoutTime = 5 * 1000; //5秒でタイムアウト
	
	private static Logger logger = Logger.getLogger("polaris.upnp");
	
	/**
	 * 
	 * @param rooterControlAddress ルータの制御アドレス 例) http://192.168.1.1:2869
	 * @param rooterServices ルータが利用可能なサービス
	 */
	public RooterControl( String rooterControlAddress, List<RooterDeviceServiceElement> rooterServices ) {
		this.rooterControlAddress = rooterControlAddress;
		this.rooterServices = rooterServices;
	}
	
	/**
	 * 外部アドレスを取得する
	 * @return
	 */
	public String getExternalIPAddress(){
		//TODO: M-POSTに対応させる
		//外部アドレス
		String externalAddressData = null;
		
		for(RooterDeviceServiceElement service : rooterServices ) {
			//サービスのコントロールアドレス
			String serviceControlAddress = getControlAddress(service);
			
			try{
				/*//アクション送信先アドレス
				URL actionURL = new URL( serviceControlAddress );
				//コネクション開始
				HttpURLConnection connection = (HttpURLConnection)actionURL.openConnection();
				//POST要求
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				//タイムアウト時間を指定
				connection.setReadTimeout(timeoutTime);

				//SOAPヘッダ GetExternalIPAddress要求
				connection.setRequestProperty("SOAPAction", service.getServiceType() + "#" + "GetExternalIPAddress");
				OutputStreamWriter osw = new OutputStreamWriter( connection.getOutputStream() );
				//GetExternalIPAddress命令のbody
				String body = createGetExternalIPAddressSoapBody( service.getServiceType() );
				//命令送信
				osw.write(body);
				osw.flush();
				osw.close();

				//レスポンスを受け取る
				BufferedReader isr = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				StringBuffer buf = new StringBuffer("");
				String str;

				while( (str = isr.readLine() ) != null ) {
					buf.append(str);
				}
				//接続切断
				isr.close();
				connection.disconnect();
				//レスポンスデータ受け取り
				externalAddressData = buf.toString();*/
				
				//接続先のホスト名，ポート番号，パスを取得
				URL rooterURL = new URL( serviceControlAddress );
				String host = rooterURL.getHost();
				int port = rooterURL.getPort();
				String path = rooterURL.getPath();
				if( rooterURL.getQuery() != null ) {
					path = path + "?" + rooterURL.getQuery();
				}
				
				logger.info("外部アドレス取得アクション.要求先ホスト: " + host + " 要求先ポート: " + port + " パス: " + path);
				
				//HTTP通信用ソケット
				HttpClientSocket socket = new HttpClientSocket();
				socket.addHeader("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
				socket.addHeader("SOAPACTION", "\"" + service.getServiceType() + "#" + "GetExternalIPAddress\"");
				
				//GetExternalIPAddress命令のbody
				String body = createGetExternalIPAddressSoapBody( service.getServiceType() );
				//受信データ
				externalAddressData = socket.sendAndReceive(path, host, port, "POST", body);
				logger.info("外部アドレス情報取得完了．\n" + externalAddressData);
				break;
			}catch(IOException e) {
				e.printStackTrace();
				continue;
			}
		}
		return getNewExternalIPAddress(externalAddressData);
	}
	
	/**
	 * 指定したインデクスにあるUPnPエントリーを取得
	 * @param index
	 * @return
	 */
	public RooterPortMappingEntry getGenericPortMappingEntry(int index) {
		String entry = null;
		RooterPortMappingEntry retEntry = null;
		
		for(RooterDeviceServiceElement service : rooterServices ) {
			//サービスのコントロールアドレス
			String serviceControlAddress = getControlAddress(service);
			
			try{
				/*//アクション送信先アドレス
				URL actionURL = new URL( serviceControlAddress );
				//コネクション開始
				HttpURLConnection connection = (HttpURLConnection)actionURL.openConnection();
				//POST要求
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				//タイムアウト時間を指定
				connection.setReadTimeout(timeoutTime);

				//SOAPヘッダ GetExternalIPAddress要求
				connection.setRequestProperty("SOAPAction", service.getServiceType() + "#" + "GetGenericPortMappingEntry");
				OutputStreamWriter osw = new OutputStreamWriter( connection.getOutputStream() );
				//GetExternalIPAddress命令のbody
				String body = createGetGenericPortMappingEntry(service.getServiceType(), index );
				//命令送信
				osw.write(body);
				osw.flush();
				osw.close();

				//レスポンスを受け取る
				BufferedReader isr = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				StringBuffer buf = new StringBuffer("");
				String str;

				while( (str = isr.readLine() ) != null ) {
					buf.append(str);
				}
				//接続切断
				isr.close();
				connection.disconnect();
				//レスポンスデータ受け取り
				entry = buf.toString();
				break;*/
				
				//接続先のホスト名，ポート番号，パスを取得
				URL rooterURL = new URL( serviceControlAddress );
				String host = rooterURL.getHost();
				int port = rooterURL.getPort();
				String path = rooterURL.getPath();
				if( rooterURL.getQuery() != null ) {
					path = path + "?" + rooterURL.getQuery();
				}
				
				logger.info("UPnPエントリー取得アクション.要求先ホスト: " + host + " 要求先ポート: " + port + " パス: " + path);
				
				//HTTP通信用ソケット
				HttpClientSocket socket = new HttpClientSocket();
				socket.addHeader("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
				socket.addHeader("SOAPACTION", "\"" + service.getServiceType() + "#" + "GetGenericPortMappingEntry\"");
				
				//GetGenericPortMappingEntry命令のbody
				String body = createGetGenericPortMappingEntry(service.getServiceType(), index );
				//受信データ
				entry = socket.sendAndReceive(path, host, port, "POST", body);
				logger.info("エントリー情報取得完了.\n" + entry);
				break;
			}catch(IOException e) {
				//e.printStackTrace();
				continue;
			}
		}
		if( entry != null ) {
			retEntry = xmlToPortMappingEntry(entry);
			//ただしい情報が取得できなかったとき
			if( retEntry.getNewExternalPort() == null || retEntry.getNewInternalClient() == null ) {
				retEntry = null;
			}
		}
		return retEntry;
	}
	
	/**
	 * XMLデータをポートマッピングエントリーに変換
	 * @param xml
	 * @return
	 */
	private RooterPortMappingEntry xmlToPortMappingEntry(String xml) {
		String newRemoteHost = getEntry(xml, "NewRemoteHost");
		String newExternalPort = getEntry(xml, "NewExternalPort");
		String newProtocol = getEntry(xml, "NewProtocol");
		String newInternalPort = getEntry(xml, "NewInternalPort");
		String newInternalClient = getEntry(xml, "NewInternalClient");
		String newEnabled =getEntry(xml, "NewEnabled");
		String newPortMappingDescription = getEntry(xml, "NewPortMappingDescription");
		String newLeaseDuration = getEntry(xml, "NewLeaseDuration");
		return new RooterPortMappingEntry(newRemoteHost, newExternalPort, newProtocol,
				newInternalPort, newInternalClient, newEnabled,
				newPortMappingDescription, newLeaseDuration);
	}
	
	/**
	 * ポートマッピングを行う
	 * @param newExternalPort 外部ポート
	 * @param newProtocol プロトコル UDPかTCP
	 * @param newInternalPort 内部ポート
	 * @param newInternalClient ポートマッピングを行うローカルIPアドレス
	 * @param portMappingDescription ポートマッピングの概要
	 * @return ポートマッピングに成功したかどうか
	 */
	public boolean addPortMapping(int newExternalPort, String newProtocol, int newInternalPort,
			String newInternalClient, String portMappingDescription ) {
		//ポートマッピングにエラーが発生したかどうか
		boolean portMappingError = true;
		//ポートマッピングアクションのレスポンス
		String receiveData = null;
		
		for(RooterDeviceServiceElement service : rooterServices ) {
			//サービスのコントロールアドレス
			String serviceControlAddress = getControlAddress(service);
			
			try{
				/*//アクション送信先アドレス
				URL actionURL = new URL( serviceControlAddress );
				//コネクション開始
				HttpURLConnection connection = (HttpURLConnection)actionURL.openConnection();
				//POST要求
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				//タイムアウト時間を指定
				connection.setReadTimeout(timeoutTime);

				//SOAPヘッダ AddPortMapping要求
				connection.setRequestProperty("SOAPAction", service.getServiceType() + "#" + "AddPortMapping");
				OutputStreamWriter osw = new OutputStreamWriter( connection.getOutputStream() );
				//AddPortMapping命令のbody
				String body = createAddPortMappingSoapBody(service.getServiceType(),
						newExternalPort, newProtocol, newInternalPort, newInternalClient, portMappingDescription );
				//命令送信
				osw.write(body);
				osw.flush();
				osw.close();

				//レスポンスを受け取る
				BufferedReader isr = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				StringBuffer buf = new StringBuffer("");
				String str;

				while( (str = isr.readLine() ) != null ) {
					buf.append(str);
				}
				//接続切断
				isr.close();
				connection.disconnect();
				//レスポンスデータ受け取り
				receiveData = buf.toString();
				//System.out.println("Response Data\n" + receiveData);
				portMappingError = false;*/
				
				//接続先のホスト名，ポート番号，パスを取得
				URL rooterURL = new URL( serviceControlAddress );
				String host = rooterURL.getHost();
				int port = rooterURL.getPort();
				String path = rooterURL.getPath();
				if( rooterURL.getQuery() != null ) {
					path = path + "?" + rooterURL.getQuery();
				}
				
				logger.info("ポートマッピングアクション.要求先ホスト: " + host + " 要求先ポート: " + port + " パス: " + path);
				
				//HTTP通信用ソケット
				HttpClientSocket socket = new HttpClientSocket();
				socket.addHeader("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
				socket.addHeader("SOAPACTION", "\"" + service.getServiceType() + "#" + "AddPortMapping\"");
				
				//AddPortMapping命令のbody
				String body = createAddPortMappingSoapBody(service.getServiceType(),
						newExternalPort, newProtocol, newInternalPort, newInternalClient, portMappingDescription );
				//受信データ
				receiveData = socket.sendAndReceive(path, host, port, "POST", body);
				logger.info("ポートマッピング完了.\n" + receiveData);
				//エラーが発生しなかった
				portMappingError = false;
				break;
			}catch(IOException e) {
				e.printStackTrace();
				continue;
			}
		}
		return portMappingError;
	}
	
	/**
	 * 指定したポートを閉じる
	 * @param externalPort 閉じるポート番号
	 * @param protocol UDPかTCP
	 * @return
	 */
	public boolean deletePortMapping(int externalPort, String protocol ) {
		//ポートマッピングにエラーが発生したかどうか
		boolean portMappingError = true;
		//ポートマッピングアクションのレスポンス
		String receiveData = null;
		
		for(RooterDeviceServiceElement service : rooterServices ) {
			//サービスのコントロールアドレス
			String serviceControlAddress = getControlAddress(service);
			
			try{
				/*//アクション送信先アドレス
				URL actionURL = new URL( serviceControlAddress );
				//コネクション開始
				HttpURLConnection connection = (HttpURLConnection)actionURL.openConnection();
				//POST要求
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				//タイムアウト時間を指定
				connection.setReadTimeout(timeoutTime);

				//SOAPヘッダ AddPortMapping要求
				connection.setRequestProperty("SOAPAction", service.getServiceType() + "#" + "DeletePortMapping");
				OutputStreamWriter osw = new OutputStreamWriter( connection.getOutputStream() );
				//DeletePortMapping命令のbody
				String body = createDeletePortMappingSoapBody( service.getServiceType(), externalPort, protocol);
				//命令送信
				osw.write(body);
				osw.flush();
				osw.close();

				//レスポンスを受け取る
				BufferedReader isr = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
				StringBuffer buf = new StringBuffer("");
				String str;

				while( (str = isr.readLine() ) != null ) {
					buf.append(str);
				}
				//接続切断
				isr.close();
				connection.disconnect();
				//レスポンスデータ受け取り
				receiveData = buf.toString();
				//System.out.println("Response Data\n" + receiveData);
				portMappingError = false;
				break;*/
				
				//接続先のホスト名，ポート番号，パスを取得
				URL rooterURL = new URL( serviceControlAddress );
				String host = rooterURL.getHost();
				int port = rooterURL.getPort();
				String path = rooterURL.getPath();
				if( rooterURL.getQuery() != null ) {
					path = path + "?" + rooterURL.getQuery();
				}
				
				logger.info("ポートマッピング削除アクション.要求先ホスト: " + host + " 要求先ポート: " + port + " パス: " + path);
				
				//HTTP通信用ソケット
				HttpClientSocket socket = new HttpClientSocket();
				socket.addHeader("CONTENT-TYPE", "text/xml; charset=\"utf-8\"");
				socket.addHeader("SOAPACTION", "\"" + service.getServiceType() + "#" + "DeletePortMapping\"");
				
				//DeletePortMapping命令のbody
				String body = createDeletePortMappingSoapBody( service.getServiceType(), externalPort, protocol);
				//受信データ
				receiveData = socket.sendAndReceive(path, host, port, "POST", body);
				logger.info("ポートマッピング削除完了.\n" + receiveData);
				//エラーが発生しなかった
				portMappingError = false;
				break;
			}catch(IOException e) {
				e.printStackTrace();
				continue;
			}
		}
		return portMappingError;
	}
	
	/**
	 * XMLデータ内から<NewExternalIPAddress>タグを探して，値を抜き出す
	 * @param xmlData
	 * @return
	 */
	private String getNewExternalIPAddress(String xmlData) {
		return getEntry( xmlData, "NewExternalIPAddress" );
	}
	
	/**
	 * 指定したタグに囲まれたデータを取得
	 * @param xmlData
	 * @param tag
	 * @return
	 */
	private String getEntry(String xmlData, String tag) {
		String start = "<" + tag + ">";
		String end   = "</" + tag + ">";
		String data = null;
		try{
			int startPos = xmlData.indexOf(start) + start.length();
			int endPos   = xmlData.indexOf(end);
			data = xmlData.substring(startPos, endPos);
		}catch(Exception e) {
			//無視
		}
		return data;
	}
	
	/**
	 * サービスからコントロールアドレスを取得する
	 * @param service
	 * @return
	 */
	private String getControlAddress(RooterDeviceServiceElement service) {
		if( rooterControlAddress == null ) {
			throw new NullPointerException("ルータ制御用アドレスが正しく設定されていません．");
		}
		if( service == null ) {
			throw new NullPointerException("ルータのサービスが正しく設定されていません．");
		}
		String controlURL = null;
		//絶対アドレスでかかれている場合はそのままコントロールアドレスとする
		if( service.getControlURL().startsWith("http://") ) {
			controlURL = service.getControlURL();
		}else {
			//コントロールアドレスが相対アドレスでかかれている場合は，絶対アドレスに変換する
			if( service.getControlURL().startsWith("/") ) {
				controlURL = rooterControlAddress + service.getControlURL();
			}else {
				controlURL = rooterControlAddress + "/" + service.getControlURL();
			}
		}
		return controlURL;
	}
	
	/**
	 * GetExternalIPAddressコマンドのSOAPメッセージを作成
	 * @param serviceType
	 * @return
	 */
	private String createGetExternalIPAddressSoapBody(String serviceType) {
		String body = new String("<?xml version=\"1.0\"?>" +
        	"<s:Envelope " + "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" " + 
        	"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
        	" <s:Body>" +
        	"  <u:GetExternalIPAddress xmlns:u=\"" + serviceType + "\">" + "</u:GetExternalIPAddress>" +
        	" </s:Body>" +
        	"</s:Envelope>");
		try{
			body = new String(body.getBytes("UTF-8"), "UTF-8");
		}catch(Exception e) {
			body = null;
		}
		return body;
	}
	
	/**
	 * ポートマッピングコマンドのSOAPメッセージを作成
	 * @param serviceType
	 * @param newExternalPort 外部ポート
	 * @param newProtocol UDPかTCP
	 * @param newInternalPort 内部ポート
	 * @param newInternalClient ローカルアドレス
	 * @param portMappingDescription ポートマッピングの説明
	 * @return
	 */
	private String createAddPortMappingSoapBody(String serviceType, int newExternalPort,
			String newProtocol, int newInternalPort, String newInternalClient,
			String portMappingDescription) {
		String desc = portMappingDescription;
		if( desc == null || desc.length() < 1) {
			desc = "PolarisUPnP";
		}
		String body = "<?xml version=\"1.0\"?>" +
	        "<s:Envelope " + "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
	        "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
	        " <s:Body>" +
	        "  <u:AddPortMapping xmlns:u=\"" + serviceType + "\">" +
	        "   <NewRemoteHost></NewRemoteHost>" +
	        "   <NewExternalPort>" + newExternalPort + "</NewExternalPort>" +
	        "   <NewProtocol>" + newProtocol + "</NewProtocol>" +
	        "   <NewInternalPort>" + newInternalPort + "</NewInternalPort>" +
	        "   <NewInternalClient>" + newInternalClient + "</NewInternalClient>" +
	        "   <NewEnabled>1</NewEnabled>" +
	        "   <NewPortMappingDescription>" + desc + "</NewPortMappingDescription>" +
	        "   <NewLeaseDuration>0</NewLeaseDuration>" +
	        "  </u:AddPortMapping>" +
	        " </s:Body>" +
	        "</s:Envelope>";
		try{
			body = new String(body.getBytes("UTF-8"), "UTF-8");
		}catch(Exception e) {
			body = null;
		}
		return body;
	}
	
	/**
	 * ポートマッピング削除コマンドのSOAPメッセージを作成
	 * @param serviceType
	 * @param externalPort
	 * @param protocol
	 * @return
	 */
	private String createDeletePortMappingSoapBody(String serviceType, int externalPort, String protocol) {
		String body = "<?xml version=\"1.0\"?>" +
	        "<s:Envelope " + "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
	        "s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
	        " <s:Body>" +
	        "  <u:DeletePortMapping xmlns:u=\"" + serviceType + "\">" +
	        "   <NewRemoteHost></NewRemoteHost>" +
	        "   <NewExternalPort>" + externalPort + "</NewExternalPort>" +
	        "   <NewProtocol>" + protocol + "</NewProtocol>" +
	        "  </u:DeletePortMapping>" +
	        " </s:Body>" +
	        "</s:Envelope>";
		try{
			body = new String(body.getBytes("UTF-8"), "UTF-8");
		}catch(Exception e) {
			body = null;
		}
		return body;
	}
	
	/**
	 * 指定したインデクスのUPnPエントリーを取得
	 * @param serviceType
	 * @param index
	 * @return
	 */
	private String createGetGenericPortMappingEntry(String serviceType, int index) {
		String body = new String("<?xml version=\"1.0\"?>" +
	        	"<s:Envelope " + "xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" " + 
	        	"s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">" +
	        	" <s:Body>" +
	        	"  <u:GetGenericPortMappingEntry xmlns:u=\"" + serviceType + "\">" + 
	        	"   <NewPortMappingIndex>" + index + "</NewPortMappingIndex>" +
	        	"</u:GetGenericPortMappingEntry>" +
	        	" </s:Body>" +
	        	"</s:Envelope>");
			try{
				body = new String(body.getBytes("UTF-8"), "UTF-8");
			}catch(Exception e) {
				body = null;
			}
			return body;
	}
}
