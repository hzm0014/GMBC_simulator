import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.graphstream.graph.Node;

class Flooding extends Protocol {

	/**
	 * 転送するノードを選択する．
	 * 送信元以外の全てのノードに対して転送する
	 * @param from 送信元
	 * @return 選択したノード
	 */
	protected Queue<Node> choiceNode(Node from) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 転送するノードを選択する
		Iterator<? extends Node> neighbor = from.getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			if (to == from) {
				continue;
			}
			sendNodes.add(to);
		}

		return sendNodes;
	}

	/**
	 * プロトコル名を返す
	 * @return プロトコル名
	 */
	public String getName() {
		return "Flooding";
	}
}