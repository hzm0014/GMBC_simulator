import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.graphstream.graph.Node;

public class createMBC {

	private int TTL = 1;

	public MBC create(Node root) {
		MBC mbc = new MBC();

		// 転送するノードを選択する
		Iterator<? extends Node> rootNeighbor = root.getNeighborNodeIterator();
		while(rootNeighbor.hasNext()) {
			Node from = rootNeighbor.next();
			// 待機ノードとメッセージ(TTL)のQueue
			// 2つが対応するように追加する
			Queue<Node> nodeQueue = new ArrayDeque<Node>();
			Queue<Integer> msgQueue = new ArrayDeque<Integer>();
			// 確認済みのノード
			Set<Node> checked = new TreeSet<Node>();
			// fromをQueueに追加
			nodeQueue.add(from);
			msgQueue.add(TTL);
			checked.add(from);
			// queueが空になるまでループ
			while(!nodeQueue.isEmpty()) {
				// 一つ取り出して，隣接ノードによって処理を行う
				Node to = nodeQueue.poll();
				int currentTtl = msgQueue.poll();
				Iterator<? extends Node> fromNeighbor = to.getNeighborNodeIterator();
				// 　各隣接ノードから処理を行う
				while(fromNeighbor.hasNext()) {
					Node node = fromNeighbor.next();
					// rootと隣接しているなら，距離を追加
					if(node == root) {
						mbc.setDist(from, to, (TTL - currentTtl) + 1);
					}
					// それ以外で，まだチェックしていない
					// かつ，TTLが0出ないならqueueに追加
					else if (!checked.contains(node) && currentTtl > 0) {
						nodeQueue.add(node);
						msgQueue.add(currentTtl - 1);
						checked.add(node);
					}
				}
			}
		}

		// 3重ループで最短距離の計算
		// 途中で経由するノードのループ
		Iterator<? extends Node> i = root.getNeighborNodeIterator();
		while(i.hasNext()) {
			Node mid = i.next();
			// スタート地点のノードのループ
			Iterator<? extends Node> j = root.getNeighborNodeIterator();
			while(j.hasNext()) {
				Node start = j.next();
				// ゴール地点のノードのループ
				Iterator<? extends Node> k = root.getNeighborNodeIterator();
				while(k.hasNext()) {
					Node goal = k.next();
					mbc.setDist(start, goal, mbc.getDist(start, mid) + mbc.getDist(mid, goal));
				}
			}
		}
		return mbc;
	}

}
