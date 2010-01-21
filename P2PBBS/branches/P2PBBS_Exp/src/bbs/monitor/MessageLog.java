package bbs.monitor;

import java.util.Date;

/**
 * 
 * @author nishio
 *
 */
public class MessageLog implements Comparable<MessageLog>{
	private String messageName; //メッセージ名
	private String address; //送信または受信相手
	private int size; //データ量
	private Date date; 
	private String type = ""; //メッセージタイプ
	
	public MessageLog() {
		
	}
	
	/**
	 * 
	 * @param address
	 * @param size
	 * @param date
	 */
	public MessageLog(String messageName, String address, int size, Date date) {
		this.messageName = messageName;
		this.address = address;
		this.size = size;
		this.date = date;
	}
	
	/**
	 * 
	 * @param address
	 * @param size
	 * @param date
	 * @param type
	 */
	public MessageLog(String messageName, String address, int size, Date date, String type) {
		this.messageName = messageName;
		this.address = address;
		this.size = size;
		this.date = date;
		this.type = type;
	}
	
	@Override
	public int compareTo(MessageLog o) {
		return date.compareTo(o.getDate());
	}
	/**
	 * addressを取得します。
	 * @return address
	 */
	public String getAddress() {
	    return address;
	}

	/**
	 * dateを取得します。
	 * @return date
	 */
	public Date getDate() {
	    return date;
	}

	/**
	 * messageNameを取得します。
	 * @return messageName
	 */
	public String getMessageName() {
	    return messageName;
	}
	/**
	 * sizeを取得します。
	 * @return size
	 */
	public int getSize() {
	    return size;
	}
	/**
	 * typeを取得します。
	 * @return type
	 */
	public String getType() {
	    return type;
	}

	/**
	 * addressを設定します。
	 * @param address address
	 */
	public void setAddress(String address) {
	    this.address = address;
	}

	/**
	 * dateを設定します。
	 * @param date date
	 */
	public void setDate(Date date) {
	    this.date = date;
	}
	
	/**
	 * messageNameを設定します。
	 * @param messageName messageName
	 */
	public void setMessageName(String messageName) {
	    this.messageName = messageName;
	}

	/**
	 * sizeを設定します。
	 * @param size size
	 */
	public void setSize(int size) {
	    this.size = size;
	}

	/**
	 * typeを設定します。
	 * @param type type
	 */
	public void setType(String type) {
	    this.type = type;
	}

	@Override
	public String toString() {
		return "MessageLog [address=" + address + ", date=" + date
				+ ", messageName=" + messageName + ", size=" + size + ", type="
				+ type + "]";
	}
	
}
