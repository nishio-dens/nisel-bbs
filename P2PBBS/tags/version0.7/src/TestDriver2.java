import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import polaris.server.PolarisServer;

import bbs.comment.CommentElement;
import bbs.comment.LocalTopicManager;
import bbs.comment.TopicManager;
import bbs.http.HttpClientSocket;
import bbs.monitor.MessageLog;
import bbs.monitor.MonitorManager;
import bbs.xml.BBSXMLParser;


public class TestDriver2 {
	/*public static void main(String[] args) {
		new TestDriver2().main2();
	}
	public void main2() {
		try {
			new TestServer().start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static void main(String[] args) {
		new TestDriver2().main2();
	}
	public void main2() {
		try {
			new PolarisServer();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
