package bbs.dht;

/**
 * DHTの初期化に失敗した場合
 * @author nishio
 *
 */
public class InitDHTException extends Exception {
	public InitDHTException() {
		super();
	}
	
	public InitDHTException(String message) {
		super(message);
	}
}
