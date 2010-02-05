package bbs.manager;

/**
 * トピックの情報
 * @author nishio
 *
 */
public class TopicInformationElement {

	//カテゴリ
	private String categoryID = null;
	//トピックのID
	private String tid = null;
	//トピックのタイトル
	private String title = null;
	//コメント数
	private int numOfComment = 0;
	//活性度
	private int activity = 0;
	//作者
	private String author = null;
	
	public TopicInformationElement() {
		
	}
	
	/**
	 * 
	 * @param categoryID
	 * @param tid
	 * @param title
	 * @param numOfComment
	 * @param activity
	 * @param author
	 */
	public TopicInformationElement(String categoryID, String tid, String title,
			int numOfComment, int activity, String author) {
		this.categoryID = categoryID;
		this.tid = tid;
		this.title = title;
		this.numOfComment = numOfComment;
		this.activity = activity;
		this.author = author;
	}
	
	
	/**
	 * activityを取得します。
	 * @return activity
	 */
	public int getActivity() {
	    return activity;
	}
	/**
	 * categoryIDを取得します。
	 * @return categoryID
	 */
	public String getCategoryID() {
	    return categoryID;
	}
	/**
	 * numOfCommentを取得します。
	 * @return numOfComment
	 */
	public int getNumOfComment() {
	    return numOfComment;
	}
	/**
	 * tidを取得します。
	 * @return tid
	 */
	public String getTid() {
	    return tid;
	}
	/**
	 * titleを取得します。
	 * @return title
	 */
	public String getTitle() {
	    return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryID == null) ? 0 : categoryID.hashCode());
		result = prime * result + ((tid == null) ? 0 : tid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TopicInformationElement other = (TopicInformationElement) obj;
		if (categoryID == null) {
			if (other.categoryID != null)
				return false;
		} else if (!categoryID.equals(other.categoryID))
			return false;
		if (tid == null) {
			if (other.tid != null)
				return false;
		} else if (!tid.equals(other.tid))
			return false;
		return true;
	}

	/**
	 * activityを設定します。
	 * @param activity activity
	 */
	public void setActivity(int activity) {
	    this.activity = activity;
	}

	/**
	 * categoryIDを設定します。
	 * @param categoryID categoryID
	 */
	public void setCategoryID(String categoryID) {
	    this.categoryID = categoryID;
	}

	/**
	 * numOfCommentを設定します。
	 * @param numOfComment numOfComment
	 */
	public void setNumOfComment(int numOfComment) {
	    this.numOfComment = numOfComment;
	}

	/**
	 * tidを設定します。
	 * @param tid tid
	 */
	public void setTid(String tid) {
	    this.tid = tid;
	}

	/**
	 * titleを設定します。
	 * @param title title
	 */
	public void setTitle(String title) {
	    this.title = title;
	}

	@Override
	public String toString() {
		return "TopicInformationElement [activity=" + activity
				+ ", categoryID=" + categoryID + ", numOfComment="
				+ numOfComment + ", tid=" + tid + ", title=" + title + "]";
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
}
