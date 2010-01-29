import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbs.dht.DHTManager;
import bbs.dht.InitDHTException;
import bbs.http.HttpClientSocket;
import bbs.monitor.MessageLog;
import bbs.monitor.MonitorManager;


public class TestDriver {

	/*public static void main(String[] args) {
		if(args.length < 2 ) {
			System.out.println("invalid arguments.");
			System.out.println("TestDriver selfAddress port contactAddress");
			return;
		}
		String contactAddress = null;
		if( args.length >= 3 ) {
			contactAddress = args[2];
		}
		String selfAddress = args[0];
		int port = Integer.parseInt(args[1]);
		
		DHTManager manager = new DHTManager();
		MonitorManager monitor = MonitorManager.getMonitorManager();
		try {
			manager.init(contactAddress, null, selfAddress, port);
			Thread.sleep(3000);
			System.out.println("Key :hello, value :" + manager.get("hello") );
			//manager.put("hello", "world");
			for(int i=0; i < 5; i++ ) {
				Set<MessageLog> rlogs = monitor.getMessages("RECEIVE_OVERLAY_WEAVER_MESSAGE");
				Set<MessageLog> slogs = monitor.getMessages("SEND_OVERLAY_WEAVER_MESSAGE");
				
				System.out.println("receive: " + 
						monitor.getMessageSize("RECEIVE_OVERLAY_WEAVER_MESSAGE"));
				System.out.println("send: " + 
						monitor.getMessageSize("SEND_OVERLAY_WEAVER_MESSAGE"));
				
				System.out.println("RECEIVE_OVERLAY_WEAVER_MESSAGE");
				for(MessageLog log: rlogs ) {
					System.out.println( log );
				}
				System.out.println("SEND_OVERLAY_WEAVER_MESSAGE");
				for(MessageLog log: slogs ) {
					System.out.println( log );
				}
				Thread.sleep(7000);
			}
		} catch (InitDHTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	public static void main(String[] args) {
		try {
			System.out.println("IP:" + getGlobalAddress(null, 0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getGlobalAddress(String proxyHost, int proxyPort) {
		/*try {
			InetAddress ia = InetAddress.getLocalHost();
			boolean isLocal = ia.isAnyLocalAddress();
			isLocal |= ia.isLinkLocalAddress();
			isLocal |= ia.isLoopbackAddress();
			isLocal |= ia.isMulticastAddress();
			isLocal |= ia.isSiteLocalAddress();
			if (!isLocal) {
				// グローバルアドレスである。
				return ia.getHostAddress();
			}
		} catch (UnknownHostException e) {
		}*/
		// ローカルアドレスだったので、外部から取得する（IPv4 専用）。
		String[] ipGetPage = { "http://www.cman.jp/network/support/go_access.cgi",
				"http://www.google.com/",
				"http://www.cybersyndrome.net/evc.html"};
		final Pattern ptnIP = Pattern.compile("(¥¥d{1,3}(¥¥.¥¥d{1,3}){3})");
		final Pattern ptnCharSet = Pattern.compile("charset[ \\t]*=[ \\t]*\"?([^\">]+)");
		Proxy proxy = null;
		if (null != proxyHost && proxyHost.trim().length() >= 1
				&& proxyPort >= 0 && proxyPort <= 65535) {
			proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
		}
		byte[] buf = new byte[1024];
		Map<String, Integer> mapIp = new TreeMap<String, Integer>();
		for (String host : ipGetPage) {
			mapIp.clear();
			URL u;
			try {
				u = new URI(host).toURL();
			} catch (Exception e) {
				//Util.logWrite(log, e, Level.FINER);
				e.printStackTrace();
				continue;
			}
			try {
				URLConnection uc = null == proxy ? u.openConnection() : u.openConnection(proxy);
				uc.setReadTimeout(3 * 1000);
				uc.setConnectTimeout(3 * 1000);
				uc.connect();
				InputStream in = uc.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while (true) {
					int n = in.read(buf);
					if (n <= 0) {
						break;
					}
					baos.write(buf, 0, n);
				}
				in.close();
				baos.close();
				final byte[] webData = baos.toByteArray();
				// charset を取得する。
				String s = new String(webData);
				Matcher m = ptnCharSet.matcher(s);
				if (m.find()) {
					s = m.group(1);
					s = new String(webData, s);
				}
				//TODO:delete
				System.out.println("GET:" + s );
				// IP アドレスと思しき部分を検索し、収集する。
				m = ptnIP.matcher(s);
				while (m.find()) {
					s = m.group(1);
					if (mapIp.containsKey(s)) {
						mapIp.put(s, mapIp.get(s) + 1);
					} else {
						mapIp.put(s, 1);
					}
				}
				// もっとも出現回数が多かった値を使う。
				int max = 0;
				s = null;
				for (Map.Entry<String, Integer> i : mapIp.entrySet()) {
					if (i.getValue() > max) {
						max = i.getValue();
						s = i.getKey();
					}
				}
				return s;
			} catch (Exception e) {
				e.printStackTrace();
				// TODO エラー
				//log.warning("Error. [" + u + ']');
				//Util.logWrite(log, e, Level.WARNING);
			}
		}
		return null;
	}
}
