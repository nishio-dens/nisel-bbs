package bbs.comment;

/**
 * トピックへのコメント書き込み数がいっぱいになった
 * @author nishio
 *
 */
public class MaxNumberOfCommentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7083107738821091539L;

	public MaxNumberOfCommentException() {
		super();
	}
	
	public MaxNumberOfCommentException(String message) {
		super(message);
	}
}
