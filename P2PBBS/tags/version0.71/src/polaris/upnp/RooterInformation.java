package polaris.upnp;

public class RooterInformation {

	//ルータ情報が書き込まれたアドレス
	private String location = null;
	//サーチ対象
	private String searchTarget = null;
	//ユニークサービスネーム
	private String uniqueServiceName = null;
	
	public RooterInformation() {
		
	}
	
	/**
	 * 
	 * @param location
	 * @param searchTarget
	 * @param uniqueServiceName
	 */
	public RooterInformation(String location, String searchTarget, String uniqueServiceName) {
		this.location = location;
		this.searchTarget = searchTarget;
		this.uniqueServiceName = uniqueServiceName;
	}
	
	/**
	 * locationを取得します。
	 * @return location
	 */
	public String getLocation() {
	    return location;
	}
	/**
	 * locationを設定します。
	 * @param location location
	 */
	public void setLocation(String location) {
	    this.location = location;
	}
	/**
	 * searchTargetを取得します。
	 * @return seachTarget
	 */
	public String getSearchTarget() {
	    return searchTarget;
	}
	/**
	 * searchTargetを設定します。
	 * @param seachTarget seachTarget
	 */
	public void setSearchTarget(String seachTarget) {
	    this.searchTarget = seachTarget;
	}
	/**
	 * uniqueServiceNameを取得します。
	 * @return uniqueServiceName
	 */
	public String getUniqueServiceName() {
	    return uniqueServiceName;
	}
	/**
	 * uniqueServiceNameを設定します。
	 * @param uniqueServiceName uniqueServiceName
	 */
	public void setUniqueServiceName(String uniqueServiceName) {
	    this.uniqueServiceName = uniqueServiceName;
	}
}
