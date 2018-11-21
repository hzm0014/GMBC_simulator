import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

class Node_ {
	Node node;
	ArrayList<String> neighter = new ArrayList<String>();
	boolean isDead;
	Node_(Node node) {
		this.node = node;
		Iterator<Node> it = node.getNeighborNodeIterator();
		while(it.hasNext()) {
			neighter.add(it.next().getId());
		}
		this.isDead = false;
	}
}

public class ChurnGraph {
	/**
	 * 乱数生成器
	 */
	private Random rnd = new Random();

	/**
	 * 対象となるグラフ
	 */
	private Graph graph;

	/**
	 * ノードが離脱，復帰する確率
	 */
	private float churnRate;

	private ArrayList<Node_> nodeList = new ArrayList<Node_>();

	// 対象のグラフを設定
	public void setGraph(Graph graph) {
		this.graph = graph;
		// ノードのリストを更新
		nodeList.clear();
		for (Node node : graph.getEachNode()) {
			nodeList.add(new Node_(node));
		}
	}

	// churnrateを設定
	public void setChurnRate(float churnRate) {
		this.churnRate = churnRate;
	}

	// 初期化
	// グラフを使い回す時に
	// setに統合できそう
	public void init() {
		// 切断中の全てのノードを再接続
		for (Node_ node  : nodeList) {
			if(node.isDead) {
				revaivalNode(node);
			}
		}
	}

	// 1stepだけ実行
	public int run() {
		// 変化したエッジの総数
		int changeEdgeNum = 0;
		// 各ノードが確率で切断，復帰
		for (Node_ node : nodeList) {
			if (rnd.nextDouble() <= churnRate) {
				// 切断中なら復帰，活動中なら切断
				if (node.isDead) {
					changeEdgeNum += revaivalNode(node);
				} else {
					changeEdgeNum += removeNode(node);
				}
			}
		}
		return changeEdgeNum;
	}

	/**
	 * ノードが離脱
	 * @param node
	 */
	private int removeNode(Node_ node) {
		int changeEdgeNum = 0;
		for (Edge e : node.node.getEachEdge()) {
			changeEdgeNum++;
		}
		graph.removeNode(node.node);
		node.isDead = true;
		return changeEdgeNum;
	}

	/**
	 * ノードを復帰
	 * @param node
	 */
	private int revaivalNode(Node_ node) {
		int changeEdgeNum = 0;
		String source = node.node.getId();
		Node createNode = graph.addNode(source);
		createNode.setAttribute("x", (int)node.node.getAttribute("x"));
		createNode.setAttribute("y", (int)node.node.getAttribute("y"));
		createNode.setAttribute("Infection", (boolean)node.node.getAttribute("Infection"));
		createNode.setAttribute("ui.style", (String)node.node.getAttribute("ui.style"));
		for (String target : node.neighter) {
			if (isDead(target)) {
				continue;
			}
			graph.addEdge(source + target, source, target);
			changeEdgeNum++;
		}
		node.node = createNode;
		node.isDead = false;
		return changeEdgeNum;
	}

	/**
	 * ノードが生きてるか返す
	 * NodeListをMapとかにするとサーチしやすいかも
	 * @param node
	 * @return
	 */
	private boolean isDead(String target) {
		for (Node_ n : nodeList) {
			if (target.equals(n.node.getId())) {
				return n.isDead;
			}
		}
		return false;
	}

	/**
	 * ノードの総数を取得
	 * @return ノード総数
	 */
	public int getNodeNum() {
		return nodeList.size();
	}
}
