import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

// 送信されてきたノードへ送り返しているかもしれない

/**
 * GMBCをシミュレートするクラス
 * メッセージを受け取ったノードが，ノードがもつMBCに従ってメッセージの転送を行う
 * パラメータにFanoutを持ち，隣接ノードを選択する際に選ぶノードの数を表す
 */
public class GossipForMBC extends Protocol {
	/**
	 * 乱数生成器
	 */
	private Random rnd = new Random();

	private MBCGenerator mbcGenerator = new MBCGenerator();

	/**
	 * 送信するメッセージ数
	 */
	private int fanout;

	/**
	 * ノードがメッセージの転送先を選択する前にMBCを更新する確率
	 */
	private float updateRate;

	public void init() {
		super.init();
		// MBCを更新
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		while(nodes.hasNext()) {
			Node node = nodes.next();
			// MBCを更新
			node.setAttribute("MBC", mbcGenerator.generate(node));
		}
	}

	/**
	 * コンストラクタ
	 * ノードがメッセージを転送する数であるFanout数を設定する
	 * @param fanout Fanout数
	 */
	public GossipForMBC(int fanout, float updateRate) {
		this.fanout = fanout;
		this.updateRate = updateRate;
	}

	/**
	 * グラフを設定する
	 * 各ノードのMBCを設定する
	 * @param graph 対象となるグラフ
	 */
	@Override
	public void setGraph(Graph graph) {
		super.setGraph(graph);
		// 各ノードのMBCを生成する
		MBCGenerator mbcGene = new MBCGenerator();
		for (Node node : graph.getNodeSet()) {
			node.setAttribute("MBC", mbcGene.generate(node));
		}
	}

	/**
	 * 転送するノードを選択する
	 * 隣接ノードからMBCに従ってFanout個選択する
	 * 隣接ノードの数がFanoutより小さければ全てに送信する
	 * 確率で選択する前にMBCの更新を行う
	 * @param from 送信元
	 * @return 選択したノード
	 */
	@Override
	protected Queue<Node> choiceNode(Message msg) {
		// 受信者
		Node receive = msg.getTo();

		// 更新が確実ならMBCを更新
		if (updateRate == 1.0f) {
			receive.setAttribute("MBC", mbcGenerator.generate(receive));
		}

		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 隣接ノードを2つに区分
		// 優先して送るノード
		ArrayList<Node> first = new ArrayList<Node>();
		// firstを送り切った後に選ばられるノード
		ArrayList<Node> second = new ArrayList<Node>();

		// 受信者のMBC
		MBC mbc = (MBC)receive.getAttribute("MBC");

		// MBCを参考に各隣接ノードの優先度を振り分け
		Iterator<? extends Node> neighbor = receive.getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			// 送信者へは送り返さない
			if (to == msg.getFrom()) {
				continue;
			}
			// それ以外は距離によって優先度を変える
			int dist = mbc.getDist(msg.getFrom(), to);
			if (mbc.isInf(dist)) {
				first.add(to);
			} else {
				second.add(to);
			}
		}

		//順序を入れ替えて合体
		Collections.shuffle(first);
		Collections.shuffle(second);
		first.addAll(second);

		// 頭からFanout個だけ取り出す
		int sendNum = Math.min(fanout, first.size());
		for (int i = 0; i < sendNum; i++) {
			sendNodes.add(first.get(i));
		}

		return sendNodes;
	}


	/**
	 * 各ターンの開始時に更新を行う
	 * 各ノードのMBCを確率で更新
	 */
	@Override
	protected void updateInTern() {
		Iterator<? extends Node> nodes = graph.getNodeIterator();
		while(nodes.hasNext()) {
			Node node = nodes.next();
			// 確率でMBCを更新
			if (rnd.nextDouble() <= updateRate) {

				// 更新が確実ならスルー（転送前に更新する）
				if (updateRate == 1.0f) {
					msgNum += node.getEdgeSet().size();
					continue;
				}

				node.setAttribute("MBC", mbcGenerator.generate(node));
				// メッセージ数を増加
				msgNum += node.getEdgeSet().size();
			}
		}
	}

	@Override
	public String toString() {
		return "GMBG_" + fanout;
	}
}
