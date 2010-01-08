package polaris.upnp.device;

/**
 * ルータ情報取得URIが間違っている
 * @author nishio
 *
 */
public class BadDeviceURIException extends Exception {

	public BadDeviceURIException() {
		super();
	}
	
	public BadDeviceURIException(String message) {
		super( message );
	}
}
