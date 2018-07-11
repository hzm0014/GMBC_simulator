import java.io.IOException;

public class Simulator {
	// プロトコルに関する定数

	private final static int PROTOCOL_NUM = 5;
	/**
	 * プロトコル名もしくは，以下のプロトコルのID
	 * Flooding: 0
	 * FFG: 1
	 * GMBC: 2
	 */
	private final static String[] PROTOCOL_ID = {"FFG", "GMBC", "GMBC", "GMBC", "GMBC"};

	/**
	 * プロトコルでのFanout数
	 */
	private final static int[] FANOUT = {3, 2, 3, 4, 6};

	public static void main(String args[]) throws InterruptedException, IOException {
		for(int i = 0; i < PROTOCOL_NUM; i++) {
			Simulate simu = new SimulateVaryingVsReachability(PROTOCOL_ID[i], FANOUT[i]);
			simu.run();
		}
	}
}