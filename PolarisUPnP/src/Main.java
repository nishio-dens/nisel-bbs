import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXParseException;

import polaris.upnp.RooterDeviceErrorException;
import polaris.upnp.RooterPortMappingEntry;
import polaris.upnp.UPnPManager;
import polaris.upnp.action.RooterControl;
import polaris.upnp.device.BadDeviceURIException;
import polaris.upnp.device.RooterDeviceDescriptionAnalyzer;
import polaris.upnp.device.RooterDeviceServiceElement;
import polaris.upnp.device.RooterDeviceServiceXMLParser;
import polaris.upnp.device.RooterRootElement;
import polaris.upnp.discover.RooterDiscovery;


public class Main {
	
	private static Logger logger = Logger.getLogger("polaris.upnp");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		UPnPManager manager = new UPnPManager();
		manager.discoverRooter();
		
		String address = null;
		boolean isPortMapping = false;
		try{
			for(;;) {
				try{
					boolean finish = manager.isFinishedDiscovery();
					logger.info("ルータを見つけています.");
					if( finish == true ) {
						break;
					}
					Thread.sleep(100);
				}catch(NullPointerException e) {
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			address = manager.getExternalIPAddress();
			isPortMapping = manager.addPortMapping(3997, "UDP", 3997, "192.168.12.4", "Port mapping test.");
			List<RooterPortMappingEntry> entries = manager.getGenericPortMappingEntry();
			for(RooterPortMappingEntry entry : entries ) {
				System.out.println( entry );
			}
			isPortMapping = manager.deletePortMapping(3997, "UDP");
		}catch(RooterDeviceErrorException e) {
			e.printStackTrace();
		}
		
		if( isPortMapping ) {
			System.out.println("Port mapping succeeded.");
		}else{
			System.out.println("Port mapping failed.");
		}
		System.out.println("外部アドレス: " + address );
	}

}
