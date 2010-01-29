package bbs.util;

import java.util.Calendar;

/**
 * 現在の時刻を取得
 * @author Nishio
 *
 */
public class CurrentTime {
	public static String getCurrentTime() {
		Calendar cal = Calendar.getInstance();

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DATE);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		return year + "年" + month + "月" + day + "日 " + hour + "時" +
		minute + "分" + second + "秒";
	}

}