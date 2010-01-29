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

/**
 * データ削除関係
 * @author nishio
 *
 */
public class DeleteHandler implements HttpHandler {

	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();

	private static final String DEFAULT_CHARACTER_ENCODING = BBSConfiguration.DEFAULT_ENCODING;

	public DeleteHandler(BBSManager manager) {
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
				//パスワード
				String password = null;
				//コメント番号
				String commentNo = null;
				//first
				String first = null;

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
						if( s1.equals("topic") ) {
							topicID = HTMLEncode.encode(s2);
						}else if( s1.equals("no") ) {
							commentNo = HTMLEncode.encode(s2);
						}else if( s1.equals("category") ) {
							categoryID = HTMLEncode.encode( s2 );
						}else if( s1.equals("password") ) {
							password = HTMLEncode.encode(s2);
						}else if( s1.equals("first")) {
							first = HTMLEncode.encode(s2);
						}else if( s1.equals("user") ) {
							userMode = true;
						}
					}
				}
				//パスワードが空の場合もある
				if( password == null ) {
					password = "";
				}

				//正しい情報を受信していない
				if (topicID == null || categoryID == null || password == null
						|| commentNo == null || first == null) {
					throw new Exception("受信データが正しくありません．");
				}
				//削除するコメント番号
				int no = Integer.parseInt(commentNo);
				boolean fst = (Integer.parseInt(first) == 1? true: false);

				//受信データログ記録
				monitorManager.addMessageLog("TOPIC_RECEIVE",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), buf.length(),
						"REMOVE");
				//トピック削除
				boolean res = manager.delete(categoryID, topicID, password, no, fst);
				if( userMode ) {
					if( res ) {
						//要求受付を完了したことを知らせる
						String tmp = UsermodeHTML.deleteRequestResponse(true);
						response = tmp.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}else {
						// 何らかの理由で書き込みに失敗した時
						response = UsermodeHTML.deleteRequestResponse(false)
								.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}
				}else {
					if( res ) {
						//要求受付を完了したことを知らせる
						String tmp = "DELETE_ACCEPT category: " + categoryID + " topic: " + topicID;
						response = tmp.getBytes();
						responseCode = 200;
					}else {
						//何らかの理由で書き込みに失敗した時
						response = "DELETE_REJECT".getBytes();
						responseCode = 200;
					}
				}
				logger.info("delete request from " + he.getRemoteAddress().getAddress().getHostAddress() +
						" category: " + categoryID + " topic: " + topicID + " no: " + no);
			}else {
				throw new Exception("Bad request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//間違った要求 400 Bad Request
			responseCode = 400;
			if( userMode ) {
				response = UsermodeHTML.deleteRequestResponse(false)
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			}else {
				response = "Bad delete Request.".getBytes();
			}
			logger.info("Bad delete Request from " + he.getRemoteAddress().getAddress().getHostAddress());
		}

		//responseCode返信
		he.sendResponseHeaders(responseCode, response.length);
		os.write(response);
		
		//送信データログ記録
		monitorManager.addMessageLog("TOPIC_SEND",
				he.getRemoteAddress().getAddress().getHostAddress()
				+ ":" + he.getRemoteAddress().getPort(), response.length,
				"REMOVE");

		//接続を切る
		os.close();
	}
}
