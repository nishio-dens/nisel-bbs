package polaris.upnp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import polaris.upnp.action.RooterControl;
import polaris.upnp.device.BadDeviceURIException;
import polaris.upnp.device.BadDeviceXMLException;
import polaris.upnp.device.RooterDeviceDescriptionAnalyzer;
import polaris.upnp.device.RooterDeviceServiceElement;
import polaris.upnp.discover.RooterDiscovery;
import polaris.upnp.discover.RooterDiscoveryEvent;
import polaris.upnp.discover.RooterDiscoveryListener;

public class UPnPManager implements RooterDiscoveryListener{
	//ルータ発見
	private RooterDiscovery rooterDiscovery = null;
	//ルータの情報
	private RooterInformation rooterInformation = null;
	//ルータ情報解析
	private RooterDeviceDescriptionAnalyzer rooterAnalyzer = null;
	//エラーが発生しているかどうか
	private boolean error = false;
	
	private static Logger logger = Logger.getLogger("polaris.upnp");
	
	/**
	 * 
	 */
	public UPnPManager() {
		this.rooterDiscovery = new RooterDiscovery();
		//ルータ情報発見に変化が起きたときに通知
		this.rooterDiscovery.addRooterDiscoveryListener(this);
	}
	
	/**
	 * ルータ情報取得を開始します．
	 */
	public void discoverRooter() {
		this.error = false;
		this.rooterDiscovery.discover();
	}
	
	/**
	 * ルータから情報を受け取るポートを指定します．
	 * @param port
	 */
	public void setDiscoveryPort(int port) {
		this.rooterDiscovery.setSourcePort(port);
	}
	
	/**
	 * ルータ情報発見が終わっているかどうか
	 * @return
	 * @throws RooterDeviceErrorException
	 */
	public boolean isFinishedDiscovery() throws RooterDeviceErrorException {
		if( error ) {
			throw new RooterDeviceErrorException("ルータ情報取得中にエラーが発生しました．");
		}
		if( rooterInformation == null || rooterAnalyzer == null) {
			return false;
		}
		logger.info("UPnP対応デバイスを発見しました．");
		logger.info("ルータ情報URI:" + rooterInformation.getLocation() );
		return true;
	}
	
	/**
	 * グローバルIPアドレスを取得します
	 * @return
	 * @throws RooterDeviceErrorException
	 * @throws NullPointerException
	 */
	public String getExternalIPAddress() throws RooterDeviceErrorException, NullPointerException {	
		String externalAddress = null;
		try {
			logger.info("グローバルIPアドレスの取得を開始します.");
			//すべてのサービスを取得
			List<RooterDeviceServiceElement> allServices = rooterAnalyzer.getDeviceService();
			//Rooter actionに関係のあるサービスだけを抜き出す
			List<RooterDeviceServiceElement> services = rooterAnalyzer.getConnectionService(allServices);
			//ルータ制御アドレス
			String location = rooterInformation.getLocation();
			//例) http://192.168.1.1:2869/upnp/ から http://192.168.1.1:2869 を抜き出す
			String[] splitLocation = location.split("/");
			String rooterControlAddress = "http://" + splitLocation[2];
			//ルータ制御
			RooterControl control = new RooterControl(rooterControlAddress, services);
			//外部アドレスを取得
			externalAddress = control.getExternalIPAddress();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RooterDeviceErrorException("ルータ情報取得中にエラーが発生しました．");
		}
		return externalAddress;
	}
	
	/**
	 * 指定したインデクスにあるUPnPエントリーを取得
	 * @param index
	 * @return
	 * @throws RooterDeviceErrorException
	 * @throws NullPointerException
	 */
	public RooterPortMappingEntry getGenericPortMappingEntry(int index) throws RooterDeviceErrorException, NullPointerException {	
		RooterPortMappingEntry entry = null;
		try {
			logger.info( index + "番目のエントリーに存在するUPnP情報を取得します．");
			//すべてのサービスを取得
			List<RooterDeviceServiceElement> allServices = rooterAnalyzer.getDeviceService();
			//Rooter actionに関係のあるサービスだけを抜き出す
			List<RooterDeviceServiceElement> services = rooterAnalyzer.getConnectionService(allServices);
			//ルータ制御アドレス
			String location = rooterInformation.getLocation();
			//例) http://192.168.1.1:2869/upnp/ から http://192.168.1.1:2869 を抜き出す
			String[] splitLocation = location.split("/");
			String rooterControlAddress = "http://" + splitLocation[2];
			//ルータ制御
			RooterControl control = new RooterControl(rooterControlAddress, services);
			//UPnPエントリーを取得
			entry = control.getGenericPortMappingEntry(index);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RooterDeviceErrorException("ルータ情報取得中にエラーが発生しました．");
		}
		return entry;
	}
	
	/**
	 * UPnPエントリーをすべて取得
	 * @return
	 */
	public List<RooterPortMappingEntry> getGenericPortMappingEntry() throws RooterDeviceErrorException, NullPointerException {
		ArrayList<RooterPortMappingEntry> entries = new ArrayList();
		RooterPortMappingEntry entry = null;
		int index = 0;
		entry = getGenericPortMappingEntry(index);
		while( entry != null ) {
			entries.add(entry);
			index++;
			entry = getGenericPortMappingEntry(index);
		}
		
		return entries;
	}
	
	/**
	 * ポートマッピングを行う
	 * @param newExternalPort 外部ポート
	 * @param newProtocol プロトコル UDPかTCP
	 * @param newInternalPort 内部ポート
	 * @param newInternalClient 内部クライアントIP
	 * @param portMappingDescription ポートマッピングの概要
	 * @return ポートマッピングが成功したらtrue
	 * @throws RooterDeviceErrorException
	 * @throws NullPointerException
	 */
	public boolean addPortMapping(int newExternalPort, String newProtocol, int newInternalPort,
	String newInternalClient, String portMappingDescription ) throws RooterDeviceErrorException, NullPointerException{
		boolean error = true;
		try {
			logger.info("ポートマッピングを開始します．newExternalPort: " + newExternalPort + " newProtocol: " + newProtocol
					+ " newInternalPort: " + newInternalPort + " newInternalClient: " + newInternalClient
					+ " portMappingDescription: " + portMappingDescription);
			//すべてのサービスを取得
			List<RooterDeviceServiceElement> allServices = rooterAnalyzer.getDeviceService();
			//Rooter actionに関係のあるサービスだけを抜き出す
			List<RooterDeviceServiceElement> services = rooterAnalyzer.getConnectionService(allServices);
			//ルータ制御アドレス
			String location = rooterInformation.getLocation();
			//例) http://192.168.1.1:2869/upnp/ から http://192.168.1.1:2869 を抜き出す
			String[] splitLocation = location.split("/");
			String rooterControlAddress = "http://" + splitLocation[2];
			//ルータ制御
			RooterControl control = new RooterControl(rooterControlAddress, services);
			//ポートマッピングに成功したかどうか
			error = control.addPortMapping(newExternalPort, newProtocol, newInternalPort, newInternalClient, portMappingDescription);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RooterDeviceErrorException("ルータ情報取得中にエラーが発生しました．");
		}
		return !error;
	}
	
	/**
	 * 指定したポートを閉じる
	 * @param externalPort
	 * @param protocol
	 * @return ポート削除に成功したらtrue
	 * @throws RooterDeviceErrorException
	 * @throws NullPointerException
	 */
	public boolean deletePortMapping(int externalPort, String protocol) 
	throws RooterDeviceErrorException, NullPointerException{
		boolean error = true;
		try {
			logger.info("指定したポートマッピングを削除します． externalPort: " + externalPort
					+ " protocol: " + protocol);
			//すべてのサービスを取得
			List<RooterDeviceServiceElement> allServices = rooterAnalyzer.getDeviceService();
			//Rooter actionに関係のあるサービスだけを抜き出す
			List<RooterDeviceServiceElement> services = rooterAnalyzer.getConnectionService(allServices);
			//ルータ制御アドレス
			String location = rooterInformation.getLocation();
			//例) http://192.168.1.1:2869/upnp/ から http://192.168.1.1:2869 を抜き出す
			String[] splitLocation = location.split("/");
			String rooterControlAddress = "http://" + splitLocation[2];
			//ルータ制御
			RooterControl control = new RooterControl(rooterControlAddress, services);
			//ポート削除ができたか
			error = control.deletePortMapping(externalPort, protocol);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RooterDeviceErrorException("ルータ情報取得中にエラーが発生しました．");
		}
		return !error;
	}

	/**
	 * ルータ情報取得のイベントに何か変化があったときに通知される
	 * @param e 
	 */
	@Override
	public void rooterInformationChanged(RooterDiscoveryEvent e) {
		if( e.getSource().isError() ) {
			error = true; //エラーが発生した
			logger.warning( e.getSource().getErrorInformation() );
		}else {
			error = false;
			logger.info("ルータ情報を取得しました.");
			//ルータ情報取得
			rooterInformation = e.getSource().getRooterInformation();
			//ルータ情報解析
			rooterAnalyzer = new RooterDeviceDescriptionAnalyzer( rooterInformation.getLocation() );
		}
	}	
	

}
