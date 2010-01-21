package polaris.upnp.device;

/**
 * ルータデバイス情報
 * @author nishio
 *
 */
public class RooterDeviceElement {

	private String deviceType = null;  //required
	private String friendlyName = null; //required
	private String manufacturer = null; //required
	private String modelName = null; //required
	
	public RooterDeviceElement() {
		
	}
	
	/**
	 * 
	 * @param deviceType
	 * @param friendlyName
	 * @param manufacturer
	 * @param modelName
	 */
	public RooterDeviceElement(String deviceType, String friendlyName, String manufacturer, String modelName) {
		this.deviceType = deviceType;
		this.friendlyName = friendlyName;
		this.manufacturer = manufacturer;
		this.modelName = modelName;
	}
	/**
	 * deviceTypeを取得します。
	 * @return deviceType
	 */
	public String getDeviceType() {
	    return deviceType;
	}
	/**
	 * deviceTypeを設定します。
	 * @param deviceType deviceType
	 */
	public void setDeviceType(String deviceType) {
	    this.deviceType = deviceType;
	}
	/**
	 * friendlyNameを取得します。
	 * @return friendlyName
	 */
	public String getFriendlyName() {
	    return friendlyName;
	}
	/**
	 * friendlyNameを設定します。
	 * @param friendlyName friendlyName
	 */
	public void setFriendlyName(String friendlyName) {
	    this.friendlyName = friendlyName;
	}
	/**
	 * manufacturerを取得します。
	 * @return manufacturer
	 */
	public String getManufacturer() {
	    return manufacturer;
	}
	/**
	 * manufacturerを設定します。
	 * @param manufacturer manufacturer
	 */
	public void setManufacturer(String manufacturer) {
	    this.manufacturer = manufacturer;
	}
	/**
	 * modelNameを取得します。
	 * @return modelName
	 */
	public String getModelName() {
	    return modelName;
	}
	/**
	 * modelNameを設定します。
	 * @param modelName modelName
	 */
	public void setModelName(String modelName) {
	    this.modelName = modelName;
	}
	
	@Override
	public String toString() {
		return "DeviceType: " + deviceType + " friendlyName: " + friendlyName +
			" manufacturer: " + manufacturer + " modelName: " + modelName;
	}
	
}
