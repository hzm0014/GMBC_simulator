import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import org.graphstream.graph.Node;

public class FixedFanoutGossip extends Protocol {

	/**
	 * 送信するメッセージ数
	 */
	private int fanout;

	public FixedFanoutGossip(int fanout) {
		this.fanout = fanout;
	}

	/**
	 * 隣接ノードからランダムにFanout個選択する
	 * 隣接ノードの数がFanoutより小さければ全てに送信する
	 * @param from 送信元
	 * @return 選択したノード
	 * @override
	 */
	protected Queue<Node> choiceNode(Node from) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 隣接ノードをList型へ変換してシャッフル
		ArrayList<Node> tmpList = new ArrayList<Node>();
		Iterator<? extends Node> neighbor = from.getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			if (to == from) {
				continue;
			}
			tmpList.add(to);
		}
		Collections.shuffle(tmpList);

		// 頭からFanout個だけ取り出す
		int sendNum = Math.min(fanout, tmpList.size());
		for (int i = 0; i < sendNum; i++) {
			sendNodes.add(tmpList.get(i));
		}

		return sendNodes;
	}
}