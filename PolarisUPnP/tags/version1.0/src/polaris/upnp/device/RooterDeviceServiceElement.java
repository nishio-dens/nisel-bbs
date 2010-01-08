package polaris.upnp.device;

public class RooterDeviceServiceElement {
	//UPnP Service type. must not contain a hash character. single URI.
	private String serviceType = null;
	//service identifier. must be unique within this device description.
	private String serviceId = null;
	//URL for service description.
	private String SCPDURL = null;
	//URL for control. must be relative to the URL at which 
	//the device description is located in accordance with section 5 of RFC 3986.
	//URLはデバイス情報と同じ場所にあれば相対アドレスで，違う場所にあれば
	//絶対アドレスでコントロールURLがかかれている
	private String controlURL = null;
	//URL for eventing. MUST be relative to the URL at which 
	//the device description is located in accordance with section 5 of RFC 3986.
	private String eventSubURL = null;
	
	public RooterDeviceServiceElement() {
		
	}
	
	/**
	 * 
	 * @param serviceType
	 * @param serviceId
	 * @param SCPDURL
	 * @param controlURL
	 * @param eventSubURL
	 */
	public RooterDeviceServiceElement(String serviceType, String serviceId, String SCPDURL, String controlURL, String eventSubURL) {
		this.serviceType = serviceType;
		this.serviceId = serviceId;
		this.SCPDURL = SCPDURL;
		this.controlURL = controlURL;
		this.eventSubURL = eventSubURL;
	}
	/**
	 * serviceTypeを取得します。
	 * @return serviceType
	 */
	public String getServiceType() {
	    return serviceType;
	}
	/**
	 * serviceTypeを設定します。
	 * @param serviceType serviceType
	 */
	public void setServiceType(String serviceType) {
	    this.serviceType = serviceType;
	}
	/**
	 * serviceIdを取得します。
	 * @return serviceId
	 */
	public String getServiceId() {
	    return serviceId;
	}
	/**
	 * serviceIdを設定します。
	 * @param serviceId serviceId
	 */
	public void setServiceId(String serviceId) {
	    this.serviceId = serviceId;
	}
	/**
	 * SCPDURLを取得します。
	 * @return SCPDURL
	 */
	public String getSCPDURL() {
	    return SCPDURL;
	}
	/**
	 * SCPDURLを設定します。
	 * @param SCPDURL SCPDURL
	 */
	public void setSCPDURL(String SCPDURL) {
	    this.SCPDURL = SCPDURL;
	}
	/**
	 * controlURLを取得します。
	 * @return controlURL
	 */
	public String getControlURL() {
	    return controlURL;
	}
	/**
	 * controlURLを設定します。
	 * @param controlURL controlURL
	 */
	public void setControlURL(String controlURL) {
	    this.controlURL = controlURL;
	}
	/**
	 * eventSubURLを取得します。
	 * @return eventSubURL
	 */
	public String getEventSubURL() {
	    return eventSubURL;
	}
	/**
	 * eventSubURLを設定します。
	 * @param eventSubURL eventSubURL
	 */
	public void setEventSubURL(String eventSubURL) {
	    this.eventSubURL = eventSubURL;
	}
	
	@Override
	public String toString() {
		return "serviceType: " + this.serviceType + "\n" +
			"serviceId: " + this.serviceId         + "\n" +
			"SCPDURL: " + this.SCPDURL             + "\n" +
			"controlURL: " + this.controlURL       + "\n" +
			"eventSubURL: " + this.eventSubURL;
	}
}
