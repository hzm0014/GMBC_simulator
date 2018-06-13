import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;

public class Simulator {

	// グラフに関する定数

	/**
	 * グラフのノード数
	 */
	//private final static int NODE_NUM = 1000;

	/**
	 * ノードが接続する半径距離（閾値）
	 */
	private final static float RADIUS = 5.0f;

	/**
	 * グラフのx軸方向の範囲
	 */
	private final static float X_RANGE = 100;

	/**
	 * グラフのy軸方向の範囲
	 */
	private final static float Y_RANGE = 100;

	// Time-Varying Graphに関する定数

	/**
	 * 切断，再接続される確率
	 */
	private final static float VARYING_RATE = 0.5f;


	public static void main(String args[]) throws InterruptedException {
		// グラフを生成する
		int graphId = 0;
		RandomGeometricGraphGenerator generator = new RandomGeometricGraphGenerator(RADIUS, X_RANGE, Y_RANGE);
		Graph graph = generator.generate(graphId++ + "");

		// display
		graph.display(false);

		// プロトコルの設定
		Protocol protocol = new Protocol(graph);

		// Time-Varying Graphの設定
		TimeVaryingGraph tvg = new TimeVaryingGraph(graph, VARYING_RATE);

		for(;;) {
			protocol.init();
			tvg.init();

			TimeUnit.SECONDS.sleep(1);
			while(!protocol.run()) {
				TimeUnit.SECONDS.sleep(1);
				tvg.run();
			}
		}
	}
}