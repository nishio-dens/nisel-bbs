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
	public static void main(String[] args) {
		new TestDriver2().main2();
	}
	
	public static final String testMessage = "aaa http://test.com/ HELLOWORLD HELLO2WORLDhttp://bbs.com/index.htmlです";
	public void main2() {
		try {
			//URL
			Pattern pattern = Pattern.compile("(http://|https://){1}[\\w\\.\\-/:]+");
			Matcher matcher = pattern.matcher( testMessage.toString() );
			StringBuffer sb = new StringBuffer();
			int lastnum = 0;
			while( matcher.find() ) {
				matcher.appendReplacement(sb, "<a href=\"" + matcher.group() 
						+ "\">" + matcher.group() + "</a>");
				lastnum = matcher.end();
				System.out.println("LASTNUM:" + lastnum);
			}
			System.out.println(sb.toString() + "\n======\n" + testMessage.substring(lastnum));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
