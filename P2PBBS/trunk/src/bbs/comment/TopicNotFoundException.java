package bbs.comment;

/**
 * トピックが見つからなかった時
 * @author nishio
 *
 */
public class TopicNotFoundException extends Exception {
	public TopicNotFoundException() {
		super();
	}
	
	public TopicNotFoundException(String message) {
		super(message);
	}
}
