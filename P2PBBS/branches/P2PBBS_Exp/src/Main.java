import bbs.server.BBSServer;


public class Main {
	public static void main(String[] args) {
		if( args.length < 5 ) {
			System.err.println("Invalid argument."
				+	"[initAddress], [statCollectAddress],"
				+   "[selfNodeAddress], [dhtport], [serverPort]");
		}
		if( args[0].equals("null") ) {
			args[0] = null;
		}
		if( args[1].equals("null") ) {
			args[1] = null;
		}
		if( args[2].equals("null") ) {
			args[2] = null;
		}
		BBSServer server = new BBSServer();
		try {
			server.start(args[0], args[1], args[2], Integer.parseInt(args[3]),
					Integer.parseInt(args[4]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
