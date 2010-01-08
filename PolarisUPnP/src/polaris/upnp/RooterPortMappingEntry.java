package polaris.upnp;

/**
 * ポートマッピングのエントリー
 * @author nishio
 *
 */
public class RooterPortMappingEntry {
	private String newRemoteHost = null;
	private String newExternalPort = null;
	private String newProtocol = null;
	private String newInternalPort = null;
	private String newInternalClient = null;
	private String newEnabled = null;
	private String newPortMappingDescription = null;
	private String newLeaseDuration = null;
	
	/**
	 * 
	 */
	public RooterPortMappingEntry() {
		
	}
	
	/**
	 * 
	 * @param newRemoteHost
	 * @param newExternalPort
	 * @param newProtocol
	 * @param newInternalPort
	 * @param newInternalClient
	 * @param newEnabled
	 * @param newPortMappingDescription
	 */
	public RooterPortMappingEntry(String newRemoteHost, String newExternalPort, String newProtocol,
			String newInternalPort, String newInternalClient, String newEnabled,
			String newPortMappingDescription, String newLeaseDuration) {
		this.newRemoteHost = newRemoteHost;
		this.newExternalPort = newExternalPort;
		this.newProtocol = newProtocol;
		this.newInternalPort = newInternalPort;
		this.newInternalClient = newInternalClient;
		this.newEnabled = newEnabled;
		this.newPortMappingDescription = newPortMappingDescription;
		this.newLeaseDuration = newLeaseDuration;
	}
	/**
	 * newRemoteHostを取得します。
	 * @return newRemoteHost
	 */
	public String getNewRemoteHost() {
	    return newRemoteHost;
	}
	/**
	 * newRemoteHostを設定します。
	 * @param newRemoteHost newRemoteHost
	 */
	public void setNewRemoteHost(String newRemoteHost) {
	    this.newRemoteHost = newRemoteHost;
	}
	/**
	 * newExternalPortを取得します。
	 * @return newExternalPort
	 */
	public String getNewExternalPort() {
	    return newExternalPort;
	}
	/**
	 * newExternalPortを設定します。
	 * @param newExternalPort newExternalPort
	 */
	public void setNewExternalPort(String newExternalPort) {
	    this.newExternalPort = newExternalPort;
	}
	/**
	 * newProtocolを取得します。
	 * @return newProtocol
	 */
	public String getNewProtocol() {
	    return newProtocol;
	}
	/**
	 * newProtocolを設定します。
	 * @param newProtocol newProtocol
	 */
	public void setNewProtocol(String newProtocol) {
	    this.newProtocol = newProtocol;
	}
	/**
	 * newInternalPortを取得します。
	 * @return newInternalPort
	 */
	public String getNewInternalPort() {
	    return newInternalPort;
	}
	/**
	 * newInternalPortを設定します。
	 * @param newInternalPort newInternalPort
	 */
	public void setNewInternalPort(String newInternalPort) {
	    this.newInternalPort = newInternalPort;
	}
	/**
	 * newInternalClientを取得します。
	 * @return newInternalClient
	 */
	public String getNewInternalClient() {
	    return newInternalClient;
	}
	/**
	 * newInternalClientを設定します。
	 * @param newInternalClient newInternalClient
	 */
	public void setNewInternalClient(String newInternalClient) {
	    this.newInternalClient = newInternalClient;
	}
	/**
	 * newEnabledを取得します。
	 * @return newEnabled
	 */
	public String getNewEnabled() {
	    return newEnabled;
	}
	/**
	 * newEnabledを設定します。
	 * @param newEnabled newEnabled
	 */
	public void setNewEnabled(String newEnabled) {
	    this.newEnabled = newEnabled;
	}
	/**
	 * newPortMappingDescriptionを取得します。
	 * @return newPortMappingDescription
	 */
	public String getNewPortMappingDescription() {
	    return newPortMappingDescription;
	}
	/**
	 * newPortMappingDescriptionを設定します。
	 * @param newPortMappingDescription newPortMappingDescription
	 */
	public void setNewPortMappingDescription(String newPortMappingDescription) {
	    this.newPortMappingDescription = newPortMappingDescription;
	}
	/**
	 * newLeaseDurationを取得します。
	 * @return newLeaseDuration
	 */
	public String getNewLeaseDuration() {
	    return newLeaseDuration;
	}

	/**
	 * newLeaseDurationを設定します。
	 * @param newLeaseDuration newLeaseDuration
	 */
	public void setNewLeaseDuration(String newLeaseDuration) {
	    this.newLeaseDuration = newLeaseDuration;
	}
	
	public String toString() {
		return "NewRemoteHost: " + this.getNewRemoteHost() +
			" NewExternalPort: " + this.getNewExternalPort() + 
			" NewProtocol: " + this.getNewProtocol() + 
			" NewInternalPort: " + this.getNewInternalPort() +
			" NewInternalClient: " + this.getNewInternalClient() +
			" NewEnabled: " + this.getNewEnabled() + 
			" NewPortMappingDescription: " + this.getNewPortMappingDescription() +
			" NewLeaseDuration: " + this.getNewLeaseDuration();
	}

}
