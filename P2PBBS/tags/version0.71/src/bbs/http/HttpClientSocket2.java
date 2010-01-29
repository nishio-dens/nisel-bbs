package bbs.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import bbs.BBSConfiguration;

public class HttpClientSocket2 {

	private String DEFAULT_ENCODING = BBSConfiguration.DEFAULT_ENCODING;
	
	public String post(String uri, String body) {
		HttpPost httpPost = null;
		try {
			HttpClient client = new DefaultHttpClient();
			httpPost = new HttpPost(uri);
			//bodyをリクエストボディに設定
			httpPost.setEntity(new StringEntity(body));
			//Post送信
			HttpResponse response = client.execute(httpPost);
			//受信データを返す
			return EntityUtils.toString(response.getEntity(), DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if( httpPost != null ) {
				httpPost.abort();
			}
		}
		return null;
	}
}
