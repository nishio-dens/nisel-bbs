package bbs.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bbs.BBSConfiguration;

/**
 * HTMLエンコードを行います
 *
 */
public class HTMLEncode {
	//HTMLエンコードが必要な文字
	private static char[] htmlEncChar = {'&', '"', '<', '>'};
	//HTMLエンコードした文字列
	private static String[] htmlEncStr = {"&amp;", "&quot;", "&lt;", "&gt;"};

	/**
	 * HTMLエンコード処理
	 * @param strIn
	 * @return
	 */
	public static String encode (String strIn) {
		if (strIn == null) {
			return(null);
		}

		// HTMLエンコード処理
		StringBuffer strOut = new StringBuffer(strIn);
		// エンコードが必要な文字を順番に処理
		for (int i = 0; i < htmlEncChar.length; i++) {
			// エンコードが必要な文字の検索
			int idx = strOut.toString().indexOf(htmlEncChar[i]);

			while (idx != -1) {
				// エンコードが必要な文字の置換
				strOut.setCharAt(idx, htmlEncStr[i].charAt(0));
				strOut.insert(idx + 1, htmlEncStr[i].substring(1));

				// 次のエンコードが必要な文字の検索
				idx = idx + htmlEncStr[i].length();
				idx = strOut.toString().indexOf(htmlEncChar[i], idx);
			}
		}
		return(strOut.toString());
	}

	/**
	 * 改行文字 \n を \n<br> へと置き換える
	 * @param data
	 * @return
	 */
	public static String replaceNewline(String data) {
		if( data == null ) {
			return null;
		}
		String buf = data.replaceAll("\r", "");
		buf = buf.replaceAll("\n", "<br>\n");
		return buf;
	}

	/**
	 * スペース タブを空白文字 &nbsp; に置き換える
	 * @param data
	 * @return
	 */
	public static String replaceSpace(String data) {
		if( data == null ) {
			return null;
		}
		String buf = data.replaceAll(" ", "&nbsp;&nbsp;");
		buf = data.replaceAll("\t","&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
		return buf;
	}
	
	/**
	 * URLを<a href>のリンクへと変換する
	 * @param data
	 * @return
	 */
	public static String replaceURL(String data) {
		// URL
		Pattern pattern = Pattern
				.compile("(http://|https://){1}[\\w\\.\\-/:]+");
		Matcher matcher = pattern.matcher(data);
		StringBuffer sb = new StringBuffer();
		int lastnum = 0;
		while (matcher.find()) {
			matcher.appendReplacement(sb, "<a href=\"" + matcher.group()
					+ "\">" + matcher.group() + "</a>");
			lastnum = matcher.end();
		}
		return sb.toString() + data.substring(lastnum);
	}
}

