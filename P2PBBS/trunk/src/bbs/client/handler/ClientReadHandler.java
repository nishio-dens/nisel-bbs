package bbs.client.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import bbs.BBSConfiguration;
import bbs.client.CategoryList;
import bbs.comment.CommentElement;
import bbs.comment.CommentElementXMLParser;
import bbs.manager.BBSManager;
import bbs.manager.TopicInformationElement;
import bbs.monitor.MonitorManager;
import bbs.util.HTMLEncode;
import bbs.util.Pair;
import bbs.xml.BBSXMLParser;

/**
 * クライアント側 トピック一覧及びトピックの呼び出し
 * @author nishio
 *
 */
public class ClientReadHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.client.handler");
	private BBSManager manager = null;
	//接続先ポート番号
	private int port;
	//文字コード
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	//ゲートウェイ
	private static final String GATEWAY_ADDRESS = BBSConfiguration.GATEWAY_ADDRESS;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();

	/**
	 *
	 * @param manager
	 * @param port 自身が待ち受けをしているポート番号
	 */
	public ClientReadHandler(BBSManager manager, int port) {
		this.manager = manager;
		this.port = port;
	}

	public void handle(HttpExchange he) throws IOException {
		//返信用BODY
		OutputStream os = he.getResponseBody();
		//要求URI
		String path = he.getRequestURI().getPath();
		//delim = /
		String[] splitStr = path.split("/");
		//返信メッセージ
		byte[] response = createHTML( createBadRequest() ).getBytes( DEFAULT_ENCODING );
		//返信コード
		int responseCode = 400;

		//read カテゴリ識別子 トピック識別子へと分解
		try {
			//受信データログ記録 自分からの要求なので受信量は0
			monitorManager.addMessageLog("TOPIC_RECEIVE",
					he.getRemoteAddress().getAddress().getHostAddress()
					+ ":" + he.getRemoteAddress().getPort(), 0,
					"READ");
			
			if( splitStr.length == 5 ) {
				//トピック取得
				String categoryID = splitStr[2];
				String topicID = splitStr[3];
				String commentNo = splitStr[4];
				//コメント番号
				String[] splitStr2 = commentNo.split("-");
				//コメント
				String commentData = null;
				//取得したコメント
				if( splitStr2.length == 2 ) {
					int start = Integer.parseInt( splitStr2[0] );
					int end = Integer.parseInt( splitStr2[1] );
					commentData = manager.read(categoryID, topicID, start, end);
				}else {
					throw new Exception("Bad Request.");
				}

				if( commentData != null ) {
					List<CommentElement> comments = CommentElementXMLParser
							.XMLToCommentElement(commentData);
					responseCode = 200;
					response = createHTML(
							createCategoryList() +
							createMainContents(
							createComments(comments)
							+ createPostForm(categoryID, topicID)
							+ createDeleteCommentForm(categoryID, topicID)
							+ createHistoryBack() ) ).getBytes( DEFAULT_ENCODING );
				}
				

			}else if( splitStr.length == 4 ) {
				//トピック取得
				String local = splitStr[2];
				String categoryID = splitStr[3];
				if( local.equals("local")) {
					//過去ログ取得
					String str = manager.getLocal(categoryID);
					String[] topics = str.split("\r\n|\n");
					//存在しないカテゴリ
					if( topics == null || topics[0].startsWith("Not Found")) {
						//トピックが見つからない
						responseCode = 404;
						response = createHTML(
								createCategoryList()
										+ createMainContents(createTopicHeader(categoryID)
												+ createExplain()
												+ createTopicNotFound()
												+ createTopicPostForm(categoryID)))
								.getBytes(DEFAULT_ENCODING);
					}else {
						//トピックが見つかった時
						responseCode = 200;
						//response = createHTML( createTopicList(topics, categoryID) ).getBytes( DEFAULT_ENCODING );
						response = createHTML(
								createCategoryList()
										+ createMainContents(createTopicHeader(categoryID)
												+ createExplain()
												+ createLocalTopicList(topics,
														categoryID)))
								.getBytes(DEFAULT_ENCODING);
					}
				}				
			}else if( splitStr.length == 3 ) {
				//トピック一覧取得
				String readData = null;
				//正しい要求
				String categoryID = splitStr[2];
				//トピック一覧取得
				readData = manager.read(categoryID);
				//トピック一覧をコメントElementへと変換
				Set<TopicInformationElement> topics =
					BBSXMLParser.topicInformationXMLToElement(readData, categoryID);
				//存在しないカテゴリ
				if( topics == null || topics.size() == 0) {
					//トピックが見つからない
					responseCode = 404;
					response = createHTML(
							createCategoryList()
									+ createMainContents(createTopicHeader(categoryID)
											+ createExplain()
											+ createTopicNotFound()
											+ createTopicPostForm(categoryID)))
							.getBytes(DEFAULT_ENCODING);
				}else {
					//トピックが見つかった時
					responseCode = 200;
					//response = createHTML( createTopicList(topics, categoryID) ).getBytes( DEFAULT_ENCODING );
					response = createHTML(
							createCategoryList()
									+ createMainContents(createTopicHeader(categoryID)
											+ createExplain()
											+ createTopicList(topics, categoryID)
											+ createTopicPostForm(categoryID)))
							.getBytes(DEFAULT_ENCODING);
				}

			} else {
				responseCode = 200;
				response = createHTML(
						createCategoryList()
								+ createMainContents(createTopicHeader("")
										+ createExplain()
										+ createToppage()
										+ createTopicPostForm("")))
						.getBytes(DEFAULT_ENCODING);
			}
		}catch(Exception e) {
			//無視
		}
		//送信データログ記録 自分からの要求なので送信量も0
		monitorManager.addMessageLog("TOPIC_SEND",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), 0,
				"READ");
		//ヘッダ送信
		he.sendResponseHeaders(responseCode, response.length);
		//データ送信
		os.write(response);
		//接続を切る
		os.close();
	}


	/**
	 * コメントのHTML作成
	 * @param comments
	 * @return
	 */
	private String createComments(List<CommentElement> comments) {
		StringBuffer buf = new StringBuffer("");
		String title = null;

		for(CommentElement comment : comments ) {
			String buf2 = "<a name=\"l" + comment.getNo() + "\"></a><dt>"
					+ comment.getNo()
					+ " 名前 :<font color=\"#008800\"><b> "
					+ comment.getAuthor()
					+ " </b></font><font size=\"-1\">["
					+ comment.getMailAddress()
					+ "] </font>"
					//+ " password: " + comment.getPassword() //TODO デバッグ用 あとで削除
					+ " 投稿日 :"
					+ comment.getDate()
					+ " ID: "
					+ comment.getId()
					+ "<br></dt>\n<dd><br>\n"									
					+ bbs.util.HTMLEncode.replaceSpace(bbs.util.HTMLEncode
							.replaceNewline( bbs.util.HTMLEncode.replaceURL(comment.getMessage())))
					+ "\n</dd><br><br>\n";
			buf.append(buf2);
			if( title == null && comment.getTitle() != null ) {
				title = comment.getTitle();
			}
		}

		String retValue = "<dl>";
		if( title == null ) {
			retValue += "<font color=\"red\" size=\"+2\">タイトルが取得できませんでした．</font><br><br>\n";
		}else {
			retValue += "<font color=\"red\" size=\"+2\">" + title + "</font><br><br>\n";
		}
		retValue += buf.toString();
		retValue += "</dl>";
		//retValue += "</dl>\n<a href=\"javascript:history.back();\">戻る</a>";
		return retValue;
	}

	/**
	 * カテゴリ一覧の作成
	 * @return
	 */
	private String createCategoryList() {
		/*return "<h1>カテゴリ一覧</h1><p>\n" +
			"<a href=\"http://" + GATEWAY_ADDRESS + ":" + port + "/read/a94a8fe5ccb19ba61c4c0873d391e987982fbbd3\""
			+">Test Category [カテゴリ名:test]</a></p>";*/
		CategoryList category = CategoryList.getCategoryListClass();
		StringBuffer buf = new StringBuffer("<div id=\"leftbox\">\n<h3>カテゴリ一覧</h3>\n");
		buf.append("<div style=\"text-align: left; margin-left: 10px;\">\n");
		for( Pair<String, String> cat : category.getCategoryList() ) {
			buf.append("<a href=\"http://" + GATEWAY_ADDRESS + ":" + port + "/read/" + cat.getSecond() + "\""
					+">" + cat.getFirst() +  "</a><br>\n" );
		}
		buf.append("</div></div>\n");
		return buf.toString();
	}

	/**
	 * トピック投稿フォームの作成
	 * @param categoryID カテゴリ名 selected
	 * @return
	 */
	private String createTopicPostForm(String categoryID) {
		StringBuffer buf = new StringBuffer("<div id=\"box\"><h3>トピック作成</h3>"
				+ "<form method=\"post\" action=\"http://" + GATEWAY_ADDRESS + ":" + port + "/command/manage/\">\n"
				+ " <p>\n"
				+ " カテゴリ<select name=\"category\"> "
				);
		CategoryList category = CategoryList.getCategoryListClass();
		for( Pair<String, String> cat : category.getCategoryList() ) {
			if( categoryID.equals(cat.getSecond())) {
				buf.append("<option selected=\"selected\">" + cat.getFirst() + "</option>");
			}else {
				buf.append("<option>" + cat.getFirst() + "</option>");
			}
		}
		buf.append("</select><br>\n"
				+ " タイトル<input type=\"text\" size=\"40\" name=\"title\" /> <br>\n"
				+ " 投稿者<input type=\"text\" name=\"author\" />\n"
				+ " メール<input type=\"text\" name=\"mail\" />\n"
				+ " パスワード<input type=\"password\" name=\"password\" /> <br>\n"
				+ " <input type=\"hidden\" name=\"user\" value=\"true\"/>"
				+ "<br>\n"
				+ "<textarea name=\"message\" rows=\"4\" cols=\"60\"></textarea>\n"
				+ "<br>\n"
				+ " <input type=\"submit\" value=\"送信\" />\n"
				+ " <input type=\"reset\" value=\"取り消し\" />\n"
				+ " </p>\n"
				+ "</form></div>\n" );
		return buf.toString();
	}

	/**
	 * コメント投稿フォームの作成
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	private String createPostForm(String categoryID, String topicID) {
		return "<p><b>コメント投稿</b><br>" +
				"<form method=\"post\" action=\"http://"
				+ "" + GATEWAY_ADDRESS + ":"
				+ port
				+ "/command/write/\">\n"
				+ "<input type=\"hidden\" name=\"category\" value=\""
				+ categoryID
				+ "\" />\n"
				+ "<input type=\"hidden\" name=\"topic\" value=\""
				+ topicID
				+ "\"/>\n"
				+ "投稿者<input type=\"text\" name=\"author\" />\n"
				+ "メール<input type=\"text\" name=\"mail\" />\n"
				+ "パスワード<input type=\"password\" name=\"password\" />\n"
				+ "<br>\n"
				+ "<textarea name=\"message\" rows=\"4\" cols=\"80\"></textarea>\n"
				+ "<br>\n"
				+ "<input type=\"hidden\" name=\"user\" value=\"true"
				+ "\"/>\n"
				+ "<input type=\"submit\" value=\"投稿\" />\n"
				+ "<input type=\"reset\" value=\"取り消し\" />\n" + "</form></p>";
	}

	/**
	 * コメント削除フォーム
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	private String createDeleteCommentForm(String categoryID, String topicID) {
		return "<p><b>コメント削除</b>"
			+ "<form method=\"post\" action=\"http://"
			+ "" + GATEWAY_ADDRESS + ":"
			+ port
			+ "/command/delete/\">\n"
			+ "<input type=\"hidden\" name=\"category\" value=\""
			+ categoryID
			+ "\" />\n"
			+ "<input type=\"hidden\" name=\"topic\" value=\""
			+ topicID
			+ "\"/>\n"
			+ "<input type=\"hidden\" name=\"first\" value=\"1"
			+ "\"/>\n"
			+ "<input type=\"hidden\" name=\"user\" value=\"true"
			+ "\"/>\n"
			+ "削除するコメント番号(1でトピック全体を削除）<input type=\"text\" name=\"no\" />\n"
			+ "パスワード<input type=\"password\" name=\"password\" /><br>\n"
			+ "<br>\n"
			+ "<input type=\"submit\" value=\"削除\" /><br><br>\n";
	}

	/**
	 * 戻るボタンの作成
	 * @return
	 */
	private String createHistoryBack() {
		return "<a href=\"javascript:history.back();\">戻る</a>";
	}

	/**
	 * トピック一覧
	 * @param topics
	 * @return
	 */
	private String createTopicList(Set<TopicInformationElement> topics, String categoryID) {
		StringBuffer buf = new StringBuffer("");
		buf.append( ""
                   + "<div id=\"box\">"
                   + "<h3>トピック一覧</h3>");
		buf.append("<div id=\"ListTable\">" + "<div id=\"ListTableIn\">"
				+ "<table>" + "<tbody>" + "<tr>" + "<th>No</th>"
				+ "<th>タイトル</th>" + "<th>作成者</th>" + "<th>コメント数</th>"
				+ "<th>参照</th>" + "</tr>");
		int i = 1;
		DecimalFormat df = new DecimalFormat("0.000");
		for(TopicInformationElement topic : topics) {
			if( i % 2 == 0 ) {
				buf.append("<tr class=\"TableOdd\">\n");
			}else {
				buf.append("<tr class=\"TableEven\">\n");
			}
			buf.append("<td>" + i + "</td>\n");
			buf.append("<td><a href=\"http://" + GATEWAY_ADDRESS + ":" + port + "/read/"
					+ categoryID + "/" + topic.getTid() + "/0-0\">");
			buf.append(HTMLEncode.encode(topic.getTitle()));
			buf.append("</a></td>\n");
			buf.append("<td>" + topic.getAuthor() + "</td>");
			buf.append("<td>" + topic.getNumOfComment() + "</td>\n");
			buf.append("<td>" + df.format((double)topic.getActivity() / 120.0) + "回/時</td>");
			buf.append("</tr>");
			i++;
		}
		buf.append("</tbody>\n");
		buf.append("</table><br><a href=\"http://" + GATEWAY_ADDRESS + ":" + port + "/read/local/"
					+ categoryID + "\">" + "過去のトピックを閲覧するにはこちらをクリックしてください．</a>"
					+ "</div></div>" +  "</div>\n");
		buf.append("");
		return buf.toString();
	}
	
	/**
	 * ローカルトピック一覧
	 * @param topics
	 * @return
	 */
	private String createLocalTopicList(String[] topics, String categoryID) {
		StringBuffer buf = new StringBuffer("");
		buf.append( ""
                   + "<div id=\"box\">"
                   + "<h3>過去のトピック一覧</h3>");
		buf.append("<div id=\"ListTable\">" + "<div id=\"ListTableIn\">"
				+ "<table>" + "<tbody>" + "<tr>" + "<th>No</th>"
				+ "<th>トピックID</th>" + "<th>作成者</th>" + ""
				+ "" + "</tr>");
		int i = 1;
		DecimalFormat df = new DecimalFormat("0.000");
		for(String topic : topics) {
			if( i % 2 == 0 ) {
				buf.append("<tr class=\"TableOdd\">\n");
			}else {
				buf.append("<tr class=\"TableEven\">\n");
			}
			buf.append("<td>" + i + "</td>\n");
			buf.append("<td><a href=\"http://" + GATEWAY_ADDRESS + ":" + port + "/read/"
					+ categoryID + "/" + topic + "/0-0\">");
			buf.append(HTMLEncode.encode(topic));
			buf.append("</a></td>\n");
			buf.append("");
			buf.append("\n");
			buf.append("<td>" + "未実装" + "</td>");
			buf.append("</tr>");
			i++;
		}
		buf.append("</tbody>\n");
		buf.append("</table></div></div></div>\n");
		buf.append("");
		return buf.toString();
	}

	/**
	 * HTMLに必要なヘッダ等を生成
	 * @param body
	 * @return
	 */
	private String createHTML(String body) {
		return "<html><head>\n"
				+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DEFAULT_ENCODING + "\">\n"
				+ "<title>P2PBBS</title>\n" + "<style>\n" + "<!--\n"
				+ "/* テーブル奇数*/\n" + ".TableEven {\n"
				+ "background-color: #f7f7f7;\n" + "}\n" + ".TableOdd {\n"
				+ "background-color: #fcfcfc;\n" + "}\n" + "/* テーブル一覧関係 */\n"
				+ "#ListTable {\n" + "clear: both;\n"
				+ "margin: 10px 25px 10px 25px;\n" + "}\n" + "#ListTableIn {\n"
				+ "width: 100%;\n" + "}\n" + "#ListTableIn table {\n"
				+ "width: 100%;\n" + "}\n" + "#ListTableIn th {\n"
				+ "padding: 5px;\n" + "background-color: #336699;\n"
				+ "color: #ffffff;\n" + "white-space: nowrap;\n" + "}\n"
				+ "#ListTableIn td {\n" + "padding: 5px;\n" + "}\n"
				+ ""
				+ "body {margin:5; padding:5; overflow:hidden;}\n"
				+ "div#leftbox {margin:0; padding:0; height:100%; width:150px;\n"
				+ "position:absolute; left:0; top:0;background-color:#F8F8FF}\n"
				+ "div#mainbox {margin:0 0 0 10em; padding:0;"
				+ "height:100%; overflow:auto;background-color:#FFFFFF}\n"
				+ "div#header{width:100%;margin:0 0 1em 0;text-align:left;background-color:#FFFFFF}\n"
				+ "div#footer{clear:both;width:100%;margin:5em 0 1em 0;text-align:left;}\n"
				+ "div#box{background-color:#FFFFFF}\n"
				+ "-->\n"
				+ "</style>\n"
				+"</head>\n<body>\n"
				+ body
				+ "\n</body>\n</html>";
	}

	/**
	 * メインコンテンツDIV HTML生成
	 * @param body
	 * @return
	 */
	private String createMainContents(String body) {
		return "<div id=\"mainbox\">" + body + "</div>";
	}

	/**
	 * 指定されたトピックにカテゴリが存在しなかったとき
	 * @return
	 */
	private String createTopicNotFound() {
		return "<b>指定されたカテゴリにトピックは存在しませんでした．</b><br>";
	}

	/**
	 * トップページ用
	 * @return
	 */
	private String createToppage() {
		return "<b>左側のカテゴリ一覧から自分の興味のあるカテゴリを選択してください．</b><br>";
	}

	/**
	 * コンテンツヘッダ部分作成
	 * @param categoryID
	 * @return
	 */
	private String createTopicHeader(String categoryID) {
		CategoryList category = CategoryList.getCategoryListClass();
		StringBuffer buf = new StringBuffer("<div id=\"header\">\n"
				+ "<h3>\n"
				+ "<center>P2PBBS カテゴリ:" + category.getCategoryName(categoryID) + "<br>\n"
				+ "</center>\n"
				+ "</h3>\n"
				+ "</div>");
		return buf.toString();
	}

	/**
	 * 掲示板利用規約等を表示
	 * @return
	 */
	private String createExplain() {
		StringBuffer buf = new StringBuffer("");
		buf.append( "<div id=\"box\">\n"
                   + "<p><span style=\"color: rgb(255, 0, 0); font-weight: bold;\">\n"
                   + "本システムを利用する前に必ず読んでください\n"
                   + "\n"
                   + "</span></p>\n"
                   + "<li>本システムはテスト段階のプログラムです．\n"
                   + "信頼性，完全性については一切保証しておりません．</li>\n"
                   + "<li>本システムを利用したことにより，利用者が何らかの損害，\n"
                   + "不利益を被った場合でも，製作者はその責任を一切負いません．</li>\n"
                   + "<li>トピック作成者は作成したトピックを削除できる権利を持ちます．</li>\n"
                   + "<li>著作権者の許可を得ていない著作物，\n"
                   + "個人のプライバシーに関わる情報等の投稿は禁止いたします．</li>\n"
                   + "<li>過去のトピックには，\n"
                   + "コメントの投稿を行うことはできません．閲覧のみ可能です．</li>\n"
                   + "</div><br>\n");
		return buf.toString();
	}

	/**
	 * おかしなリクエストが送られてきたとき
	 * @return
	 */
	private String createBadRequest() {
		return "リクエストが間違っています．正しいリクエストを送ってください．<br>"
				+ "<a href=\"javascript:history.back();\">戻る</a>";
	}
}
