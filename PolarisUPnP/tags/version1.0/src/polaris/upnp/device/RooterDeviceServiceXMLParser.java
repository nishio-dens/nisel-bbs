package polaris.upnp.device;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class RooterDeviceServiceXMLParser {
	private static final String ROOT = "root";
	private static final String SPECVERSION = "specVersion";
	private static final String DEVICE = "device";
	//deviceノードの下にある要素
	private static final String SERVICELIST = "serviceList";
	private static final String SERVICE = "service";
	private static final String DEVICELIST = "deviceList";
	
	private static final String DEVICETYPE = "deviceType";
	private static final String FRIENDLYNAME = "friendlyName";
	private static final String MANUFACTURER = "manufacturer";
	private static final String MODELNAME = "modelName";
	//serviceの下にある要素
	private static final String SERVICETYPE = "serviceType";
	private static final String SERVICEID = "serviceId";
	private static final String SCPDURL = "SCPDURL";
	private static final String CONTROLURL = "controlURL";
	private static final String EVENTSUBURL = "eventSubURL";
	
	/**
	 * XML文章からServiceエレメントを取得
	 * @param xmlData
	 * @return
	 * @throws SAXParseException 
	 */
	public static List<RooterDeviceServiceElement> XMLToSerciveElement( String xmlData ) throws SAXParseException {
		if( xmlData == null ) {
			return null;
		}
		//service要素
		List<RooterDeviceServiceElement> serviceListElements = new ArrayList<RooterDeviceServiceElement>();
		
		try {
			//ドキュメントビルダーファクトリを生成
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			//ドキュメントビルダーを生成
			DocumentBuilder builder = dbfactory.newDocumentBuilder();
			//パースを実行してDocumentオブジェクトを取得
			Document doc = null;
			//入力ストリーム作成
			StringReader sr = new StringReader(xmlData);
			InputSource is = new InputSource(sr);

			doc = builder.parse(is);

			//root要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			//System.out.println("tag :" + root.getTagName() );
			//<device>下の要素を取得
			NodeList services = root.getElementsByTagName(SERVICE);
			for(int i=0; i < services.getLength(); i++ ) {
				Element serviceElement = (Element)services.item(i);
				String serviceTypeStr = getChildren(serviceElement, SERVICETYPE);
				String serviceIdStr = getChildren(serviceElement, SERVICEID);
				String SCPDURLStr = getChildren(serviceElement, SCPDURL );
				String controlURLStr = getChildren(serviceElement, CONTROLURL );
				String eventSubURLStr = getChildren(serviceElement, EVENTSUBURL );
				//サービス情報を追加
				serviceListElements.add( new RooterDeviceServiceElement(
						serviceTypeStr, serviceIdStr, SCPDURLStr, controlURLStr, eventSubURLStr) );
			}

		}catch(SAXParseException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return serviceListElements;
	}
	
	/**
	 * 指定されたエレメントから子要素の内容を取得
	 *
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 */
	private static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		if( cElement == null || cElement.getFirstChild() == null ) {
			return "";
		}
		return cElement.getFirstChild().getNodeValue();
	}
}
