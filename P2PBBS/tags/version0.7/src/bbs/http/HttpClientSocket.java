package bbs.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import bbs.BBSConfiguration;

/**
 * 
 * @author nishio
 *
 */
public class HttpClientSocket {
	private static final String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	private static final int DEFAULT_TIMEOUT = 5 * 1000; //5 seconds
	private int timeout = DEFAULT_TIMEOUT;
	
	/**
	 * 
	 */
	public HttpClientSocket() {
	}
	
	/**
	 * データの送受信を行います
	 * @param url 接続先
	 * @param command GETかPOST
	 * @param body 本文
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public String sendAndReceive(String url, String command, String body) 
	throws UnknownHostException, IOException {
		//アクション送信先アドレス
		URL actionURL = new URL( url );
		//コネクション開始
		HttpURLConnection connection = (HttpURLConnection)actionURL.openConnection();
		//POST要求
		connection.setRequestMethod( command );
		connection.setDoOutput(true);
		connection.setDoInput(true);
		//タイムアウト時間を指定
		connection.setConnectTimeout( timeout );
		connection.setReadTimeout( timeout );
		//Connect
		connection.connect();
		//命令送信
		OutputStream ostream = connection.getOutputStream();
		OutputStreamWriter osw = new OutputStreamWriter( ostream , DEFAULT_ENCODING);
		//BufferedReader isr = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
		osw.write(body);
		osw.flush();
		//接続切断
		ostream.close();
		osw.close();
		//レスポンスを受け取る
		InputStream istream = connection.getInputStream();
		BufferedReader isr = new BufferedReader( new InputStreamReader( istream, DEFAULT_ENCODING ) );
		StringBuffer buf = new StringBuffer("");
		String str;

		while( (str = isr.readLine() ) != null ) {
			buf.append(str+"\n");
		}
		istream.close();
		isr.close();
		connection.disconnect();
		return buf.toString();
	}
}
