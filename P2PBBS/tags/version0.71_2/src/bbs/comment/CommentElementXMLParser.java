package bbs.comment;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import bbs.BBSConfiguration;
import bbs.comment.CommentElement;

/** 
*
* @author Nishio
*
*/
public class CommentElementXMLParser {
	
	public static final String ROOT    = "topic";
	public static final String COMMENT = "comment";
	public static final String NO      = "no";
	public static final String AUTHOR  = "author";
	public static final String MAIL    = "mail";
	public static final String ID      = "id";
	public static final String DATE    = "date";
	public static final String MESSAGE = "message";
	public static final String TITLE   = "title";
	public static final String PASSWORD = "password";
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;

	/**
	 * XML文章からCommentエレメントを取得
	 * @param xmlData
	 * @return
	 * @throws SAXParseException 
	 */
	public static List<CommentElement> XMLToCommentElement( String xmlData ) throws SAXParseException {
		if( xmlData == null ) {
			return null;
		}
		try {
			xmlData = new String( xmlData.getBytes(DEFAULT_ENCODING), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
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
			// comment要素を取得
			NodeList comments = root.getElementsByTagName( COMMENT );
			ArrayList<CommentElement> ret = new ArrayList<CommentElement>();
			
			for(int i=0; i < comments.getLength(); i++ ) {
				// page要素を取得
		        Element element = (Element)comments.item(i);
				// 各要素を取得
				String no = getChildren(element, NO);
				String author = getChildren(element, AUTHOR);
				String mail = getChildren(element, MAIL);
				String id = getChildren(element, ID);
				String date = getChildren(element, DATE);
				String message = getChildren(element, MESSAGE);
				String title = getChildren(element, TITLE);
				String password = getChildren(element, PASSWORD);
				
				CommentElement ce = new CommentElement(Integer.parseInt(no), author, mail, id, date, message);
				if( title != null ) {
					ce.setTitle(title);
				}
				if( password != null ) {
					ce.setPassword(password);
				}
				ret.add( ce );
			}
			
			return ret;

		}catch(SAXParseException e) {
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
			return null;
		}
		return cElement.getFirstChild().getNodeValue();
	}
}

