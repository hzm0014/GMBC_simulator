import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

/**
 * エッジとその属すエリアを表す構造体
 */
class EdgeArea {
	Edge edge;
	String area;
	boolean isDead;
	EdgeArea(Edge edge, String area) {
		this.edge = edge;
		this.area = area;
		isDead = false;
	}
	public EdgeArea(Edge edge, int area) {
		this.edge = edge;
		if (area == 0) {
			this.area = "odd";
		} else {
			this.area = "even";
		}
		isDead = false;
	}
}

/**
 * Time-VeryingGraphを再現するクラス．
 * 1stepごとに各エッジが確率で切断，再接続する<br>
 * 再接続されるエッジは一度接続されていたエッジだけであり，全く新しいエッジが生まれることはない<br>
 * コンストラクタで対象になるグラフを指定し，runで1step変化する
 */
public class TimeVaryingGraph {
	/**
	 * 乱数生成器
	 */
	private Random rnd = new Random();

	/**
	 * 対象となるグラフ
	 */
	private Graph graph;

	/**
	 * 離脱エッジが固定数で計測するか
	 */
	private boolean varyingFixedMode;

	/**
	 * 切断，再接続される確率
	 */
	private float varyingRate;

	/**
	 * 切断離脱する個数
	 */
	private int varyingFixedNum;

	// グラフの切断に偏りのあるグラフ
	/**
	 * 偏りの強さ
	 */
	private float biasRate = 0.0f;

	/**
	 * 分割するときの1エリアの横幅
	 */
	private int areaX;

	/**
	 * 分割するときの1エリアの縦幅
	 */
	private int areaY;

	/**
	 * エッジとエリアのリスト
	 */
	private ArrayList<EdgeArea> edgeList = new ArrayList<EdgeArea>();

	/**
	 * グラフを設定する（偏りのある切断に必要な情報を付加）
	 * @param graph
	 * @param rangeX
	 * @param rangeY
	 * @param separateX
	 * @param separateY
	 */
	public void setGraph(Graph graph, float rangeX, float rangeY, int separateX, int separateY) {
		edgeList.clear();

		this.graph = graph;
		this.areaX = (int)rangeX / separateX;
		this.areaY = (int)rangeY / separateY;

		// エッジを奇数エリアと偶数エリアで振り分け
		for(Edge edge : graph.getEachEdge()) {
			// エッジの中心座標を求める
			int x = ((int)edge.getNode0().getAttribute("x") + (int)edge.getNode1().getAttribute("x")) / 2;
			int y = ((int)edge.getNode0().getAttribute("y") + (int)edge.getNode1().getAttribute("y")) / 2;
			// どちらのエリアに属すのか判定
			// judgeが偶数なら奇数エリア，奇数なら偶数エリア（0を含むのでここでどうしてもずれる）
			int judge = (int)(x / areaX) + (int)(y / areaY);
			edgeList.add(new EdgeArea(edge, judge % 2));
		}
	}

	/**
	 * グラフを設定する
	 * @param graph 対象になるグラフ
	 */
	public void setGraph(Graph graph) {
		setGraph(graph, 150, 150, 3, 3);
	}

	/**
	 * 切断，再接続される確率を設定する
	 * @param varyingRate 切断，再接続される確率
	 */
	public void setVaryingRate(float varyingRate) {
		this.varyingRate = varyingRate;
		// ランダムに選択する
		this.varyingFixedMode = false;
	}

	/**
	 * 切断，再接続するエッジ数を設定する
	 * @param varyingFixNum
	 */
	public void setVaryingFixNum(int varyingFixNum) {
		this.varyingFixedNum = varyingFixNum;
		// 固定数選択する
		this.varyingFixedMode = true;
	}

	/**
	 * 偏りのあるグラフの設定
	 * @param biasRate
	 * @param rangeX
	 * @param rangeY
	 * @param separateX
	 * @param separateY
	 */
	public void setBias(float biasRate) {
		this.biasRate = biasRate;
	}

	/**
	 * 初期化．切断しているエッジを復帰させる
	 */
	public void init() {
		// 切断中の全てのエッジを再接続
		for (EdgeArea edge : edgeList) {
			if(edge.isDead) {
				revaivalEdge(edge);
			}
		}
	}

	/**
	 * 1stepだけ実行する．
	 * 接続されているエッジは確率で切断され，切断されているエッジは確率で再接続される
	 */
	public void run() {
		if (varyingFixedMode) {
			runInFixed();
		} else {
			runInRandom();
		}
	}

	/**
	 * 1stepだけ実行する
	 * 固定数エッジを選択
	 */
	private void runInFixed() {
		Collections.shuffle(edgeList);
		int cnt = 1;
		for (EdgeArea edge : edgeList) {
			// isDeadがfalseなら切断，trueなら再接続
			if (edge.isDead) {
				revaivalEdge(edge);
			} else {
				removeEdge(edge);
			}

			if (cnt > varyingFixedNum)
				break;
			cnt++;
		}
	}

	/**
	 * 1stepだけ実行する
	 * ランダムにエッジを選択する
	 */
	private void runInRandom() {
		// エッジのリストを周回
		for(EdgeArea edge : edgeList) {
			// 属するエリアで変化する確率を決定
			float varyingRate = getVaryingRate(edge.area);

			// 確率でエッジが変化
			if (rnd.nextDouble() <= varyingRate) {

				// isDeadがfalseなら切断，trueなら再接続
				if (edge.isDead) {
					revaivalEdge(edge);
				} else {
					removeEdge(edge);
				}
			}
		}
	}

	/**
	 * エリアにおける変化の確率を計算
	 * @param area エッジが属するエリア
	 * @return 変化の確率
	 */
	private float getVaryingRate(String area) {
		float rate = varyingRate;
		if (area.equals("odd")) {
			rate -= varyingRate * biasRate;
		} else if(area.equals("even")) {
			rate += varyingRate * biasRate;
		}
		return rate;
	}

	/**
	 * エッジを切断
	 * @param edge
	 */
	private void removeEdge(EdgeArea edge) {
		graph.removeEdge(edge.edge);
		edge.isDead = true;
	}

	/**
	 * エッジを接続
	 * @param edge
	 */
	private void revaivalEdge(EdgeArea edge) {
		String source = edge.edge.getNode0().getId();
		String target = edge.edge.getNode1().getId();
		edge.edge =  graph.addEdge(source + target, source, target);
		edge.isDead = false;
	}
}
