package bbs.server.html;

/**
 * 利用者に優しい応答をする際に利用
 * @author nishio
 *
 */
public class UsermodeHTML {
	private static final String ENCODING = bbs.BBSConfiguration.DEFAULT_ENCODING;

	/**
	 * トピック作成に成功したかどうかの返信
	 * @param success
	 * @return
	 */
	public static String manageRequestResponse(boolean success) {
		if (success) {
			return header() + "トピックの作成に成功しました．<br>"
					+ "戻るボタンで前の画面に戻り，更新ボタンを押してください．"
					+ "<br>書き込み内容が反映されるまで最大30秒ほどかかります．<br><br>\n"
					+ "<a href=\"javascript:history.back();\">戻る</a>"
					+ footer();
		}
		return header() + "トピックの作成に失敗しました．<br>書き込んだ内容をもう一度よく確認してください．"
				+ "<br>戻るボタンで前の画面に戻ってください．<br><br>\n"
				+ "<a href=\"javascript:history.back();\">戻る</a>" + footer();
	}

	/**
	 * トピックまたはコメント削除に失敗した場合
	 * @param success
	 * @return
	 */
	public static String deleteRequestResponse(boolean success) {
		if (success) {
			return header() + "削除に成功しました．戻るボタンで前の画面に戻り，更新ボタンを押してください．"
					+ "<br>情報が反映されるまで，最大30秒ほどかかります．<br><br>\n"
					+ "<a href=\"javascript:history.back();\">戻る</a>"
					+ footer();
		}
		return header() + "削除に失敗しました．<br>パスワードまたは削除するコメント番号が間違っている可能性があります．"
				+ "<br>戻るボタンで前の画面に戻ってください．<br><br>\n"
				+ "<a href=\"javascript:history.back();\">戻る</a>" + footer();
	}

	/**
	 *
	 * @param success
	 * @return
	 */
	public static String writeRequestResponse(boolean success) {
		if (success) {
			return header() + "コメントの投稿に成功しました．戻るボタンで前の画面に戻り，更新ボタンを押してください．" +
					"<br>情報が反映されるまで，最大30秒ほどかかります．<br><br>\n"
					+ "<a href=\"javascript:history.back();\">戻る</a>"
					+ footer();
		}
		return header() + "コメントの投稿に失敗しました．<br>書き込んだ内容をもう一度よく確認してください．"
				+ "<br>戻るボタンで前の画面に戻ってください．<br><br>\n"
				+ "<a href=\"javascript:history.back();\">戻る</a>" + footer();
	}

	/**
	 * HTMLヘッダの作成
	 * @return
	 */
	private static String header() {
		return "<html><head>\n<meta <meta http-equiv=\"Content-Type\" content=\"text/html; charset="
				+ ENCODING + "\">" + "<title>P2PBBS</title>\n<body>\n";
	}

	private static String footer() {
		return "</body></html>";
	}

}
