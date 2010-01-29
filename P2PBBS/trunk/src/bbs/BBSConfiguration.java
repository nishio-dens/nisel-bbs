package bbs;

public class BBSConfiguration {
	//デフォルト文字コード
	public static final String DEFAULT_ENCODING = "UTF-8";
	//読み込んだトピックをローカルに保存するか
	public static final boolean SAVE_TOPIC_TO_LOCAL = true;
	//トピックログ保存ディレクトリ名
	public static final String LOG_DIRECTORY = "log";
	//ゲートウェイアドレス
	public static String GATEWAY_ADDRESS = "localhost";
	//通信ログ保存ディレクトリ名
	public static final String TRAFFIC_LOG_DIRECTORY = "trafficlog";
	//通信ログ最大数
	public static final int NUM_OF_MAX_TRAFFIC_LOG = 100000;
	//通信ログをファイルへ出力するか
	public static final boolean SAVE_TRAFFIC_TO_LOCAL = true;
	
	/**
	 * GATEWAY_ADDRESSを取得します。
	 * @return GATEWAY_ADDRESS
	 */
	public String getGATEWAY_ADDRESS() {
	    return GATEWAY_ADDRESS;
	}
	
	/**
	 * GATEWAY_ADDRESSを設定します。
	 * @param GATEWAY_ADDRESS GATEWAY_ADDRESS
	 */
	public static void setGATEWAY_ADDRESS(String GATEWAY_ADDRESS) {
	    BBSConfiguration.GATEWAY_ADDRESS = GATEWAY_ADDRESS;
	}
	
}
