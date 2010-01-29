package polaris.upnp.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.xml.sax.SAXParseException;

/**
 * ルータの情報が書き込まれているURIからルータ情報の解析を行う
 * @author nishio
 *
 */
public class RooterDeviceDescriptionAnalyzer {
	//ルータの情報がかかれているURI
	private String deviceDescriptionURI = null;
	
	private static Logger logger = Logger.getLogger("polaris.upnp");
	
	/**
	 * 
	 * @param deciveDescriptionURI
	 */
	public RooterDeviceDescriptionAnalyzer(String deciveDescriptionURI) {
		this.deviceDescriptionURI = deciveDescriptionURI;
	}
	
	/**
	 * ルータ情報を取得し，サービス一覧を取得する
	 * @return
	 * @throws BadDeviceURIException
	 * @throws IOException
	 * @throws BadDeviceXMLException
	 */
	public List<RooterDeviceServiceElement> getDeviceService() throws BadDeviceURIException,
	IOException, BadDeviceXMLException {
		String xmlData = this.getDeviceDescription();
		return getDeviceService( xmlData );
	}
	
	/**
	 * サービスタイプがWANPPPConnectionまたはWANIPConnctionであるサービスを取得
	 * @param services
	 * @return
	 */
	public List<RooterDeviceServiceElement> getConnectionService(List<RooterDeviceServiceElement> services) {
		if( services == null ) {
			throw new NullPointerException("サービスが一つも見つかりませんでした．");
		}
		List<RooterDeviceServiceElement> connectionServices = new ArrayList<RooterDeviceServiceElement>();
		for(RooterDeviceServiceElement elem : services) {
			if( elem.getServiceType().contains("WANPPPConnection") ||
					elem.getServiceType().contains("WANIPConnection") ) {
				connectionServices.add( elem );
			}
		}
		return connectionServices;
	}

	/**
	 * ルータ情報がかかれたXMLからデバイスのサービスを抽出する
	 * @param deviceDescription
	 * @throws BadDeviceXMLException 
	 */
	public List<RooterDeviceServiceElement> getDeviceService(String deviceDescriptionXML) throws BadDeviceXMLException {
		List<RooterDeviceServiceElement> services = null;
		try{
			 services = RooterDeviceServiceXMLParser.XMLToSerciveElement(deviceDescriptionXML);
		}catch(SAXParseException e) {
			throw new BadDeviceXMLException("ルータ情報解析中にXML文章の Column: " + e.getColumnNumber() +
					" Line: " + e.getLineNumber() + " にてエラーが発生しました．");
		}catch(Exception e) {
			throw new BadDeviceXMLException("ルータ情報が正しく取得できませんでした（未知のエラー)" );
		}
		return services;
	}
	
	/**
	 * ルータの情報を取得
	 * @return
	 * @throws BadDeviceURIException ルータ情報取得用URIが正しく設定されていなかった
	 * @throws IOException ルータ情報取得用URIへアクセスできなかった
	 */
	public String getDeviceDescription() throws BadDeviceURIException, IOException {
		if( deviceDescriptionURI == null ) {
			throw new BadDeviceURIException("ルータ情報取得用URIが正しく設定されていません．");
		}
		//取得したXMLデータ
		StringBuffer xmlData = null;
		URL url = new URL(deviceDescriptionURI);
		try {
			//device情報が乗っているアドレスに接続し，データを取得
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			//情報取得用リーダ
			BufferedReader deviceInfoReader = new BufferedReader( 
					new InputStreamReader( connection.getInputStream() ) );
			
			String buf;
			xmlData = new StringBuffer("");
			//ルータ情報を取得
			while( (buf = deviceInfoReader.readLine() ) != null ) {
				xmlData.append( buf + "\n");
			}
			logger.info("ルータXMLデータ: " + xmlData.toString());
			//切断
			deviceInfoReader.close();
			connection.disconnect();			
		} catch (IOException e) {
			throw new IOException("Connectionに失敗しました．ルータ情報取得用URIが間違っています．");
		}
		
		return xmlData.toString();
	}

}
