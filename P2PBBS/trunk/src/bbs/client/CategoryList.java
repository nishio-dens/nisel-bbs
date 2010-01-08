package bbs.client;

import java.util.LinkedList;
import java.util.List;

import bbs.util.Pair;

/**
 * カテゴリ一覧
 * @author nishio
 *
 */
public class CategoryList {

	private static CategoryList myself = null;
	//カテゴリ Pair<カテゴリ名,SHA-1>
	private List<Pair<String,String>> category = null;

	//singleton
	private CategoryList() {

	}

	/**
	 * カテゴリリスト取得
	 * @return
	 */
	public static CategoryList getCategoryListClass() {
		if( myself == null ) {
			myself = new CategoryList();
			myself.createCategory();
		}
		return myself;
	}

	/**
	 * カテゴリ一覧を取得
	 * @return
	 */
	public List<Pair<String,String>> getCategoryList() {
		return category;
	}

	/**
	 * カテゴリ名一覧を取得
	 * @return
	 */
	public List<String> getCategoryNames() {
		List<String> categories = new LinkedList<String>();
		for( Pair<String,String> cat : category) {
			categories.add( cat.getFirst() );
		}
		return categories;
	}


	/**
	 * SHA1名一覧を取得
	 * @return
	 */
	public List<String> getSHA1Names() {
		List<String> categories = new LinkedList<String>();
		for( Pair<String,String> cat : category) {
			categories.add( cat.getSecond() );
		}
		return categories;
	}

	/**
	 * SHA1からカテゴリ名を取得する
	 * @param sha1
	 * @return
	 */
	public String getCategoryName(String sha1) {
		String ret = null;
		for( Pair<String, String> cat : category ) {
			if( cat.getSecond().equals(sha1) ) {
				ret = cat.getFirst();
			}
		}
		return ret;
	}

	/**
	 * カテゴリ名からSHA1を取得する
	 * @param categoryName
	 * @return
	 */
	public String getSHA1Name(String categoryName) {
		String ret = null;
		for( Pair<String, String> cat : category ) {
			if( cat.getFirst().equals(categoryName) ) {
				ret = cat.getSecond();
			}
		}
		return ret;
	}


	/**
	 * カテゴリ一覧を作る
	 */
	private void createCategory() {
		category = new LinkedList<Pair<String,String>>();
		category.add( new Pair<String,String>("BBS運用情報","ae2371fb6feb5d5643b42d809fcba82fc2f43d64"));
		category.add( new Pair<String,String>("テストカテゴリ","4a7a13dada54be941753f1c02ac2be1f03d749e8"));
		category.add( new Pair<String,String>("雑談","90b29d5fa1eb2664a6380433542a87e8f146287f"));
	}
}
