package polaris.upnp.device;

/**
 * ルータ情報のroot element
 * @author nishio
 *
 */
public class RooterRootElement {
	private String specVersion = null;
	private String device = null;
	
	public RooterRootElement() {
		
	}
	/**
	 * 
	 * @param specVersion
	 * @param device
	 */
	public RooterRootElement(String specVersion, String device) {
		this.specVersion = specVersion;
		this.device = device;
	}

	/**
	 * specVersionを取得します。
	 * @return specVersion
	 */
	public String getSpecVersion() {
	    return specVersion;
	}

	/**
	 * specVersionを設定します。
	 * @param specVersion specVersion
	 */
	public void setSpecVersion(String specVersion) {
	    this.specVersion = specVersion;
	}

	/**
	 * deviceを取得します。
	 * @return device
	 */
	public String getDevice() {
	    return device;
	}

	/**
	 * deviceを設定します。
	 * @param device device
	 */
	public void setDevice(String device) {
	    this.device = device;
	}
}
