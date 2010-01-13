package bbs.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.logging.Logger;

import bbs.BBSConfiguration;
import bbs.comment.CommentElement;
import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;
import bbs.server.html.UsermodeHTML;
import bbs.util.Digest;
import bbs.util.HTMLEncode;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ManageHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();

	private static final String DEFAULT_CHARACTER_ENCODING = BBSConfiguration.DEFAULT_ENCODING;

	public ManageHandler(BBSManager manager) {
		this.manager = manager;
	}

	public void handle(HttpExchange he) throws IOException {
		//返信用BODY
		OutputStream os = he.getResponseBody();
		//要求URI
		String path = he.getRequestURI().getPath();
		//delim = /
		String[] splitStr = path.split("/");
		//response
		byte[] response = null;
		int responseCode = 200;
		//一般ユーザ向けの返信を行うかどうか
		boolean userMode = false;

		try{
			//トピック生成
			if( splitStr.length == 3 ) {
				//正しい要求
				//String categoryID = splitStr[3];
				//String topicID = splitStr[4];
				//要求取得
				InputStream is = he.getRequestBody();
				//入力ストリームを取得
				BufferedReader in = new BufferedReader(
						new InputStreamReader(is, DEFAULT_CHARACTER_ENCODING ) );

				//データ読み取り
				String line;
				StringBuffer buf = new StringBuffer();
				while ((line = in.readLine()) != null) {
					buf.append(line);
				}
				//トピック要素
				CommentElement commentElem = new CommentElement();
				//カテゴリ識別子
				String categoryID = null;
				//トピック識別子
				String topicID = null;

				//&毎に区切る
				String[] data = buf.toString().split("&");
				for( String d : data) {
					//=で区切られているものを抜き出す
					String[] msg = d.split("=");
					//属性
					String s1 = msg[0];
					if( msg.length <= 1 ) {
						continue;
					}
					//要求内容
					String s2 = new String( URLDecoder.decode( msg[1],
							DEFAULT_CHARACTER_ENCODING  ) );
					if( s1 != null && s2 != null ) {
						if( s1.equals("title") ) {
							commentElem.setTitle(HTMLEncode.encode( s2 ));
							//topic識別子はランダムな値
							topicID = Digest.getStringDigest(s1+s2+(Math.random() * 10000));
						}else if( s1.equals("author") ) {
							commentElem.setAuthor(HTMLEncode.encode( s2 ));
						}else if( s1.equals("mail") ) {
							commentElem.setMailAddress(HTMLEncode.encode( s2 ));
						}else if( s1.equals("message") ) {
							commentElem.setMessage( HTMLEncode.encode( s2 ) );
						}else if( s1.equals("category") ) {
							//カテゴリ識別子は，カテゴリ名のSHA-1をとったもの
							categoryID = Digest.getStringDigest(s2);
						}else if( s1.equals("password") ) {
							commentElem.setPassword( Digest.getStringDigest(s2) );
						}else if( s1.equals("user") ) {
							userMode = true;
						}
					}
				}
				//正しい情報を受信していない
				if( topicID == null || categoryID == null || commentElem.getTitle() == null ||
						commentElem.getMessage() == null ) {
					throw new Exception("受信データが正しくありません．");
				}
				//名前がない場合
				if( commentElem.getAuthor() == null ) {
					commentElem.setAuthor("NoName");
				}
				//メールがない場合
				if( commentElem.getMailAddress() == null ) {
					commentElem.setMailAddress("");
				}
				if( commentElem.getPassword() == null ) {
					commentElem.setPassword("");
				}

				//受信データログ記録
				monitorManager.addMessageLog("TOPIC_RECEIVE",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), buf.length(),
						"MANAGE");

				//トピック作成
				boolean res = manager.manage(categoryID, topicID, commentElem);
				if( userMode ) {
					if( res ) {
						//要求受付を完了したことを知らせる
						String tmp = UsermodeHTML.manageRequestResponse(true);
						response = tmp.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}else {
						// 何らかの理由で書き込みに失敗した時
						response = UsermodeHTML.manageRequestResponse(false)
								.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}
				}else {
					if( res ) {
						//要求受付を完了したことを知らせる
						String tmp = "ACCEPT category: " + categoryID + " topic: " + topicID;
						response = tmp.getBytes();
						responseCode = 200;
					}else {
						//何らかの理由で書き込みに失敗した時
						response = "REJECT".getBytes();
						responseCode = 200;
					}
				}
				logger.info("manage request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID + " topic: " + topicID);
			}else {
				throw new Exception("Bad request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//間違った要求 400 Bad Request
			responseCode = 400;
			if( userMode ) {
				response = UsermodeHTML.manageRequestResponse(false)
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			}else {
				response = "Bad manage Request".getBytes();
			}
			logger.info("Bad manage Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}

		//responseCode返信
		he.sendResponseHeaders(responseCode, response.length);
		os.write(response);
		
		//受信データログ記録
		monitorManager.addMessageLog("TOPIC_SEND",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), response.length,
				"MANAGE");

		//接続を切る
		os.close();
	}
}
