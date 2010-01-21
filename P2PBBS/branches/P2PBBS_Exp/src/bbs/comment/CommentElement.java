package bbs.comment;

import java.io.Serializable;

public class CommentElement implements Serializable, Comparable<CommentElement>{
	
	private static final long serialVersionUID = 7594536019751085715L;
	private int no; //投稿番号
	private String author = ""; //投稿者
	private String mailAddress = ""; //メールアドレス
	private String id = ""; //投稿者のID
	private String date = ""; //投稿日時
	private String message = ""; //本文
	private String title = null; //トピックタイトル 一番はじめのみ
	private String password = null; //トピックの削除・編集パスワード 一番はじめのみ

	public CommentElement() {
		
	}
	
	public CommentElement(int no, String author, String mailAddress, String id, String date, String message) {
		this.no = no;
		this.author = author;
		this.mailAddress = mailAddress;
		this.id = id;
		this.date = date;
		this.message = message;
		this.title = null;
		this.password = null;
	}
	
	/**
	 * noを取得します。
	 * @return no
	 */
	public int getNo() {
	    return no;
	}
	/**
	 * noを設定します。
	 * @param no
	 */
	public void setNo(int no) {
	    this.no = no;
	}
	/**
	 * authorを取得します。
	 * @return author
	 */
	public String getAuthor() {
	    return author;
	}
	/**
	 * authorを設定します。
	 * @param author author
	 */
	public void setAuthor(String author) {
	    this.author = author;
	}
	/**
	 * mailAddressを取得します。
	 * @return mailAddress
	 */
	public String getMailAddress() {
	    return mailAddress;
	}
	/**
	 * mailAddressを設定します。
	 * @param mailAddress mailAddress
	 */
	public void setMailAddress(String mailAddress) {
	    this.mailAddress = mailAddress;
	}
	/**
	 * idを取得します。
	 * @return id
	 */
	public String getId() {
	    return id;
	}
	/**
	 * idを設定します。
	 * @param id id
	 */
	public void setId(String id) {
	    this.id = id;
	}
	/**
	 * dateを取得します。
	 * @return date
	 */
	public String getDate() {
	    return date;
	}
	/**
	 * dateを設定します。
	 * @param date date
	 */
	public void setDate(String date) {
	    this.date = date;
	}
	/**
	 * messageを取得します。
	 * @return message
	 */
	public String getMessage() {
	    return message;
	}
	/**
	 * messageを設定します。
	 * @param message message
	 */
	public void setMessage(String message) {
	    this.message = message;
	}
	/**
	 * トピックタイトルを取得します．
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * トピックタイトルを設定します．
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 
	 * @param o
	 */
	public int compareTo(CommentElement o) {
		if( this.getNo() < o.getNo() ) {
			return -1;
		}else if( this.getNo() == o.getNo() ) {
			return 0;
		}
		return 1;
	}
	
	/**
	 * passwordを取得します。
	 * @return password
	 */
	public String getPassword() {
	    return password;
	}

	/**
	 * passwordを設定します。
	 * @param password password
	 */
	public void setPassword(String password) {
	    this.password = password;
	}
	
	@Override
	public String toString() {
		return "CommentElement [author=" + author + ", date=" + date + ", id="
				+ id + ", mailAddress=" + mailAddress + ", message=" + message
				+ ", no=" + no + ", password=" + password + ", title=" + title
				+ "]";
	}
	
}
