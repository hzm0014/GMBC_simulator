import org.graphstream.graph.Node;

/**
 * メッセージのクラス
 * シミュレーションでメッセージのキューに追加していくオブジェクト
 * 送信者と受信者を把握するために利用する
 */
public class Message {
	/**
	 * メッセージの送信者
	 */
	private Node from;

	/**
	 * メッセージの受信者
	 */
	private Node to;

	/**
	 * メッセージのコンストラクタ
	 * 送信者と受信者を追加する
	 * @param from 送信者
	 * @param to 受信者
	 */
	public Message(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * 送信者を取得する
	 * @return 送信者
	 */
	public Node getFrom() {
		return from;
	}

	/**
	 * 受信者を取得する
	 * @return 受信者
	 */
	public Node getTo() {
		return to;
	}
}
