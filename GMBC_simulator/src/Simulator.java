import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;

public class Simulator {

	private static Graph graph = null;

	private static float varyingRate = 0.5f;


	public static void main(String args[]) throws InterruptedException {
		// generate graph
		int graphId = 0;
		RandomGeometricGraphGenerator generator = new RandomGeometricGraphGenerator();
		graph = generator.generate(graphId++ + "");

		// display
	    graph.display(false);

	    // time verying test
	    TimeVaryingGraph tvg = new TimeVaryingGraph(graph, varyingRate);
	    for(;;) {
	    		TimeUnit.SECONDS.sleep(1);
	    		tvg.run();
	    }
    	}
}