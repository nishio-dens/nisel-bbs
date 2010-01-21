package polaris.server;

import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class PolarisServer implements Runnable {
	
	// ユーザの窓口となるソケット
	private Socket sock_ = null;

	
	// サーバ起動時に実行する部分
	public static void main( String[] args )
	{
		try {
			// サーバソケットの作成
			ServerSocket svsock = new ServerSocket( 8080 );
			svsock.setReuseAddress(true);
			while( true )
			{
				// クライアントからの接続を受け付ける
				Socket sock = svsock.accept();
				sock.setKeepAlive(false);
				sock.setTcpNoDelay(true);
				System.out.println("Accept ok");
				PolarisServer sv = new PolarisServer(sock);
				Thread tr = new Thread(sv);
				System.out.println("Thread Start");
				tr.start();
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

	public PolarisServer() {
		
	}
	// インスタンス作成時
	public PolarisServer(Socket sock)
	{
		this.sock_ = sock;
	}
	
	// インスタンス破棄時
	protected void finalize()
	{
		try {
			sock_.close();
		} catch (Exception e) {}
	}


	// スレッド化した時に実行する部分
	public void run()
	{
		
		// -------- リクエストを受信 --------

		
		String s = null;
		String from_user = null;
		try{
			BufferedReader in = new BufferedReader( new InputStreamReader( this.sock_.getInputStream() ) );
			if( (s = in.readLine()).length() > 0 )
			{
				// 要求ファイル名を切り出す
				// 例： GET /example/request/ HTTP/1.1
				from_user = s.split(" ")[ 1 ];
				from_user = from_user.substring( 1, from_user.length() );
			}
		}
		catch( IOException e )
		{
			System.out.println("読み取りエラー");
		}

		
		// -------- ファイル読み取り --------

		
		/*StringBuilder sb_msg = new StringBuilder();
		try{
			// バッチに渡す
			Process process = Runtime.getRuntime().exec( "response.bat \"" + from_user + "\"" );
			InputStream is = process.getInputStream();
			BufferedReader br = new BufferedReader( new InputStreamReader( is ) );
			String line;
			while ( ( line = br.readLine() ) != null )
			{
				sb_msg.append( line + "\r\n" );
			}
		}
		catch( IOException e )
		{
			System.out.println("外部コマンドエラー");
		}*/
		//重い処理
		LinkedList<String> test = new LinkedList<String>();
		for(int i=0; i < 70000; i++) {
			test.add("AAAAAA");
		}
		System.out.println("TEST HANDLER ADD OK");
		for(int i=0; i < 70000; i++) {
			test.get(i);
		}
		System.out.println("TEST HANDLER GET OK");
		
		String msg = new String( "HELLOWORLD" );
		
		
		// -------- レスポンスを送信 --------
		
		
		try {

			DataOutputStream out = new DataOutputStream( sock_.getOutputStream() );
			
			// 送信文字列
			StringBuilder sb = new StringBuilder();
			sb.append( "HTTP/1.1 200 OK\r\n" );
			sb.append( "Connection: close\r\n" );
			sb.append( "Content-Type: text/html; charset=Shift_JIS\r\n" );
			sb.append( "\r\n");
			sb.append( msg );
			sb.append( "\r\n" );
			String str = new String( sb );
			byte[] b = str.getBytes();

			// 送信
			out.write( b, 0, b.length );
			sock_.close();

		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}

}


