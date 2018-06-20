import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;;

/**
 * メッセージ配送プロトコルの各親クラス
 */
public abstract class Protocol {
	/**
	 * 対象となるグラフ
	 */
	private Graph graph;

	/**
	 * メッセージ送信待ちのノード
	 */
	private Queue<Node> waitNodes = new ArrayDeque<Node>();

	// 取得するデータ
	/**
	 * 到達率（全ノードのうち，どれだけのノードがメッセージを受け取ったか）を計測するためのカウンタ
	 * メッセージを受信したノードの数をカウント
	 */
	private int receivedNodeNum;

	/**
	 * メッセージ数のカウント
	 */
	private int msgNum;

	/**
	 * ホップ数のカウント
	 */
	private int hopNum;

	/**
	 * グラフを設定する
	 * @param graph 対象となるグラフ
	 */
	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * 初期化．
	 * 全てのノードの受診済みのマーキングを初期化し，一つ目のノード(source)を追加
	 */
	public void init() {
		// 全てのノードを未受診に
		// clearAttributeで行けるかも
		for(Node node : graph.getEachNode()) {
			node.addAttribute("Infection", false);
			node.addAttribute("ui.style", "fill-color: rgb(0,0,0);");
		}

		// データ型変数の初期化
		receivedNodeNum = 0;
		msgNum = 0;
		hopNum = 0;

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
		// ホップ数をカウント
		hopNum++;

		// 次のstepでのsendNodes
		Queue<Node> nextWaitNodes = new ArrayDeque<Node>();

		// 送信待ちのノードからメッセージを送信
		Node from;
		while((from = waitNodes.poll()) != null) {
			// 転送先を選択する
			Queue<Node> sendNode;
			if (from.getId().equals("n1")) {
				// ソースノードの場合
				sendNode = firstChoiceNode(from);
			} else {
				// それ以外のノードの場合
				sendNode = choiceNode(from);
			}
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
		// メッセージ送信のカウント
		msgNum++;

		// 受信済みであれば処理しない
		if((boolean)to.getAttribute("Infection")) {
			return false;
		}

		// 受信済みのマーキング
		to.addAttribute("Infection", true);
		to.addAttribute("ui.style", "fill-color: rgb(255,0,0);");

		// 受信済みノードのカウント
		receivedNodeNum++;
		return true;
	}

	/**
	 * 転送するノードを選択する．
	 * このメソッドをオーバーライドすることで，プロトコルを作成する
	 * @param from 送信元
	 * @return 選択したノード
	 */
	abstract protected Queue<Node> choiceNode(Node from);

	/**
	 * ソースノードによる転送を行うノードを選択する
	 * 全ての隣接ノードに対しメッセージを転送する
	 * @param from 転送元（ソースノード）
	 * @return 選択したノード
	 */
	private Queue<Node> firstChoiceNode(Node from) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 転送するノードを選択する
		Iterator<? extends Node> neighbor = from.getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			sendNodes.add(to);
		}

		return sendNodes;
	}

	/**
	 * 到達率（全ノードのうち，どれだけのノードがメッセージを受け取ったか）を返す
	 * @return 到達率
	 */
	public float getReachability() {
		return (float)receivedNodeNum / (float)graph.getNodeCount();
	}

	/**
	 * 送信したメッセージ数を返す
	 * @return メッセージ数
	 */
	public int getMsgNum() {
		return msgNum;
	}

	/**
	 * かかったホップ数（ターン数）を返す
	 * @return ホップ数
	 */
	public int getHopNum() {
		return hopNum;
	}
}