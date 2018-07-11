import java.io.IOException;

public abstract class Simulate {
	// シミュレータに関する変数
	/**
	 * ビュアーの表示の有無
	 */
	protected final static boolean isView = false;
	/**
	 * グラフのID
	 */
	protected int graphId = 0;
	/**
	 * 試行回数のカウント
	 */
	protected int trialNum = 0;
	/**
	 * Random Geometric Graph のジェネレータのクラス
	 */
	protected RandomGeometricGraphGenerator generator;
	/**
	 * メッセージ配送のプロトコルのクラス
	 */
	protected Protocol protocol;
	/**
	 * TimeVaryingGraphのクラス
	 */
	protected TimeVaryingGraph tvg;

	// 出力に関する定数と変数
	/**
	 * 外部ファイルへの出力の有無
	 */
	protected boolean isWrite = true;
	/**
	 * 出力先のパス
	 */
	protected final static String PATH = "result/";
	/**
	 * 外部ファイルに出力するWriterクラス
	 */
	protected ResultWriter writer;

	/**
	 * シミュレーション
	 * @throws InterruptedException
	 * @throws IOException
	 */
	abstract public void run() throws IOException, InterruptedException;

	/**
	 * プロトコルIDからプロトコルの実態を返す
	 * @param id プロトコルID
	 * @param fanout fanout数
	 * @return プロトコル
	 */
	protected static Protocol getProtocol(String id, int fanout, float updateRate) {
		Protocol protocol;
		if (id.equals("Flooding") || id.equals("0")) {
			protocol = new Flooding();
		} else if (id.equals("FFG") || id.equals("1")) {
			protocol = new FixedFanoutGossip(fanout);
		} else if (id.equals("GMBC") || id.equals("2")) {
			protocol = new GossipForMBC(fanout, updateRate);
		} else {
			System.out.println("未実装のプロトコル名");
			protocol = null;
		}
		return protocol;
	}
}
