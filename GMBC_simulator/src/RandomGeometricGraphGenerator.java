/**
 * issue
 * - set radius.
 * - set nodeNum.
 * - caluclate effected radius or nodeNum in algorirhm.
 * - generate graph baised node of point.
 */

// memo: Toolkit kit new
// kit.isConnected(); 連結しているかどうかを判断

import java.util.Random;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

public class RandomGeometricGraphGenerator {
	/**
	 * graph
	 */
	static Graph graph = null;

	Random rnd;

	int nodeCount;

	double radius;

	int xRange = 100;
	int yRange = 100;

	/**
	 * constracta
	 */
	RandomGeometricGraphGenerator(int nodeCount, double radius) {
		this.nodeCount = nodeCount;
		this.radius = radius;

		rnd = new Random();
	}

	RandomGeometricGraphGenerator() {
		this(1000, 5);
	}

	/**
	 * Generate Random Geometric Graph
	 * @param arg
	 * @return
	 */
	Graph generate(String arg) {
		graph = new SingleGraph("RGG: " + arg);

		for(int i = 0; i < nodeCount; i++) {
			// add node
			Node addedNode = addNode(i + "");
			// add edge, if distance smaller than radius
			for (Node n : graph.getEachNode()) {
				if (n != addedNode && isConnect(addedNode, n, radius)) {
					graph.addEdge(addedNode.getId() + n.getId() + "", addedNode.getId() + "", n.getId() + "");
				}
			}
		}

		/*
		Generator gen = new RandomEuclideanGenerator();
	    gen.addSink(graph);
	    gen.begin();
	    for(int i=0; i<1000; i++) {
	            gen.nextEvents();
	    }
	    gen.end();
		*/

		return graph;
	}

	/**
	 * add node with point x, y in graph
	 * @param args
	 * @param x
	 * @param y
	 */
	Node addNode(String arg, int x, int y) {
		Node node = graph.addNode(arg);
		node.addAttribute("x", x);
		node.addAttribute("y", y);
		return node;
	}

	Node addNode(String arg) {
		return addNode(arg, rnd.nextInt(100), rnd.nextInt(100));
	}

	boolean isConnect(Node node0, Node node1, double r) {
		int x0 = (int) node0.getNumber("x");
		int y0 = (int) node0.getNumber("y");
		int x1 = (int) node1.getNumber("x");
		int y1 = (int) node1.getNumber("y");
		return r * r >= ((x0 -x1) * (x0 - x1)) + ((y0 - y1) * (y0 - y1));
	}
}
