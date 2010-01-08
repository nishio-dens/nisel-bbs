package polaris.upnp.discover;


/**
 * ルータ発見時のイベント
 * @author nishio
 *
 */
public class RooterDiscoveryEvent {
	private RooterDiscovery source;
	
	/**
	 * 
	 * @param discovery
	 */
	public RooterDiscoveryEvent(RooterDiscovery discovery) {
		this.source = discovery;
	}
	
	/**
	 * 
	 */
	public RooterDiscovery getSource() {
		return source;
	}
}
