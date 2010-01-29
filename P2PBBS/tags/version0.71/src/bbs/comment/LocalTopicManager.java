package bbs.comment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import bbs.BBSConfiguration;
import bbs.util.HTMLEncode;
import bbs.util.Pair;

/**
 * 
 * @author nishio
 *
 */
public class LocalTopicManager {
	private TopicManager localTopic; //ローカルトピック管理
	private Pair<String,String> currentTopic = null; //現在表示中のトピック, <topic, category>
	
	private static final String charsetName = BBSConfiguration.DEFAULT_ENCODING; //文字コード
	private static final String logDirectory = BBSConfiguration.LOG_DIRECTORY; //ログ保存先
	
	/**
	 * 
	 * @param localTopic
	 */
	public LocalTopicManager() {
		this.localTopic = new TopicManager();
	}
	
	/**
	 * コメントエレメントをXML文章に変換
	 * @param comment
	 * @return
	 */
	private String commentElementToXML(CommentElement comment) {
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
	
	/**
	 * コメント追加
	 * @param topic
	 * @param category
	 * @param comment
	 */
	public void addComment(String topic, String category, CommentElement comment) {
		try {
			this.localTopic.add(topic, category, comment);
		} catch (MaxNumberOfCommentException e) {
			e.printStackTrace();
		} catch (MaxLengthOfCommentException e) {
			e.printStackTrace();
		} catch (MaxLengthOfAuthorException e) {
			e.printStackTrace();
		} catch (MaxLengthOfMailException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 指定したトピックのコメントを取得
	 * @param topic
	 * @return
	 * @throws CommentNotFoundException 
	 */
	public String getComment(String topic, String category) throws CommentNotFoundException {
		//メモリ上に存在している場合は，コメントをメモリ上から取得
		SortedSet<CommentElement> comments = localTopic.get(topic, category);
		//StringBuffer commentBuf = new StringBuffer("<topic>\n");
		StringBuffer commentBuf = new StringBuffer("");
		
		if( comments == null ) {
			//コメントをファイルから持ってくる
			loadTopicFromFile(topic, category);
			comments = localTopic.get(topic, category);
		}
		
		//コメントを取得
		for(CommentElement buf : comments ) {
			commentBuf.append( commentElementToXML( buf ) + "\n" );
		}
		//commentBuf.append("</topic>");
		
		//現在表示中のトピックを更新
		currentTopic = new Pair<String,String>(topic, category);
		
		return commentBuf.toString();
	}
	
	/**
	 * 指定したトピックのコメントを取得
	 * @param topic
	 * @param category
	 * @param fromNo
	 * @param toNo
	 * @return
	 * @throws CommentNotFoundException
	 */
	public String getComment(String topic, String category, int fromNo, int toNo) throws CommentNotFoundException {
		//メモリ上に存在している場合は，コメントをメモリ上から取得
		SortedSet<CommentElement> comments = localTopic.get(topic, category, fromNo, toNo);
		//StringBuffer commentBuf = new StringBuffer("<topic>\n");
		StringBuffer commentBuf = new StringBuffer("");
		
		if( comments == null ) {
			//コメントをファイルから持ってくる
			loadTopicFromFile(topic, category);
			comments = localTopic.get(topic, category, fromNo, toNo);
		}
		//コメントを取得
		for(CommentElement buf : comments ) {
			commentBuf.append( commentElementToXML( buf ) + "\n" );
		}
		//現在表示中のトピックを更新
		currentTopic = new Pair<String,String>(topic, category);
		
		return commentBuf.toString();
	}
	
	/**
	 * 指定したトピックのコメントを取得
	 * @param topic
	 * @param category
	 * @param fromNo
	 * @return
	 * @throws CommentNotFoundException
	 */
	public String getComment(String topic, String category, int fromNo) throws CommentNotFoundException {
		//メモリ上に存在している場合は，コメントをメモリ上から取得
		SortedSet<CommentElement> comments = localTopic.get(topic, category, fromNo);
		//StringBuffer commentBuf = new StringBuffer("<topic>\n");
		StringBuffer commentBuf = new StringBuffer("");
		
		if( comments == null ) {
			//コメントをファイルから持ってくる
			loadTopicFromFile(topic, category);
			comments = localTopic.get(topic, category, fromNo);
		}
		//コメントを取得
		for(CommentElement buf : comments ) {
			commentBuf.append( commentElementToXML( buf ) + "\n" );
		}
		//現在表示中のトピックを更新
		currentTopic = new Pair<String,String>(topic, category);
		
		return commentBuf.toString();
	}
	
	/**
	 * categoryIDに所属するトピック一覧を取得します
	 * @param categoryID
	 * @return
	 */
	public String[] getTopics(String categoryID) {
		File dir = new File("./" + logDirectory + "/" + categoryID);
		return dir.list();
	}

	/**
	 * currentTopicを取得します。
	 * @return currentTopic
	 */
	public Pair<String,String> getCurrentTopic() {
	    return currentTopic;
	}
	
	/**
	 * ファイルからトピックを読み込む
	 * @param topic
	 * @param category
	 * @throws CommentNotFoundException 
	 */
	public void loadTopicFromFile(String topic, String category) throws CommentNotFoundException {
		try {
			File f = new File( "./" + logDirectory + "/" + category + "/" + topic + ".dat");
			byte[] b = new byte[(int) f.length()];
			FileInputStream fi = new FileInputStream(f);

			fi.read(b);
			//読み取ったデータ
			String data = new String(b, this.charsetName);
			fi.close();
			
			List<CommentElement> topics = CommentElementXMLParser.XMLToCommentElement(data);
			if( topics != null ) {
				for(CommentElement t : topics ) {
					//データをメモリ上に読み込む
					localTopic.add(topic, category, t);
					//System.out.println("debug :" + t.getMessage() + " - " + t.getDate());
				}
			}
		} catch (Exception e) {
			//データを読み込めなかった場合
			throw new CommentNotFoundException("トピック:" + topic + "は見つかりませんでした．");
		}
	}
	
	/**
	 * 現在表示中のトピックを除いてメモリ上から削除する
	 * また，削除したトピックはファイルへと保存する
	 */
	public void garbageCollection() {
		//メモリ上に保持されているトピックをファイルへ移動する
		saveTopicToFile();
		//トピック一覧を取得
		Set<Pair<String, String>> topics = localTopic.getTopics();
		if( topics == null ) {
			return;
		}
		//現在表示しているトピック以外を削除
		for(Pair<String, String> topic : topics ) {
			if( topic.equals( getCurrentTopic() ) ) {
				continue;
			}
			remove(topic.getFirst(), topic.getSecond() );
		}
	}
	
	/**
	 * 指定したトピックをメモリ上から削除する
	 * @param topic
	 * @param category
	 */
	public void remove(String topic, String category) {
		localTopic.remove(topic, category);
	}
	
	/**
	 * メモリ上に保持しているトピックをファイルへ保存する
	 * @param topic
	 * @param category
	 * @return
	 */
	public boolean saveTopicToFile() {
		Set<Pair<String,String>> topics = localTopic.getTopics();
		
		//データが一つも存在しない
		if( topics == null ) {
			return false;
		}
		//ログディレクトリを作成
		try{
			File logDir = new File("./" + logDirectory );
			if( !logDir.exists() ) {
				//ディレクトリが存在しないので作成する
				if( logDir.mkdir() == false ) {
					throw new IOException(logDirectory + "ディレクトリを作成できませんでした．");
				}
			}

			//メモリ上に存在するすべてのトピックをファイルへ保存する
			for(Pair<String, String> topic : topics) {
				//ファイル名 ./カテゴリ名/トピック名.dat
				String filename = "./" + logDirectory + "/" + topic.getSecond() + "/" + topic.getFirst() + ".dat";
				//トピック保存ディレクトリを作成
				//ディレクトリ名はトピックのカテゴリ名
				File dir = new File("./" + logDirectory + "/" + topic.getSecond() );
				if( !dir.exists() ) {
					//ディレクトリが存在しないので作成する
					if( dir.mkdir() == false ) {
						throw new IOException("ディレクトリを作成できませんでした．");
					}
				}

				FileOutputStream fos = new FileOutputStream( filename );
				OutputStreamWriter osw = new OutputStreamWriter(fos, charsetName);
				BufferedWriter bw = new BufferedWriter(osw);

				//ファイル書き込みデータ
				StringBuffer writeData = new StringBuffer("<?xml version=\"1.0\" encoding=\"" + charsetName + "\"?>\n");
				writeData.append("<topic>\n");

				SortedSet<CommentElement> comments = localTopic.get( topic.getFirst(), topic.getSecond() );
				//トピックの所属するすべてのコメントデータを書き込む
				for( CommentElement comment : comments ) {
					writeData.append( commentElementToXML(comment) + "\n" );
				}
				writeData.append("</topic>");
				//ファイル書き込み
				bw.write( writeData.toString() );
				//ファイルを閉じる
				bw.close();
				osw.close();
				fos.close();
			}
		}catch(Exception e) {
			System.err.println( e );
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * どのくらいのコメント数を保持しているか
	 * @param topic
	 * @param category
	 * @return
	 */
	public int size(String topic, String category) {
		//メモリ上に存在している場合は，コメントをメモリ上から取得
		SortedSet<CommentElement> comments = localTopic.get(topic, category);
		//StringBuffer commentBuf = new StringBuffer("<topic>\n");
		
		if( comments == null ) {
			//コメントをファイルから持ってくる
			try {
				loadTopicFromFile(topic, category);
				comments = localTopic.get(topic, category);
			} catch (CommentNotFoundException e) {
				//コメントが見つからなかった，無視
			}
		}	
		return localTopic.size(topic, category);
	}
	
	/**
	 * データ量を取得
	 * @param topic
	 * @param category
	 * @return
	 */
	public int calcMessageDataSize(String topic, String category) {
		//メモリ上に存在している場合は，コメントをメモリ上から取得
		SortedSet<CommentElement> comments = localTopic.get(topic, category);
		//StringBuffer commentBuf = new StringBuffer("<topic>\n");
		
		if( comments == null ) {
			//コメントをファイルから持ってくる
			try {
				loadTopicFromFile(topic, category);
				comments = localTopic.get(topic, category);
			} catch (CommentNotFoundException e) {
				//コメントが見つからなかった，無視
			}
		}	
		return localTopic.calcMessageDataSize(topic, category);
	}

}
