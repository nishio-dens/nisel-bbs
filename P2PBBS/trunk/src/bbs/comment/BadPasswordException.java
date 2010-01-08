package bbs.comment;

import java.io.IOException;

/**
 * パスワードが正しくない場合に発生する例外
 * @author nishio
 *
 */
public class BadPasswordException extends IOException {
	
	/**
	 * 
	 */
	public BadPasswordException() {
		super();
	}
	
	/**
	 * 
	 * @param message
	 */
	public BadPasswordException(String message) {
		super( message );
	}

}
