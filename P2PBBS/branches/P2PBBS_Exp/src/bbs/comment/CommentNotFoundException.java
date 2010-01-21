package bbs.comment;

public class CommentNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CommentNotFoundException() {
		super();
	}
	
	public CommentNotFoundException(String message) {
		super(message);
	}
}
