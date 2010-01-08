package bbs.xml;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import bbs.BBSConfiguration;
import bbs.manager.TopicInformationElement;

public class BBSXMLParser {
	
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	
	/**
	 * 管理ノード情報XMLからトピックの活性度を取得
	 * @param xmlData
	 * @return
	 */
	public static String manageNodeXMLToActivity( String xmlData ) {
		if( xmlData == null ) {
			return null;
		}
		try {
			xmlData = new String(xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String retValue = "0";
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
			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			//System.out.println("ルート要素名 : " + root.getTagName());
			retValue = root.getElementsByTagName("activity").item(0).getFirstChild().getNodeValue();
		}catch(SAXParseException e) {
			System.err.println("XMLデータが壊れています．");
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return retValue;
	}
	
	/**
	 * 管理ノード情報XMLからコメント数を取得
	 * @param xmlData
	 * @return
	 */
	public static String manageNodeXMLToNumOfComments( String xmlData ) {
		if( xmlData == null ) {
			return null;
		}
		try {
			xmlData = new String(xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String retValue = "0";
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
			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			//System.out.println("ルート要素名 : " + root.getTagName());
			retValue = root.getElementsByTagName("num").item(0).getFirstChild().getNodeValue();
		}catch(SAXParseException e) {
			System.err.println("XMLデータが壊れています．");
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return retValue;
	}
	
	/**
	 * 管理ノード情報XMLからトピックのコメントデータ量を取得
	 * @param xmlData
	 * @return
	 */
	public static String manageNodeXMLToTopicMessageSize( String xmlData ) {
		if( xmlData == null ) {
			return null;
		}
		try {
			xmlData = new String(xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String retValue = "0";
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
			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			//System.out.println("ルート要素名 : " + root.getTagName());
			retValue = root.getElementsByTagName("size").item(0).getFirstChild().getNodeValue();
		}catch(SAXParseException e) {
			System.err.println("XMLデータが壊れています．");
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return retValue;
	}

	/**
	 * 管理ノード情報XMLからIPアドレスを抜き出す
	 * @param xmlData
	 * @return
	 */
	public static LinkedList<String> manageNodeXMLToIPAddress( String xmlData ) {
		if( xmlData == null ) {
			return null;
		}
		try {
			xmlData = new String(xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		LinkedList<String> retArray = new LinkedList<String>();
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

			//XML解析
			doc = builder.parse(is);

			// ルート要素になっている子ノードを取得
			NodeList list = doc.getElementsByTagName("node");
			String[] nodeList = new String[list.getLength()];
			for(int i=0; i < list.getLength(); i++ ) {
				//System.out.println("ルート要素名 : " + root.getTagName());
				// 各要素を取得
				try {
					Element element = (Element) list.item(i);
					String id = element.getAttribute("id");
					NodeList ipList = element.getElementsByTagName("ip");
					Element ipElement = (Element) ipList.item(0);
					String ip = ipElement.getFirstChild().getNodeValue();
					//id
					//retArray.add(Integer.parseInt(id), ip);
					nodeList[ Integer.parseInt(id) - 1 ] = ip;
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			for(int i=0; i < nodeList.length; i++) {
				retArray.add(nodeList[i]);
			}

		}catch(SAXParseException e) {
			System.err.println("XMLデータが壊れています．");
			e.printStackTrace();
			return null;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return retArray;
	}
	
	/**
	 * トピック情報をトピック情報エレメントへと変換
	 * @param xmlData
	 * @return
	 */
	public static Set<TopicInformationElement> topicInformationXMLToElement(
			String xmlData, String categoryID) {
		if (xmlData == null || categoryID == null) {
			return null;
		}
		try {
			xmlData = new String(xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Set<TopicInformationElement> retArray = new LinkedHashSet<TopicInformationElement>();
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

			try {
				doc = builder.parse(is);
			}catch( SAXParseException se ) {
				//XMLの形式がおかしい
				System.err.println("XMLデータが壊れています．");
				return null;
			}

			// ルート要素になっている子ノードを取得
			Element root = doc.getDocumentElement();
			//System.out.println("ルート要素名 : " + root.getTagName());
			// 各ノードリストを取得
			NodeList list = root.getElementsByTagName("topic");
			for (int i = 0; i < list.getLength(); i++) {
				// System.out.println("ルート要素名 : " + root.getTagName());
				// 各要素を取得
				try{
					Element element = (Element) list.item(i);
					String tid = getChildren(element, "tid");
					String title = getChildren(element, "title");
					int num = Integer.parseInt( getChildren(element, "num") );
					int activity = Integer.parseInt( getChildren(element, "activity"));
					TopicInformationElement elem = new TopicInformationElement(
							categoryID, tid, title, num, activity);
					// id
					retArray.add(elem);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return retArray;
	}
	
	
	/**
	 * 指定されたエレメントから子要素の内容を取得。
	 * 
	 * @param   element 指定エレメント
	 * @param   tagName 指定タグ名
	 * @return  取得した内容
	 */
	private static String getChildren(Element element, String tagName) {
		NodeList list = element.getElementsByTagName(tagName);
		Element cElement = (Element)list.item(0);
		if( cElement == null || cElement.getFirstChild() == null) {
			return null;
		}
		return cElement.getFirstChild().getNodeValue();
	}
}
