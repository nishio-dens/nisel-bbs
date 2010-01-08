package polaris.upnp.gui;

import java.util.List;

import javax.swing.JLabel;

import polaris.upnp.RooterDeviceErrorException;
import polaris.upnp.RooterPortMappingEntry;
import polaris.upnp.UPnPManager;

/**
 * 
 * @author nishio
 *
 */
public class UPnP {
	
	private UPnPManager manager = null;
	private UPnPTableModel tableModel = null;
	//外部アドレス
	private String externalAddress = null;
	//ルータ探索スレッド
	private Thread discoveryThread = null;
	//ポートマッピングスレッド
	private Thread addPortMappingThread = null;
	//ポートマッピング削除スレッド
	private Thread deletePortMappingThread = null;
	//情報表示
	private JLabel information = null;
	//外部アドレス
	private JLabel externalAddressLabel = null;
	
	/**
	 * 
	 * @param tableModel
	 */
	public UPnP(UPnPTableModel tableModel, JLabel information, JLabel externalAddressLabel) {
		this.manager = new UPnPManager();
		this.tableModel = tableModel;
		this.information = information;
		this.externalAddressLabel = externalAddressLabel;
	}
	
	/**
	 * ルータ探索
	 */
	public void discover() {
		tableModel.clear();
		discoveryThread = new Thread( new DiscoveryThread() );
		discoveryThread.start();
	}
	
	/**
	 * ポート開放
	 * @param newExternalPort
	 * @param newProtocol
	 * @param newInternalPort
	 * @param newInternalClient
	 * @param portMappingDescription
	 */
	public void addPortMapping(int newExternalPort, String newProtocol, int newInternalPort,
				String newInternalClient, String portMappingDescription ) {
		tableModel.clear();
		addPortMappingThread = new Thread( new AddPortMappingThread(newExternalPort,
				newProtocol, newInternalPort, newInternalClient, portMappingDescription));
		addPortMappingThread.start();
	}
	
	/**
	 * ポート削除
	 * @param externalPort
	 * @param protocol
	 */
	public void deletePortMapping(int externalPort, String protocol) {
		//tableModel.clear();
		deletePortMappingThread = new Thread( new DeletePortMappingThread(externalPort, protocol));
		deletePortMappingThread.start();
	}
	
	/**
	 * 外部アドレスを取得
	 * @return
	 */
	public String getExternalAddress() {
		return externalAddress;
	}

	/**
	 * ルータ情報発見時に利用
	 * @author nishio
	 *
	 */
	private class DiscoveryThread implements Runnable {
		@Override
		public void run() {
			manager.discoverRooter();
			try{
				for(;;) {
					try{
						//ルータ探索
						boolean finish = manager.isFinishedDiscovery();
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
				information.setText("ルータ情報の取得が完了しました.");
				//外部アドレスを取得
				externalAddress = manager.getExternalIPAddress();
				externalAddressLabel.setText( externalAddress );
				information.setText("外部IPアドレスを取得しました.");
				//ポートマッピングエントリーを取得
				List<RooterPortMappingEntry> entries = manager.getGenericPortMappingEntry();
				for(RooterPortMappingEntry entry : entries ) {
					tableModel.add( entry );
				}
				information.setText("UPnPエントリー情報取得完了.");
			}catch(RooterDeviceErrorException e) {
				e.printStackTrace();
				information.setText("エラーが発生しました．");
			}
		}
	}
	
	/**
	 * ポートマッピング時に利用
	 * @author nishio
	 *
	 */
	private class AddPortMappingThread implements Runnable{
		
		private int newExternalPort;
		private String newProtocol;
		private int newInternalPort;
		private String newInternalClient;
		private String portMappingDescription;
		
		/**
		 * 
		 * @param newExternalPort
		 * @param newProtocol
		 * @param newInternalPort
		 * @param newInternalClient
		 * @param portMappingDescription
		 */
		public AddPortMappingThread(int newExternalPort, String newProtocol, int newInternalPort,
				String newInternalClient, String portMappingDescription ) {
			this.newExternalPort = newExternalPort;
			this.newProtocol = newProtocol;
			this.newInternalPort = newInternalPort;
			this.newInternalClient = newInternalClient;
			this.portMappingDescription = portMappingDescription;
		}

		@Override
		public void run() {
			boolean isPortMapping = false;
			try {
				isPortMapping = manager.addPortMapping(newExternalPort, newProtocol,
						newInternalPort, newInternalClient, portMappingDescription);
				
				//ポートマッピングエントリーを取得
				List<RooterPortMappingEntry> entries = manager.getGenericPortMappingEntry();
				for(RooterPortMappingEntry entry : entries ) {
					tableModel.add( entry );
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (RooterDeviceErrorException e) {
				e.printStackTrace();
			}
			
			if( isPortMapping ) {
				information.setText("ポート追加に成功しました.");
			}else {
				information.setText("ポート開放に失敗しました.");
			}
		}
		
	}
	
	/**
	 * ポート削除用スレッド
	 * @author nishio
	 *
	 */
	private class DeletePortMappingThread implements Runnable{

		private int newExternalPort;
		private String protocol;
		
		/**
		 * 
		 * @param newExternalPort
		 * @param protocol
		 */
		public DeletePortMappingThread(int newExternalPort, String protocol) {
			this.newExternalPort = newExternalPort;
			this.protocol = protocol;
		}
		
		@Override
		public void run() {
			boolean isPortMapping = false;
			try {
				isPortMapping = manager.deletePortMapping(newExternalPort,protocol);
				
				//ポートマッピングエントリーを取得
				/*List<RooterPortMappingEntry> entries = manager.getGenericPortMappingEntry();
				for(RooterPortMappingEntry entry : entries ) {
					tableModel.add( entry );
				}*/
			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (RooterDeviceErrorException e) {
				e.printStackTrace();
			}
			
			if( isPortMapping ) {
				information.setText("ポート削除に成功しました.");
			}else {
				information.setText("ポート削除に失敗しました.");
			}
		}
		
	}

}
