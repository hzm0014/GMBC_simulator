import java.util.HashMap;
import java.util.Map;

import org.graphstream.graph.Node;

/**
 * 各ノードが持つ二重連結成分分解を表す行列
 * 隣接(1hop以内)する2ノード間の距離を表す
 * ノードのID
 */
public class MBC {
	/**
	 * MBCを表すHashMap
	 * keyをノードAのID+ノードBのIDとし，Valueをそのノード間の距離とする
	 * Mapにないデータは接続していないと判断し，極大値(INF)とする
	 */
	private Map<String, Integer> mbc = new HashMap<String, Integer>();

	/**
	 * 極大値
	 */
	private final int INF = 1<<29;

	/**
	 * MBCの値(ノード間の距離)を取得する
	 * @param key ノード間のキー(ノードAのID+ノードBのID)
	 * @return ノード間の距離
	 */
	public int getDist(String key) {
		// キーが登録されていればその値を，されていなければ極大値を返す
		if (mbc.containsKey(key)) {
			return mbc.get(key);
		} else {
			return INF;
		}
	}

	/**
	 * MBCの値(ノード間の距離)を取得する
	 * @param nodeA ノードA
	 * @param nodeB ノードB
	 * @return ノード間の距離
	 */
	public int getDist(Node nodeA, Node nodeB) {
		String key = nodeA.getId() + nodeB.getId();
		return getDist(key);
	}

	/**
	 * MBCの値(ノード間の距離)を追加する
	 * すでに登録されている場合は，小さい方を優先する
	 * @param nodeA ノードA
	 * @param nodeB ノードB
	 * @param dist ノード間の距離
	 */
	public void setDist(Node nodeA, Node nodeB, int dist) {
		String key = nodeA.getId() + nodeB.getId();
		mbc.put(key, Math.min(getDist(nodeA, nodeB), dist));
	}

	/**
	 * 与えられた距離が極大値であるかを判断する
	 * @param dist
	 * @return
	 */
	public boolean isInf(int dist) {
		if (dist == INF) {
			return true;
		} else {
			return false;
		}
	}
}
