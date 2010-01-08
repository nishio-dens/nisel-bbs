package polaris.upnp.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * HTTP socket
 * @author nishio
 *
 */
public class HttpClientSocket {
	private Map<String,String> header = null;
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final int DEFAULT_TIMEOUT = 2 * 1000; //2 second
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * 
	 */
	public HttpClientSocket() {
		header = new HashMap<String, String>();
	}
	
	/**
	 * データの送受信を行います
	 * @param path 接続先パス
	 * @param host 接続先
	 * @param port 接続先ポート
	 * @param command GETかPOST
	 * @param body 本文
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String sendAndReceive(String path, String host, int port, String command, String body) 
	throws UnknownHostException, IOException {
		String retValue = null;
		//httpソケット
		Socket socket = new Socket(host, port);
		socket.setSoTimeout( timeout );
		//データ送信用
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(socket.getOutputStream(), DEFAULT_CHARSET) );
		//データ受信用
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(socket.getInputStream(), DEFAULT_CHARSET) );
		
		//コマンド送信
		writer.write(command + " " + path + " HTTP/1.1\r\n");
		writer.write( this.createHeader( socket.getLocalAddress().getHostAddress(), socket.getLocalPort(), body) );
		writer.write(body);
		writer.flush();

		StringBuffer buf = new StringBuffer("");
		//データ受信
		String line;
		//TODO: ここを書き直す SocketTimeoutはかならず起こるので，なんとかしてEOF判定してループ脱出を図りたい．
		try {
			while( (line = reader.readLine()) != null ) {
				buf.append(line + "\n");
				/*if( reader.ready() == false ) {
					break;
				}*/
			}			
		}catch(SocketTimeoutException e) {
			//無視 これはひどい
		}
		
		//書き込み接続切断
		writer.close();
		//接続切断
		reader.close();
		socket.close();

		retValue = buf.toString();
		return retValue;
	}
	
	/**
	 * ヘッダ要素を追加します．
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		header.put(key, value);
	}
	
	/**
	 * タイムアウトまでの時間を設定
	 * @param timeout
	 */
	public void setSoTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * ヘッダ文字列を生成します
	 * @param host
	 * @param port
	 * @param body
	 * @return
	 */
	private String createHeader(String host, int port, String body) {
		//ヘッダ文字列
		String headerStr = null;
		try {
			headerStr = "HOST: " + host + ":" + port + "\r\n";
			headerStr += "CONTENT-LENGTH: " + body.getBytes( DEFAULT_CHARSET ).length + "\r\n";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//ヘッダ一覧
		Set<String> headerKeys = header.keySet();
		for( String key : headerKeys ) {
			String value = header.get(key);
			headerStr += key + ": " + value + "\r\n";
		}
		headerStr += "\r\n";
		
		return headerStr;		
	}

}
