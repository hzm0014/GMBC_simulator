import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;;

/**
 * メッセージ配送プロトコルの各親クラス
 */
public class Protocol {
	/**
	 * 乱数生成器
	 */
	Random rnd = new Random();

	/**
	 * 対象となるグラフ
	 */
	private Graph graph;

	/**
	 * メッセージ送信待ちのノード
	 */
	Queue<Node> waitNodes = new ArrayDeque<Node>();

	/**
	 * コンストラクタ
	 * @param graph 対象となるグラフ
	 */
	public Protocol(Graph graph) {
		this.graph = graph;
	}

	/**
	 * 初期化．
	 * 全てのノードの受診済みのマーキングを初期化し，一つ目のノード(source)を追加
	 */
	public void init() {
		// 全てのノードを未受診に
		for(Node node : graph.getEachNode()) {
			node.addAttribute("Infection", false);
			node.addAttribute("ui.style", "fill-color: rgb(0,0,0);");
		}
		// 一つ目のノード(source)を追加
		Node source = graph.getNode("n1");
		sendMsg(source);
		waitNodes.add(source);
	}

	/**
	 * プロトコルの実行．
	 * 1hopだけ進める
	 * @return プロトコルが終了するか
	 */
	public boolean run() {
		// 次のstepでのsendNodes
		Queue<Node> nextWaitNodes = new ArrayDeque<Node>();

		// 送信待ちのノードからメッセージを送信
		Node from;
		while((from = waitNodes.poll()) != null) {
			// 転送先を選択する
			Queue<Node> sendNode = choiceNode(from);
			// 決定した転送先へ転送
			Node to;
			while((to = sendNode.poll()) != null) {
				boolean isSend = sendMsg(to);
				if(isSend) {
					nextWaitNodes.add(to);
				}
			}
		}

		waitNodes = nextWaitNodes;

		// 待機中のノードがなければプロトコル終了
		if (waitNodes.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * メッセージを送信
	 * @param to 送信先
	 */
	private boolean sendMsg(Node to) {
		// 受信済みであれば処理しない
		if((boolean)to.getAttribute("Infection")) {
			return false;
		}
		// 受信済みのマーキング
		to.addAttribute("Infection", true);
		to.addAttribute("ui.style", "fill-color: rgb(255,0,0);");
		return true;
	}

	/**
	 * 転送するノードを選択する．
	 * このメソッドをオーバーライドすることで，プロトコルを作成する
	 * @param from 送信元
	 * @return 選択したノード
	 */
	private Queue<Node> choiceNode(Node from) {
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
}