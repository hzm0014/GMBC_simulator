/**
 * issue
 * - one node remove and revival in 1 tern
 * - remake random generator to better
 * - use queue in remove and revival
 */

import java.util.ArrayList;
import java.util.Random;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

public class TimeVaryingGraph {
	Graph graph = null;
	Random rnd = new Random();
	double varyingRate;

	ArrayList<Edge> deadingEdges;

	/**
	 * constracta
	 * @param graph
	 */
	TimeVaryingGraph(Graph graph, double varyingRate) {
		this.graph = graph;
		this.varyingRate = varyingRate;

		deadingEdges = new ArrayList<Edge>();
	}

	TimeVaryingGraph(Graph graph) {
		this(graph, 0.5f);
	}

	/**
	 * varying each node with varyingRate
	 */
	void varying() {
		removeEdge();
		revivalEdge();
	}

	/**
	 * remove edges with varyingRate
	 */
	void removeEdge() {
		// temporary holder for  removed edges
		ArrayList<Edge> removeEdges = new ArrayList<Edge>();
		// choise edges with varyingRtae
		for(Edge edge : graph.getEachEdge()) {
			if (rnd.nextDouble() <= varyingRate) {
				removeEdges.add(edge);
			}
		}
		// remove chose edges
		for(Edge edge : removeEdges) {
			graph.removeEdge(edge);
			deadingEdges.add(edge);
		}

	}

	void revivalEdge() {
		ArrayList<Edge> revaivalEdges = new ArrayList<Edge>();

		for(Edge edge : deadingEdges) {
			if(rnd.nextDouble() <= varyingRate) {
				String source = edge.getNode0().getId();
				String target = edge.getNode1().getId();
				graph.addEdge(source + target, source, target);

				revaivalEdges.add(edge);
			}
		}

		for(Edge edge : revaivalEdges) {
			deadingEdges.remove(edge);
		}
	}

}
