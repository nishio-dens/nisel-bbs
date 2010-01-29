package bbs.comment;

/**
 * トピックが作成できないとき
 * @author nishio
 *
 */
public class CannotCreateTopicException extends Exception {
	public CannotCreateTopicException() {
		super();
	}
	
	public CannotCreateTopicException(String message) {
		super( message );
	}
}
