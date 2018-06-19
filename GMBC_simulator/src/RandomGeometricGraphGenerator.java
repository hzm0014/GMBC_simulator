import java.util.Random;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

/**
 * RandamGeometricGraphを生成する
 */
public class RandomGeometricGraphGenerator {
	/**
	 * 乱数生成器
	 */
	private Random rnd = new Random();

	/**
	 * 作成するグラフ
	 */
	private static Graph graph = null;

	/**
	 * ノード数
	 */
	private int nodeNum;

	/**
	 * ノードが接続する半径距離（閾値）
	 * 2ノードの座標のユークリウッド距離がradius以下であれば接続する
	 */
	private float radius;

	/**
	 * x軸方向の範囲
	 */
	private float xRange;

	/**
	 * y軸方向の範囲
	 */
	private float yRange;

	/**
	 * ポワソン分布のパラメータ
	 */
	private final float e = 0.1f;

	/**
	 * コンストラクタ
	 * @param nodeNum ノード数
	 * @param radius ノードが接続する半径距離（閾値）
	 */
	public RandomGeometricGraphGenerator(int nodeNum, float radius, float xRange, float yRange) {
		this.nodeNum= nodeNum;
		this.radius = radius;
		this.xRange = xRange;
		this.yRange = yRange;
	}

	/**
	 * ノード数のみ指定するコンストラクタ．ポワソン分布によりノードの接続する半径距離（閾値）を算出する
	 * @param nodeNum ノード数
	 */
	public RandomGeometricGraphGenerator(int nodeNum, float xRange, float yRange) {
		this(nodeNum, 0.0f, xRange, yRange);
		double area = xRange * yRange;
		this.radius = (float) Math.sqrt(((1+e) * Math.log(area)) / (nodeNum / area * Math.PI));
	}

	/**
	 * ノードの接続する半径距離（閾値）のみ指定するコンストラクタ．ポワソン分布によりノード数を算出する
	 * @param radius ノードの接続する半径距離（閾値）
	 */
	public RandomGeometricGraphGenerator(float radius, float xRange, float yRange) {
		this(0, radius, xRange, yRange);
		double area = xRange * yRange;
		this.nodeNum = (int)(((1+e) * Math.log(area) * area) / (Math.PI * radius * radius));
	}

	/**
	 * グラフの範囲を設定する
	 * @param xRange x軸方向の範囲
	 * @param yRange y軸方向の範囲
	 */
	public void setRange(float xRange, float yRange) {
		this.xRange = xRange;
		this.yRange = yRange;
	}

	/**
	 * Random Geometric Graphを生成する
	 * 必ず連結しているグラフを生成するが，limit回思考中に一度も連結しなかった場合はふ連結なグラフを返す
	 * @param arg グラフ名
	 * @return 生成したグラフ
	 */
	public Graph generate(String arg) {
		// グラフを生成
		do {
			graph = new SingleGraph("RGG: " + arg);
			for(int i = 0; i < nodeNum; i++) {
				// ノードを追加
				addNode("n" + i + "");
			}
		// 連結していなければ再生成
		}while(!Toolkit.isConnected(graph));

		return graph;
	}

	/**
	 * ノードを追加する
	 * @param args ノード名
	 * @param x x座標
	 * @param y y座標
	 * @return 追加したノード
	 */
	void addNode(String arg, int x, int y) {
		Node node = graph.addNode(arg);
		node.addAttribute("x", x);
		node.addAttribute("y", y);
		// エッジを追加
		addEdge(node);
	}

	/**
	 * ランダムな座標にノードを追加する
	 * @param arg ノード名
	 * @return 追加したノード
	 */
	void addNode(String arg) {
		addNode(arg, rnd.nextInt((int)xRange), rnd.nextInt((int)yRange));
	}

	/**
	 * ノードにエッジを追加する．
	 * 2ノード間のユークリウッド距離がradius以下であれば接続する
	 * @param addedNode 追加されたノード
	 */
	void addEdge(Node addedNode) {
		for(Node node : graph.getEachNode()) {
			if (node != addedNode && isConnect(addedNode, node, radius)) {
				graph.addEdge(addedNode.getId() + node.getId() + "", addedNode.getId() + "", node.getId() + "");
			}
		}
	}

	/**
	 * 接続するかどうかを決定する
	 * @param node0 ノード0
	 * @param node1 ノード1
	 * @param r 半径距離（閾値）
	 * @return 接続するかどうか
	 */
	boolean isConnect(Node node0, Node node1, double r) {
		int x0 = (int) node0.getNumber("x");
		int y0 = (int) node0.getNumber("y");
		int x1 = (int) node1.getNumber("x");
		int y1 = (int) node1.getNumber("y");
		return r * r >= ((x0 -x1) * (x0 - x1)) + ((y0 - y1) * (y0 - y1));
	}

}
