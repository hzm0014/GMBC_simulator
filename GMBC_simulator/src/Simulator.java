import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class Simulator {
	// グラフに関する定数
	/**
	 * グラフのノード数
	 */
	//private final static int NODE_NUM = 1000;

	/**
	 * ノードが接続する半径距離（閾値）
	 */
	private final static float RADIUS = 10.0f;

	/**
	 * グラフのx軸方向の範囲
	 */
	private final static float X_RANGE = 150;

	/**
	 * グラフのy軸方向の範囲
	 */
	private final static float Y_RANGE = 150;

	// プロトコルに関する定数
	/**
	 * プロトコル名もしくは，以下のプロトコルのID
	 * Flooding: 0
	 * FFG: 1
	 * GMBC: 2(未実装)
	 */
	private final static String PROTOCOL_ID = "GMBC";

	/**
	 * プロトコルでのFanout数
	 */
	private final static int FANOUT = 2;

	/**
	 * MBCの更新頻度
	 */
	private final static float UPDATE_RATE = 1.0f;

	// Time-Varying Graphに関する定数
	/**
	 * 切断，再接続される確率の開始
	 */
	private final static float VARYING_RATE_START = 0.5f;
	/**
	 * 切断，再接続される確率の開始
	 */
	private final static float VARYING_RATE_FINISH = 0.5f;
	/**
	 * 切断，再接続される確率の開始
	 */
	private final static float VARYING_RATE_DELTA = 0.1f;

	// シミュレーションに関する定数
	/**
	 * 試行回数
	 * 何度グラフを初期化して試行するか
	 * TRIALS * GRAPH_TRIALS が試行回数になる
	 */
	private final static int TRIALS = 50;

	/**
	 * グラフに対する試行回数
	 * 1つのグラフに対して何度試行するか
	 * TRIALS * GRAPH_TRIALS が試行回数になる
	 */
	private final static int GRAPH_TRIALS = 10;

	/**
	 * ビュアーの表示の有無
	 */
	private final static boolean isView = false;

	// シミュレータに関する変数
	/**
	 * グラフのID
	 */
	private static int graphId;

	private static int trialNum = 0;

	/**
	 * Random Geometric Graph のジェネレータのクラス
	 */
	private static RandomGeometricGraphGenerator generator;

	/**
	 * メッセージ配送のプロトコルのクラス
	 */
	private static Protocol protocol;

	/**
	 * TimeVaryingGraphのクラス
	 */
	private static TimeVaryingGraph tvg;

	// 出力関係
	/**
	 * 外部ファイルへの出力の有無
	 */
	private static boolean isWrite = true;

	/**
	 * 出力先のパス
	 */
	private final static String PATH = "result/";

	/**
	 * 外部ファイルに出力するWriterクラス
	 */
	private static ResultWriter writer;

	public static void main(String args[]) throws InterruptedException, IOException {
		// シミュレーションの設定
		// グラフジェネレータの設定
		graphId = 0;
		generator = new RandomGeometricGraphGenerator(RADIUS, X_RANGE, Y_RANGE);
		// プロトコルの設定
		protocol = getProtocol(PROTOCOL_ID, FANOUT, UPDATE_RATE);
		// Time-Varying Graphの設定
		tvg = new TimeVaryingGraph();
		// writer
		if(isWrite) writer = new ResultWriter(PATH + getProtocolName() + ".csv");

		// プロトコルの説明などを表示
		printExplain();

		// 変化率を変化させてシミュレーションを実行
		for(float varying = VARYING_RATE_START; varying <= VARYING_RATE_FINISH; varying += VARYING_RATE_DELTA) {
			simulate(varying);
		}

		if(isWrite) writer.close();
	}

	/**
	 * シミュレーションを実行
	 * @param varying
	 * @throws InterruptedException
	 */
	private static void simulate(float varyingRate) throws InterruptedException {
		// 変化率の設定
		tvg.setVaryingRate(varyingRate);

		Viewer viewer;
		// グラフを初期化する試行のループ
		for(int i = 0; i < TRIALS; i++) {
			// グラフを更新
			Graph graph = generator.generate(graphId++ + "");
			protocol.setGraph(graph);
			tvg.setGraph(graph);

			// 同じグラフでの試行のループ
			for(int j = 0; j < GRAPH_TRIALS; j++) {
				trialNum++;

				// 各種初期化
				tvg.init();
				protocol.init();

				// display開始
				if (isView) viewer = graph.display(false);

				// シミュレーション実行
				do {
					if (isView) TimeUnit.SECONDS.sleep(1);
					tvg.run();
				} while(!protocol.run());
				if (isView) TimeUnit.SECONDS.sleep(1);

				// display終了
				if (isView) viewer.close();

				// 結果の出力
				printResult(trialNum, graph.getNodeCount(), varyingRate, protocol.getReachability(),
						protocol.getMsgNum(), protocol.getHopNum());
			}
		}
	}

	/**
	 * プロトコルIDからプロトコルの実態を返す
	 * @param id プロトコルID
	 * @param fanout fanout数
	 * @return プロトコル
	 */
	private static Protocol getProtocol(String id, int fanout, float updateRate) {
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

	/**
	 * プロトコルの説明などを表示
	 * @param protocolName プロトコル名
	 */
	private static void printExplain() {
		String query = "id,nodeNum,varyingRate,reachability,msgNum,hopNum";
		if(isWrite) {
			writer.println(getProtocolName());
			writer.println(query);
		}
		System.out.println(getProtocolName());
		System.out.println(query);
	}

	/**
	 * プロトコル名を取得
	 * @return プロトコル名
	 */
	private static String getProtocolName() {
		String name = PROTOCOL_ID;
		if (!name.equals("Flooding")) {
			name += "_" + FANOUT;
		}
		return name;
	}

	/**
	 * 結果の表示
	 * @param trial 試行回数（何度目か）
	 * @param node ノード数
	 * @param varying グラフのエッジの変化率
	 * @param reach メッセージが到達した割合
	 * @param msg メッセージ数
	 * @param hop ホップ数
	 */
	private static void printResult(int trial, int node, float varying, float reach, int msg, int hop) {
		String str = trial + "," + node + "," + varying + "," + reach + "," + msg + "," + hop;
		if(isWrite) {
			writer.println(str);
		}
		System.out.println(str);
	}
}