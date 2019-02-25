import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.ui.view.Viewer;

public class SimulateChurnVsReachability extends Simulate {
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
	// Churn Graphに関する定数
	/**
	 * CuhrnGraph
	 */
	private ChurnGraph cg;
	/**
	 * 切断，再接続される確率の開始
	 */
	private final static float CHURN_RATE_START = 0.0f;
	/**
	 * 切断，再接続される確率の終了
	 */
	private final static float CHURN_RATE_FINISH = 1.05f;
	/**
	 * 切断，再接続される確率の刻み
	 */
	private final static float CHURN_RATE_DELTA = 0.1f;

	// シミュレーションに関する定数
	/**
	 * 試行回数
	 * 何度グラフを初期化して試行するか
	 * TRIALS * GRAPH_TRIALS が試行回数になる
	 */
	private final static int TRIALS = 10;

	/**
	 * グラフに対する試行回数
	 * 1つのグラフに対して何度試行するか
	 * TRIALS * GRAPH_TRIALS が試行回数になる
	 */
	private final static int GRAPH_TRIALS = 50;

	public SimulateChurnVsReachability(String protocolId, int fanout) {
		// プロトコルの設定
		protocol = getProtocol(protocolId, fanout, UPDATE_RATE);
	}

	public void run() throws IOException, InterruptedException {
		// シミュレーションの設定
		// グラフジェネレータの設定
		generator = new RandomGeometricGraphGenerator(RADIUS, X_RANGE, Y_RANGE);
		// churn Graphの設定
		cg = new ChurnGraph();
		// writer
		if(isWrite) writer = new ResultWriter(PATH + protocol.toString() + ".csv");

		// プロトコルの説明などを表示
		printExplain();

		// 変化率を変化させてシミュレーションを実行
		for(float churn = CHURN_RATE_START; churn <= CHURN_RATE_FINISH; churn += CHURN_RATE_DELTA) {
			simulate(churn);
		}

		if(isWrite) writer.close();
	}

	/**
	 * シミュレーションを実行
	 * @param varying
	 * @throws InterruptedException
	 */
	private void simulate(float churnRate) throws InterruptedException {
		// 変化率の設定
		cg.setChurnRate(churnRate);

		Viewer viewer;
		// グラフを初期化する試行のループ
		for(int i = 0; i < TRIALS; i++) {
			// グラフを更新
			Graph graph = generator.generate(graphId++ + "");
			protocol.setGraph(graph);
			cg.setGraph(graph);

			// 同じグラフでの試行のループ
			for(int j = 0; j < GRAPH_TRIALS; j++) {
				trialNum++;

				// 各種初期化
				cg.init();
				protocol.init();

				int cnt = 0;
				for(Edge edge : graph.getEachEdge()) {
					cnt++;
				}

				// display開始
				if (isView) viewer = graph.display(false);

				// シミュレーション実行
				int chengeEdgeNum = 0;
				do {
					if (isView) TimeUnit.SECONDS.sleep(1);
					chengeEdgeNum += cg.run();
				} while(!protocol.run());
				if (isView) TimeUnit.SECONDS.sleep(1);

				// display終了
				if (isView) viewer.close();

				// 結果の出力
				printResult(trialNum, cg.getNodeNum(), churnRate, protocol.getReachability(),
						protocol.getMsgNum(), protocol.getHopNum(), chengeEdgeNum, cnt);
			}
		}
	}


	/**
	 * プロトコルの説明などを表示
	 * @param protocolName プロトコル名
	 */
	private void printExplain() {
		String query = "id,nodeNum,ChurnRate,reachability,msgNum,hopNum,aveChangeEdgeNum,edgeNum";
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
	 * @param churn グラフのエッジの変化率
	 * @param reach メッセージが到達した割合
	 * @param msg メッセージ数
	 * @param hop ホップ数
	 */
	private void printResult(int trial, int node, float churn, float reach, int msg, int hop, int ChangeEdgeNum, int edgeNum) {
		String str = trial + "," + node + "," + churn + "," + reach + "," + msg + "," + hop + "," + (int)(ChangeEdgeNum/hop) + "," + edgeNum;
		if(isWrite) {
			writer.println(str);
		}
		System.out.println(str);
	}

}
