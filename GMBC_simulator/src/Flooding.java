import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 * Floodingをシミュレートするクラス
 * メッセージを受け取ったノードが，全ての隣接ノードへメッセージを転送する
 */
class Flooding extends Protocol {

	/**
	 * グラフを設定する
	 * @param graph 対象となるグラフ
	 */
	protected void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * 転送するノードを選択する．
	 * 送信元以外の全てのノードに対して転送する
	 * @param from 送信元
	 * @return 選択したノード
	 */
	protected Queue<Node> choiceNode(Message msg) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 転送するノードを選択する
		Iterator<? extends Node> neighbor = msg.getTo().getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			// 送信元へは転送しない
			if (to == msg.getFrom()) {
				continue;
			}
			sendNodes.add(to);
		}
		return sendNodes;
	}

	@Override
	protected void updateInTern() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String toString() {
		return "Flooding";
	}
}