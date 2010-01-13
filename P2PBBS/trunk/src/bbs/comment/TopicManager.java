package bbs.comment;

import java.util.Set;
import java.util.SortedSet;

import bbs.util.Digest;
import bbs.util.HTMLEncode;
import bbs.util.MultiSortedMap;
import bbs.util.Pair;

/**
 * 
 * @author nishio
 *
 */
public class TopicManager {
	private MultiSortedMap< Pair<String, String> , CommentElement> topicData; //key = <トピック固有値, カテゴリID>， value = コメント
	public static final int maxNumberOfComment = 500; //最大コメント数
	public static final int maxLengthOfComment = 300; //1コメントあたりの最大文字数
	public static final int maxLengthOfAuthor = 30; //作者最大文字数
	public static final int maxLengthOfMail = 70; //メール最大文字数
	
	/**
	 * コメントエレメントをXML文章に変換
	 * @param comment
	 * @return
	 */
	public static String commentElementToXML(CommentElement comment) {
		StringBuffer buf = new StringBuffer("");
		
		buf.append("<comment>");
		buf.append("<no>"     + comment.getNo() + "</no>");
		buf.append("<author>" + HTMLEncode.encode( comment.getAuthor() ) + "</author>");
		buf.append("<mail>"   + HTMLEncode.encode( comment.getMailAddress() ) + "</mail>");
		buf.append("<id>"     + HTMLEncode.encode( comment.getId() ) + "</id>");
		buf.append("<date>"   + HTMLEncode.encode( comment.getDate() ) + "</date>");
		buf.append("<message>"+ HTMLEncode.encode( comment.getMessage() ) + "</message>");
		if( comment.getTitle() != null ) {
			buf.append("<title>" + HTMLEncode.encode( comment.getTitle() ) + "</title>");
		}
		if( comment.getPassword() != null ) {
			buf.append("<password>" + HTMLEncode.encode( comment.getPassword() ) + "</password>");
		}
		buf.append("</comment>");
		
		return buf.toString();
	}
	
	//TODO: 保持可能トピック数の上限を決める
	/**
	 * 
	 */
	public TopicManager() {
		topicData = new MultiSortedMap<Pair<String,String>, CommentElement>();
	}
	
	/**
	 * トピックへのコメント追加
	 * @param topic
	 * @param category
	 * @param comment
	 * @return 
	 * @throws MaxNumberOfCommentException コメント数が最大値に達した
	 * @throws MaxLengthOfCommentException コメントの最大文字数の上限に達した
	 * @throws MaxLengthOfAuthorException 作者の最大文字数の上限に達した
	 * @throws MaxLengthOfMailException メールアドレスの最大文字数に達した
	 */
	public void add(String topic, String category, CommentElement comment) throws 
								MaxNumberOfCommentException, MaxLengthOfCommentException,
								MaxLengthOfAuthorException, MaxLengthOfMailException {

		Set<CommentElement> comments = topicData.get(new Pair<String, String>(topic, category) );
		if( comments != null && comments.size() >= maxNumberOfComment ) {
			throw new MaxNumberOfCommentException( topic + "への書き込みに失敗しました.書き込み数が最大値"
					+ maxNumberOfComment + "に達しました.");
		}
		
		if( comment.getMessage() != null && comment.getMessage().length() >= maxLengthOfComment ) {
			throw new MaxLengthOfCommentException( topic + "への書き込みに失敗しました．" +
					"1コメントあたりの最大文字数が" + maxLengthOfComment + "以上です．");
		}
		
		if( comment.getAuthor() != null && comment.getAuthor().length() >= maxLengthOfAuthor ) {
			throw new MaxLengthOfAuthorException( topic + "への書き込みに失敗しました．" +
					"作者の最大文字数が" + maxLengthOfAuthor + "以上です．");
		}
		
		if( comment.getMailAddress() != null && comment.getMailAddress().length() >= maxLengthOfMail ) {
			throw new MaxLengthOfMailException( topic + "への書き込みに失敗しました．" +
					"メールアドレスの文字数が" + maxLengthOfMail + "以上です．");
		}
		//値を保持していれば削除
		if( topicData.contains(new Pair<String, String>(topic, category), comment)) {
			topicData.removeValue( new Pair<String, String>(topic, category), comment );
		}
		//トピック追加
		topicData.add( new Pair<String, String>(topic, category), comment );
	}
	
	/**
	 * トピックへのコメント投稿
	 * @param topic
	 * @param category
	 * @param author
	 * @param mailAddress
	 * @param id
	 * @param message
	 * @param password
	 * @throws MaxNumberOfCommentException
	 * @throws MaxLengthOfCommentException
	 * @throws MaxLengthOfAuthorException
	 * @throws MaxLengthOfMailException
	 * @throws TopicNotFoundException 
	 */
	public void add(String topic, String category, String author, String mailAddress,
			String id, String message, String password)
	 throws MaxNumberOfCommentException, MaxLengthOfCommentException,
	 		MaxLengthOfAuthorException, MaxLengthOfMailException, TopicNotFoundException {
		//noを書き換える, 投稿日時を書き換える
		SortedSet<CommentElement> elems = topicData.get(new Pair<String, String>(topic, category) );
		int no = 0;
		if( elems == null ) {
			throw new TopicNotFoundException("指定したトピックが見つかりませんでした．");
		}else {
			//一番最後の番号 + 1
			no = elems.last().getNo() + 1;
		}
		//現在の時間を投稿日時とする
		String date = bbs.util.CurrentTime.getCurrentTime();
		//コメント
		CommentElement comment = new CommentElement(no, author, mailAddress, id, date, message);
		if( password != null ) {
			comment.setPassword(password);
		}
		//コメント挿入
		this.add(topic, category, comment);
	}
	
	/**
	 * 指定したトピックを保持しているかどうか
	 * @param topic
	 * @param category
	 * @return
	 */
	public boolean contains(String topic, String category) {
		Set<Pair<String, String>> topics = this.getTopics();
		if( topics == null ) {
			return false;
		}
		return topics.contains( new Pair<String, String>(topic, category) );
	}
	
	/**
	 * トピックの作成
	 * @param topic
	 * @param category
	 * @param author
	 * @param mailAddress
	 * @param id
	 * @param message
	 * @param password
	 * @param title トピックタイトル
	 * @throws MaxNumberOfCommentException
	 * @throws MaxLengthOfCommentException
	 * @throws MaxLengthOfAuthorException
	 * @throws MaxLengthOfMailException
	 * @throws CannotCreateTopicException 
	 */
	public void createTopic(String topic, String category, String author,
			String mailAddress, String id, String message, String title, String password)
	 throws MaxNumberOfCommentException, MaxLengthOfCommentException,
	 		MaxLengthOfAuthorException, MaxLengthOfMailException, CannotCreateTopicException {
		//noを書き換える, 投稿日時を書き換える
		SortedSet<CommentElement> elems = topicData.get(new Pair<String, String>(topic, category) );
		int no = 0;
		if( elems == null ) {
			//最初のコメント
			no = 1;
		}else {
			throw new CannotCreateTopicException("既にトピックが存在しています．");
		}
		//現在の時間を投稿日時とする
		String date = bbs.util.CurrentTime.getCurrentTime();
		//コメント
		CommentElement comment = new CommentElement(no, author, mailAddress, id, date, message);
		//トピックのタイトル
		comment.setTitle(title);
		//パスワード
		comment.setPassword(password);
		//コメント挿入
		this.add(topic, category, comment);
	}
	
	/**
	 * 指定したコメントを削除コメントで埋める
	 * @param topic
	 * @param category
	 * @param no
	 * @return
	 */
	public boolean fill(String topic, String category, int no) {
		SortedSet<CommentElement> elem = get(topic, category, no, no+1);
		if( elem == null || elem.size() <= 0 ) {
			return false;
		}
		for(CommentElement comment : elem ) {
			comment.setAuthor("Delete");
			comment.setMailAddress("Delete");
			comment.setMessage("Delete");
			comment.setPassword("");
		}
		return true;
	}

	/**
	 * 指定したコメントを削除コメントで埋める
	 * 
	 * @param topic
	 * @param category
	 * @param no
	 * @param password
	 * @return
	 * @throws BadPasswordException
	 */
	public boolean fill(String topic, String category, int no, String password)
			throws BadPasswordException {
		SortedSet<CommentElement> elem = get(topic, category, no, no + 1);
		if( elem == null || elem.size() <= 0 ) {
			return false;
		}
		boolean accept = false;
		for(CommentElement comment : elem ) {
			//パスワードが一致した場合
			try {
				if (comment.getPassword() == null
						|| comment.getPassword().equals(Digest.getStringDigest(password))) {
					accept = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		boolean retValue = accept;
		if( accept ) {
			retValue = this.fill(topic, category, no);
		}else {
			throw new BadPasswordException("パスワードが正しくありません.");
		}
		return retValue;
	}
	
	/**
	 * 指定したトピックのコメントを取得
	 * @param topic
	 * @param category
	 * @return
	 */
	public SortedSet<CommentElement> get(String topic, String category) {
		return topicData.get( new Pair<String, String>(topic, category) );
	}
	
	/**
	 * 指定したトピックの指定した投稿No以上のコメントを取得
	 * @param topic
	 * @param category
	 * @param fromNo
	 * @return
	 */
	public SortedSet<CommentElement> get(String topic, String category, int fromNo) {
		return get(topic, category, fromNo, maxNumberOfComment);
	}
	
	/**
	 * 指定したトピックの指定した投稿Noのコメントを取得
	 * @param topic
	 * @param category
	 * @param fromNo
	 * @param toNo
	 * @return
	 */
	public SortedSet<CommentElement> get(String topic, String category, int fromNo, int toNo) {
		CommentElement compFrom = new CommentElement(fromNo, null,null,null,null,null);
		CommentElement compTo   = new CommentElement(toNo, null,null,null,null,null);
		SortedSet<CommentElement> elems = topicData.get(new Pair<String, String>(topic, category) );
		
		if( elems == null ) {
			return null;
		}
		
		SortedSet<CommentElement> ret = null;
		try{
			ret = elems.subSet( compFrom, compTo );
		}catch(NullPointerException e) {
			throw e;
		}catch(IllegalArgumentException e) {
			throw e;
		}
		
		return ret;
	}
	
	/**
	 * トピックのパスワードを取得
	 * @param topic
	 * @param category
	 * @return
	 */
	private String getPassword(String topic, String category) {
		SortedSet<CommentElement> comments = get(topic, category);
		String password = "";
		if (comments != null) {
			for (CommentElement comment : comments) {
				if (comment.getPassword() != null) {
					password = comment.getPassword();
					break;
				}
			}
		}
		return password;
	}
	
	/**
	 * トピック一覧を取得 Pair = TopicID, categoryID
	 * @return
	 */
	public Set<Pair<String, String>> getTopics() {
		return topicData.getKeys();
	}
	
	/**
	 * 指定したトピックをすべて削除
	 * @param topic
	 * @param category
	 * @return
	 */
	public boolean remove(String topic, String category) {
		if( topicData.remove(new Pair<String, String>(topic, category) ) == null ) {
			return false;
		}
		return true;
	}
	
	/**
	 * 指定したトピックの指定した番号のコメントを削除
	 * @param topic
	 * @param category
	 * @param no
	 * @return
	 */
	public boolean remove(String topic, String category, int no) {
		SortedSet<CommentElement> elem = topicData.get(new Pair<String, String>(topic, category) );
		if( elem == null ) {
			return false;
		}
		
		return elem.remove( new CommentElement(no, null,null,null,null,null) );
	}

	/**
	 * 指定したトピックの指定した番号のコメントを削除
	 * 
	 * @param topic
	 * @param category
	 * @param password
	 * @return
	 * @throws BadPasswordException
	 */
	public boolean remove(String topic, String category, int no, String password)
			throws BadPasswordException {
		String topicPassword = getPassword(topic, category);
		if( topicPassword.length() > 0 ) {
			String pass = null;
			try {
				pass = Digest.getStringDigest(password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if( !topicPassword.equals(pass) ) {
				throw new BadPasswordException("パスワードが正しくありません.");
			}
		}
		return remove(topic, category, no);
	}
	
	/**
	 * 指定したトピックをすべて削除
	 * @param topic
	 * @param category
	 * @param password
	 * @return
	 * @throws BadPasswordException 
	 */
	public boolean remove(String topic, String category, String password)
			throws BadPasswordException {
		String topicPassword = getPassword(topic, category);
		if( topicPassword.length() > 0 ) {
			String pass = null;
			try {
				pass = Digest.getStringDigest(password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if( !topicPassword.equals(pass) ) {
				throw new BadPasswordException("パスワードが正しくありません.");
			}
		}
		return remove(topic, category);
	}
	
	/**
	 * どのくらいのコメント数を保持しているか
	 * @param topic
	 * @param category
	 * @return
	 */
	public int size(String topic, String category) {
		Set<CommentElement> comments = topicData.get(new Pair<String, String>(topic, category) );
		if( comments == null ) {
			return 0;
		}
		return comments.size();
	}
	
	/**
	 * 保持しているコメントのデータ量を計算
	 * @param topic
	 * @param category
	 * @return
	 */
	public int calcMessageDataSize(String topic, String category) {
		SortedSet<CommentElement> comments = this.get(topic, category);
		int size = 0;
		if( comments != null ) {
			for( CommentElement comment : comments) {
				String message = comment.getMessage();
				if( message != null ) {
					size += message.length();
				}
			}
		}
		return size;
	}

}
