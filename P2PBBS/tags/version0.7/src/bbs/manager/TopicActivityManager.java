package bbs.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bbs.util.*;

/**
 * トピックの活性度を管理
 * @author nishio
 * TODO: 古くなったトピックが残ったままにならないか
 */
public class TopicActivityManager {
	//トピックの活性度を管理
	private Map<Pair<String, String>, Integer> topicActivity = null;
	//一つの書き込みにつき増やす数値
	private int speed = 120;
	/**
	 * 
	 * @param topicManager
	 */
	public TopicActivityManager() {	
		this.topicActivity = new HashMap<Pair<String, String>, Integer>();
	}
	
	/**
	 * 更新する毎にトピックの活性度を一つ下げる
	 */
	public void update() {
		Set<Pair<String, String>> topics = this.topicActivity.keySet();
		//一回更新する毎にactivityを1下げる
		for(Pair<String, String> topic : topics ) {
			int activity = this.topicActivity.get(topic);
			activity--;
			if( activity < 0 ) {
				activity = 0;
			}
			this.topicActivity.put(topic, activity);
		}
	}
	
	/**
	 * トピックの活性度を更新
	 * @param categoryID
	 * @param topicID
	 * @param viewCount 何回参照されたか
	 */
	public void updateFromViewcount(String categoryID, String topicID, int viewCount) {
		Pair<String, String> topic = new Pair<String, String>(topicID, categoryID);
		if( topicActivity.containsKey(topic) ) {
			int activity = topicActivity.get(topic);
			topicActivity.put(topic, activity + (viewCount * speed) );
		}else {
			topicActivity.put(topic, viewCount * speed);
		}
	}
	
	/**
	 * トピックの活性度を更新
	 * @param categoryID
	 * @param topicID
	 * @param activity 活性度
	 */
	public void updateFromActivity(String categoryID, String topicID, int activity) {
		Pair<String, String> topic = new Pair<String, String>(topicID, categoryID);
		if( topicActivity.containsKey(topic) ) {
			int oldActivity = topicActivity.get(topic);
			topicActivity.put(topic, oldActivity + activity);
		}else {
			topicActivity.put(topic, activity);
		}
	}
	
	/**
	 * 活性度をリセット
	 * @param categoryID
	 * @param topicID
	 */
	public void resetActivity(String categoryID, String topicID) {
		this.topicActivity.remove(new Pair<String, String>(topicID, categoryID) );
	}
	
	/**
	 * トピックの活性度を取得
	 * @param categoryID
	 * @param topicID
	 * @return
	 */
	public int getActivity(String categoryID, String topicID) {
		Pair<String, String> topic = new Pair<String, String>(topicID, categoryID);
		int activity = 0;
		if( topicActivity.containsKey(topic) ) {
			activity = topicActivity.get(topic);
		}
		return activity;
	}
	
	/**
	 * もっとも活性度の高い値を取得
	 * @return
	 */
	public int maxActivity() {
		Collection<Integer> activity = this.topicActivity.values();
		if( activity == null || activity.size() == 0 ) {
			return 0;
		}
		return Collections.max(activity);
	}

}
