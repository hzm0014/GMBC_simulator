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
	protected Graph graph;

	/**
	 * メッセージ送信待ちのノード
	 */
	private Queue<Message> waitNodes = new ArrayDeque<Message>();

	// 取得するデータ
	/**
	 * 到達率（全ノードのうち，どれだけのノードがメッセージを受け取ったか）を計測するためのカウンタ
	 * メッセージを受信したノードの数をカウント
	 */
	private int receivedNodeNum;

	/**
	 * ノード数
	 * 到達率を求めるために保持
	 */
	private int nodeNum;

	/**
	 * メッセージ数のカウント
	 */
	protected int msgNum;

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
		nodeNum = graph.getNodeCount();
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
		waitNodes.add(new Message(null, source));
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
		Queue<Message> nextWaitNodes = new ArrayDeque<Message>();

		// ターンの開始時の処理
		updateInTern();

		// 送信待ちのノードからメッセージを送信
		Message msg;
		while((msg = waitNodes.poll()) != null) {
			Node receive = msg.getTo();

			// 転送先を選択する
			Queue<Node> sendNode;
			if (receive.getId().equals("n1")) {
				// ソースノードの場合
				sendNode = firstChoiceNode(msg);
			} else {
				// それ以外のノードの場合
				sendNode = choiceNode(msg);
			}
			// 決定した転送先へ転送
			Node to;
			while((to = sendNode.poll()) != null) {
				boolean isSend = sendMsg(to);
				if(isSend) {
					nextWaitNodes.add(new Message(receive, to));
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
	abstract protected Queue<Node> choiceNode(Message msg);

	/**
	 * ソースノードによる転送を行うノードを選択する
	 * 全ての隣接ノードに対しメッセージを転送する
	 * @param from 転送元（ソースノード）
	 * @return 選択したノード
	 */
	private Queue<Node> firstChoiceNode(Message msg) {
		// 転送するノード
		Queue<Node> sendNodes = new ArrayDeque<Node>();

		// 転送するノードを選択する
		Iterator<? extends Node> neighbor = msg.getTo().getNeighborNodeIterator();
		while(neighbor.hasNext()) {
			Node to = neighbor.next();
			sendNodes.add(to);
		}

		return sendNodes;
	}

	/**
	 * ターンの開始時に変更を加える
	 */
	abstract protected void updateInTern();

	/**
	 * 到達率（全ノードのうち，どれだけのノードがメッセージを受け取ったか）を返す
	 * @return 到達率
	 */
	public float getReachability() {
		return (float)receivedNodeNum / (float)nodeNum;
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

	public abstract String toString();
}