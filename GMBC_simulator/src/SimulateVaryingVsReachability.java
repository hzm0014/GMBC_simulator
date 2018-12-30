import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class SimulateVaryingVsReachability extends Simulate {
	// グラフに関する定数
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
	 * MBCの更新頻度
	 */
	private final static float UPDATE_RATE = 1.0f;

	// Time-Varying Graphに関する定数
	/**
	 * 切断，再接続される確率の開始
	 */
	private final static float VARYING_RATE_START = 0.0f;
	/**
	 * 切断，再接続される確率の終了
	 */
	private final static float VARYING_RATE_FINISH = 1.0f;
	/**
	 * 切断，再接続される確率の刻み
	 */
	private final static float VARYING_RATE_DELTA = 0.1f;

	/**
	 * 固定数による切断，再接続のパラメータ
	 */
	int varyingFixNum = 7;
//	int[] varyingFixNumList = {0, 511, 1030, 1587, 2152, 2693, 3157, 3570};
	int[] varyingFixNumList = {3600, 3700, 3800, 3900, 4000, 4100, 4200};

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

	public SimulateVaryingVsReachability(String protocolId, int fanout) {
		// プロトコルの設定
		protocol = getProtocol(protocolId, fanout, UPDATE_RATE);
	}

	public void run() throws IOException, InterruptedException {
		// シミュレーションの設定
		// グラフジェネレータの設定
		generator = new RandomGeometricGraphGenerator(RADIUS, X_RANGE, Y_RANGE);
		// Time-Varying Graphの設定
		tvg = new TimeVaryingGraph();
		// writer
		if(isWrite) writer = new ResultWriter(PATH + protocol.toString() + ".csv");

		// プロトコルの説明などを表示
		printExplain();

		// 変化率を変化させてシミュレーションを実行
		/**
		for(float varying = VARYING_RATE_START; varying <= VARYING_RATE_FINISH; varying += VARYING_RATE_DELTA) {
			simulate(varying);
		}
		**/

		for (int i = 0; i < varyingFixNum; i++) {
			simulate(varyingFixNumList[i]);
		}

		if(isWrite) writer.close();
	}

	/**
	 * シミュレーションを実行
	 * @param varying
	 * @throws InterruptedException
	 */
	private void simulate(int varyingFixNum) throws InterruptedException {
		// 変化率の設定
		tvg.setVaryingFixNum(varyingFixNum);

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
				printResult(trialNum, graph.getNodeCount(), varyingFixNum, protocol.getReachability(),
						protocol.getMsgNum(), protocol.getHopNum());
			}
		}
	}

	/**
	 * プロトコルの説明などを表示
	 * @param protocolName プロトコル名
	 */
	private void printExplain() {
		String query = "id,nodeNum,varyingFixNum,reachability,msgNum,hopNum";
		if(isWrite) {
			writer.println(protocol.toString());
			writer.println(query);
		}
		System.out.println(protocol.toString());
		System.out.println(query);
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
	private void printResult(int trial, int node, float varying, float reach, int msg, int hop) {
		String str = trial + "," + node + "," + varying + "," + reach + "," + msg + "," + hop;
		if(isWrite) {
			writer.println(str);
		}
		System.out.println(str);
	}
}