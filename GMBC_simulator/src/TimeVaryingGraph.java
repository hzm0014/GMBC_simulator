import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

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
	 * 切断，再接続される確率
	 */
	private double varyingRate;

	/**
	 * 切断されているエッジを保持しておくキュー
	 */
	private Queue<Edge> deadingEdges = new ArrayDeque<Edge>();

	/**
	 * コンストラクタ
	 * @param graph 対象になるグラフ
	 * @param varyingRate 切断，再接続される確率
	 */
	public TimeVaryingGraph(Graph graph, double varyingRate) {
		this.graph = graph;
		this.varyingRate = varyingRate;
	}

	/**
	 * 初期化．切断しているエッジを復帰させる
	 */
	public void init() {
		updateGraph(new ArrayDeque<Edge>(), deadingEdges);
	}

	/**
	 * 1stepだけ実行する．
	 * 接続されているエッジは確率で切断され，切断されているエッジは確率で再接続される
	 */
	public void run() {
		// 切断されるエッジの決定
		Queue<Edge> removeEdges  = removeEdge();
		// 再接続されるエッジの決定
		Queue<Edge> revivalEdges = revivalEdge();
		// グラフを更新する
		updateGraph(removeEdges, revivalEdges);
	}

	/**
	 * 接続中のエッジから，切断されるエッジを決定する
	 * @return 切断されるエッジのキュー
	 */
	private Queue<Edge> removeEdge() {
		// 切断されるエッジ
		Queue<Edge> removeEdges = new ArrayDeque<Edge>();

		// 確率で切断されるエッジを決定
		for(Edge edge : graph.getEachEdge()) {
			if (rnd.nextDouble() <= varyingRate) {
				removeEdges.add(edge);
			}
		}

		return removeEdges;
	}

	/**
	 * 切断中のエッジから，再接続されるエッジを決定する
	 * @return 再接続されるエッジのキュー
	 */
	private Queue<Edge> revivalEdge() {
		// 再接続されるエッジ
		Queue<Edge> revivalEdges = new ArrayDeque<Edge>();
		// 再接続されなかったエッジ
		Queue<Edge> nextDeadingEdges = new ArrayDeque<Edge>();

		// 確率で再接続されるエッジを決定
		Edge edge;
		while((edge = deadingEdges.poll()) != null) {
			if(rnd.nextDouble() <= varyingRate) {
				revivalEdges.add(edge);
			} else {
				// 選ばれなければ次のstepで再試行
				nextDeadingEdges.add(edge);
			}
		}

		// 残ったエッジは次のstepで再試行
		deadingEdges = nextDeadingEdges;
		return revivalEdges;
	}

	/**
	 * グラフの更新（エッジの切断と再接続）を行う
	 * @param removeEdges 切断されるエッジ
	 * @param revivalEdges 再接続されるエッジ
	 */
	private void updateGraph(Queue<Edge> removeEdges, Queue<Edge> revivalEdges) {
		Edge edge;
		// エッジの切断
		while((edge = removeEdges.poll()) != null) {
			graph.removeEdge(edge);
			deadingEdges.add(edge);
		}

		// エッジの再接続
		while((edge = revivalEdges.poll()) != null) {
			String source = edge.getNode0().getId();
			String target = edge.getNode1().getId();
			graph.addEdge(source + target, source, target);

			deadingEdges.remove(edge);
		}
	}
}
