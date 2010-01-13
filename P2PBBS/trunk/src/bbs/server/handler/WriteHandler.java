package bbs.server.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Logger;

import bbs.BBSConfiguration;
import bbs.comment.CommentElement;
import bbs.comment.MaxLengthOfAuthorException;
import bbs.comment.MaxLengthOfCommentException;
import bbs.comment.MaxLengthOfMailException;
import bbs.comment.MaxNumberOfCommentException;
import bbs.comment.TopicNotFoundException;
import bbs.manager.BBSManager;
import bbs.monitor.MonitorManager;
import bbs.server.html.UsermodeHTML;
import bbs.util.Digest;
import bbs.util.HTMLEncode;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * トピックへのコメント書き込み処理
 * @author nishio
 *
 */
public class WriteHandler implements HttpHandler {
	private Logger logger = Logger.getLogger("bbs.server.handler");
	private BBSManager manager = null;
	//ネットワークログ
	private MonitorManager monitorManager = MonitorManager.getMonitorManager();

	private static final String DEFAULT_CHARACTER_ENCODING = BBSConfiguration.DEFAULT_ENCODING;

	public WriteHandler(BBSManager manager) {
		this.manager = manager;
	}

	public void handle(HttpExchange he) throws IOException {
		//返信用BODY
		OutputStream os = he.getResponseBody();
		//要求URI
		String path = he.getRequestURI().getPath();
		//delim = /
		String[] splitStr = path.split("/");
		//返信メッセージ
		byte[] responseBody = null;
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
				//パスワード
				String password = "";

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
							topicID = s2;
						}else if( s1.equals("author") ) {
							commentElem.setAuthor(HTMLEncode.encode( s2 ));
						}else if( s1.equals("mail") ) {
							commentElem.setMailAddress(HTMLEncode.encode( s2 ));
						}else if( s1.equals("message") ) {
							commentElem.setMessage( HTMLEncode.encode( s2 ) );
						}else if( s1.equals("category") ) {
							categoryID = s2;
						}else if( s1.equals("password") ) {
							commentElem.setPassword( Digest.getStringDigest(s2) );
							password = HTMLEncode.encode(s2);
						}else if( s1.equals("user") ) {
							userMode = true;
						}
					}
				}
				//正しい情報を受信していない
				if( topicID == null || categoryID == null || commentElem.getMessage() == null ) {
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
				//受信データログ記録
				monitorManager.addMessageLog("TOPIC_RECEIVE",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), buf.length(),
						"WRITE");

				//トピック作成
				boolean res = manager.write(categoryID, topicID, commentElem, password);
				if( userMode ) {
					if( res ) {
						//要求受付を完了したことを知らせる
						String tmp = UsermodeHTML.writeRequestResponse(true);
						responseBody = tmp.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}else {
						// 何らかの理由で書き込みに失敗した時
						responseBody = UsermodeHTML.writeRequestResponse(false)
								.getBytes(DEFAULT_CHARACTER_ENCODING);
						responseCode = 200;
					}
				}else {
				if (res) {
						// 要求受付を完了したことを知らせる
						// os.write("ACCEPT".getBytes());
						responseBody = "ACCEPT".getBytes();
					} else {
						// 何らかの理由で書き込みに失敗した時
						// os.write("REJECT".getBytes());
						responseBody = "REJECT".getBytes();
					}
				}

				//受信データログ記録
				monitorManager.addMessageLog("TOPIC_SEND",
						he.getRemoteAddress().getAddress().getHostAddress()
						+ ":" + he.getRemoteAddress().getPort(), 6,
						"WRITE");

				logger.info((res ? "Accept" : "Reject") + " write request from "
						+ he.getRemoteAddress().getAddress().getHostAddress()
						+ " category: " + categoryID + " topic: " + topicID);
			}else {
				throw new Exception("Bad request.");
			}
		} catch (MaxNumberOfCommentException e) {
			e.printStackTrace();
			// 間違った要求 400 Bad Request
			responseCode = 400;
			if (userMode) {
				responseBody = UsermodeHTML.manageRequestErrorMessage("トピックへこれ以上書き込めません．")
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		} catch (MaxLengthOfCommentException e) {
			e.printStackTrace();
			// 間違った要求 400 Bad Request
			responseCode = 400;
			if (userMode) {
				responseBody = UsermodeHTML.manageRequestErrorMessage("1コメントあたりの文字数が長すぎます．")
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		} catch (MaxLengthOfAuthorException e) {
			e.printStackTrace();
			// 間違った要求 400 Bad Request
			responseCode = 400;
			if (userMode) {
				responseBody = UsermodeHTML.manageRequestErrorMessage("作者名が長すぎます．")
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		} catch (MaxLengthOfMailException e) {
			e.printStackTrace();
			// 間違った要求 400 Bad Request
			responseCode = 400;
			if (userMode) {
				responseBody = UsermodeHTML.manageRequestErrorMessage("メールアドレスが長すぎます．")
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		} catch (TopicNotFoundException e) {
			e.printStackTrace();
			// 間違った要求 400 Bad Request
			responseCode = 400;
			if (userMode) {
				responseBody = UsermodeHTML.manageRequestErrorMessage("該当トピックが見つかりませんでした．")
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		}catch (Exception e) {
			e.printStackTrace();
			if (userMode) {
				responseCode = 400;
				responseBody = UsermodeHTML.writeRequestResponse(false)
					.getBytes(DEFAULT_CHARACTER_ENCODING);
			} else {
				// 間違った要求 400 Bad Request
				// he.sendResponseHeaders(400, 0);
				responseCode = 400;
				// os.write("Bad write Request.".getBytes());
				responseBody = "Bad write Request.".getBytes();
				logger.info("Bad write Request from "
						+ he.getRemoteAddress().getAddress().getHostAddress());
			}
		}
		
		// response Header
		he.sendResponseHeaders(responseCode, responseBody.length);
		// body 送信
		os = he.getResponseBody();
		if (responseBody != null) {
			os.write(responseBody);
		}

		// 接続を切る
		os.close();
		he.close();
	}
}
