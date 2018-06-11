import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;

public class Simulator {

	static Graph graph = null;


	public static void main(String args[]) throws InterruptedException {
		// generate graph
		int graphId = 0;
		RandomGeometricGraphGenerator generator = new RandomGeometricGraphGenerator();
		graph = generator.generate(graphId++ + "");

		// display
	    graph.display(false);

	    // time verying test
	    TimeVaryingGraph tvg = new TimeVaryingGraph(graph);
	    for(;;) {
	    		TimeUnit.SECONDS.sleep(1);
	    		tvg.varying();
	    }
    	}
}