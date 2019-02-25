import java.io.IOException;

public class Simulator {
	// プロトコルに関する定数

	private final static int PROTOCOL_NUM = 2;
	/**
	 * プロトコル名もしくは，以下のプロトコルのID
	 * Flooding: 0
	 * FFG: 1
	 * GMBC: 2
	 */
	private final static String[] PROTOCOL_ID = {"GMBC", "FFG"};

	/**
	 * プロトコルでのFanout数
	 */
	private final static int[] FANOUT = {4, 4};

	public static void main(String args[]) throws InterruptedException, IOException {
		// グラフの偏りの強さと到達率のシミュレーション
		/*
		for(int i = 0; i < PROTOCOL_NUM; i++) {
			Simulate simu = new SimulateBiasedVaryingVsReachability(PROTOCOL_ID[i], FANOUT[i]);
			simu.run();
		}
		*/

		// グラフの変化率(varying)と到達率のシミュレーション
		/*
		for(int i = 0; i < PROTOCOL_NUM; i++) {
			Simulate simu = new SimulateVaryingVsReachability(PROTOCOL_ID[i], FANOUT[i]);
			simu.run();
		}
		*/

		// グラフの変化率(churn)と到達率のシミュレーション
		for(int i = 0; i < PROTOCOL_NUM; i++) {
			Simulate simu = new SimulateChurnVsReachability(PROTOCOL_ID[i], FANOUT[i]);
			simu.run();
		}

		// MBCの更新頻度と到達率のシミュレーション
		/*
		for (int i = 0; i < PROTOCOL_NUM; i++) {
			Simulate simu = new SimulateUpdateMbcVsReachiability(PROTOCOL_ID[i], FANOUT[i]);
			simu.run();
		}
		*/
	}
}