package polaris.upnp;

/**
 * ルータ情報取得中にエラーが起こった
 * @author nishio
 *
 */
public class RooterDeviceErrorException extends Exception {

	public RooterDeviceErrorException() {
		super();
	}
	
	public RooterDeviceErrorException(String message) {
		super( message );
	}
}
