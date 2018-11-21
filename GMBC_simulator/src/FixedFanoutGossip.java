import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;

import org.graphstream.graph.Node;

/**
 * FixedFanoutGossipをシミュレートするクラス
 * メッセージを受け取ったノードが，隣接ノードからいつくかをランダムに選択してメッセージを転送する
 * パラメータにFanoutを持ち，隣接ノードを選択する際に選ぶノードの数を表す
 */
public class FixedFanoutGossip extends Protocol {

	/**
	 * 送信するメッセージ数
	 */
	private int fanout;

	/**
	 * コンストラクタ
	 * ノードがメッセージを転送する数であるFanout数を設定する
	 * @param fanout Fanout数
	 */
	public FixedFanoutGossip(int fanout) {
		this.fanout = fanout;
	}

	/**
	 * 転送するノードを選択する
	 * 隣接ノードからランダムにFanout個選択する
	 * 隣接ノードの数がFanoutより小さければ全てに送信する
	 * @param from 送信元
	 * @return 選択したノード
	 */
	@Override
	protected Queue<Node> choiceNode(Message msg) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 隣接ノードをList型へ変換してシャッフル
		ArrayList<Node> tmpList = new ArrayList<Node>();
		Iterator<? extends Node> neighbor = msg.getTo().getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			if (to == msg.getFrom()) {
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

	@Override
	protected void updateInTern() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public String toString() {
		return "FFG_" + fanout;
	}
}