import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This class represents a graph.
 * Each graph has vertices represented by integers from 0 to numVertices—1, inclusive.
 */
class Graph {
	
	/***** 1. PUBLIC STATIC API *****/
	
	/**
	 * Create all graphs from a file
	 * @param filename
	 * @return An ArrayList of graphs.
	 */
	static List<Graph> makeAllGraphsFromFile (String filename) {
		Scanner file = null;
		try { file = new Scanner(new File(filename)); }
		catch (FileNotFoundException e) {}
		List<Graph> graphs = new ArrayList<Graph>();
		for (int i = 1; ; i++) {
			int order = file.nextInt();
			if (order > 0) graphs.add(new Graph(i, order, file));
			else break;
		}
		return graphs;
	}
	
	
	
	
	
	/***** 2. PUBLIC API *****/
	
	
	
	/*** 2.1 Creation API (creating new graphs) ***/
	
	/**
	 * Create a new, blank graph with a given number of vertices and no edges.
	 * @param numVertices
	 */
	Graph (int numVertices) {
		matrix = new ArrayList<>();
		this.numVertices = numVertices;
	}
	
	/**
	 * Create a new, empty graph.
	 */
	Graph () { this(0); }
	
	
	
	/*** 2.2 Reading API (read information about the graph) ***/
	
	/**
	 * Get all edges, represented as a HashSet. Each entry in the HashSet represents a single edge, using an array of
	 * two integers (the two vertices connected by this edge). Each edge will be ordered with the smaller vertex first.
	 * @return
	 */
	Set<int[]> getEdges () {
		Set<int[]> edges = new HashSet<>();
		for (int v1 = 0; v1 < numVertices; v1++)
			for (int v2 = v1 + 1; v2 < numVertices; v2++)
				if (areConnected(v1, v2))
					edges.add(new int[]{v1, v2});
		return edges;
	}
	
	/**
	 * Get the numVertices of (number of vertices in) this graph.
	 * @return
	 */
	int getNumVertices () { return numVertices; }
	
	/**
	 * Get all vertices in this graph, i.e., creates a set with integers ordered from 0 to numVertices—1.
	 * @return
	 */
	Set<Integer> getVertices () {
		return IntStream
			.range(0,  numVertices)
			.boxed()
			.collect(Collectors.toSet());
	}
	
	
	/**
	 * Get the adjacent vertices of a given vertex.
	 * @param vertex
	 * @return
	 */
	Set<Integer> getAdjacentVertices (int vertex) {
		return getVertices()
			.stream()
			.filter(v -> areConnected(vertex, v))
			.collect(Collectors.toSet());
	}
	
	/**
	 * Generate a string representation of this graph suitable for console display, including the index, number of
	 * vertices, and number of edges of this graph.
	 */
	public String toString () { return toString(false); }
	
	/**
	 * Check whether two vertices are connected.
	 * @param v1
	 * @param v2
	 * @return
	 */
	boolean areConnected (int v1, int v2) { return matrix.get(v1).get(v2); }
	
	/**
	 * Find the largest clique in the graph. Returns a HashSet of integer vertices.
	 * @return
	 */
	Set<Integer> findLargestClique () {
		bronKerbosch(
			set(),
			getVertices(), 
			set()
		);
		return new TreeSet<Integer>(
			maximalCliques
				.stream()
				.reduce(null, (curMaxClique, thisClique) ->
					curMaxClique == null || thisClique.size() > curMaxClique.size() ? thisClique : curMaxClique
				)
		);
	}

	
	
	/*** 2.3 Modification API (modifying the graph)  ***/
	
	/**
	 * Create a connection between two vertices. Returns itself (chainable).
	 * @param v1
	 * @param v2
	 * @return
	 */
	Graph addEdge (int v1, int v2) { return setMatrix(v1, v2, true); }
	
	/**
	 * Append a new vertex to the graph, not connected to any other vertices.
	 * @return The number of the new vertex
	 */
	int appendVertex () {
		addVertex(numVertices);
		return numVertices - 1;
	}
	
	/**
	 * Add a vertex to the graph with a specific number.
	 * IMPORTANT: Note that this adds 1 to the number of all higher-numbered vertices.
	 * @param vertex
	 * @return
	 */
	Graph addVertex (int vertex) {
		numVertices++;
		for (List<Boolean> row: matrix) row.add(vertex, false);
		matrix.add(vertex, new ArrayList<>(Collections.nCopies(numVertices, false)));
		return this;
	}
	
	/**
	 * Remove an edge from the graph.
	 * @param v1
	 * @param v2
	 * @return
	 */
	Graph removeEdge (int v1, int v2) { return setMatrix(v1, v2, false); }
	
	/**
	 * Remove a vertex from the graph.
	 * IMPORTANT: Note that this subtracts 1 from the number of all higher-numbered vertices.
	 * @param vertex
	 * @return
	 */
	Graph removeVertex (int vertex) {
		matrix.remove(vertex);
		for (List<Boolean> row: matrix) row.remove(vertex);
		--numVertices;
		return this;
	}
	
	/**
	 * Print debugging information about the graph, including a URL to visualize it. Omit the parameter to end execution
	 * immediately, or pass `true` to continue execution.
	 * @param noDie
	 */
	void debug (boolean noDie) {
		System.out.println();
		System.out.println("Debugging graph " + this.toString(true) + ":");
		System.out.println("To visualize this graph, copy the following lines and paste on");
		System.out.println("csacademy.com/app/graph_editor");
		System.out.println();
		for (int v1 = 0; v1 < numVertices; v1++)
			System.out.println(v1);
		for (int v1 = 0; v1 < numVertices; v1++)
			for (int v2 = v1 + 1; v2 < numVertices; v2++)
				if (areConnected(v1, v2))
					System.out.println(v1 + " " + v2);
		System.out.println();
		System.out.print("Debugging output complete. ");
		if (noDie) {
			System.out.println("Continuing execution.");
			System.out.println("To terminate execution, remove the parameter to the debug() method.");
			System.out.println();
		}
		else {
			System.out.println("Stopping execution.");
			System.out.println("To continue execution, pass `true` to the debug() method.");
			System.exit(0);
		}
	}
	
	void debug () { debug(false); }
	
	
	
	
	
	/***** 3. PRIVATE INSTANCE FIELDS *****/
	
	/**
	 * A number >= 1 representing the index of this graph.
	 */
	private int index;
	
	/**
	 * The number of vertices that this graph contains.
	 */
	private int numVertices;
	 
	/**
	 * A (numVertices * numVertices) 2D array representing an adjacency matrix storing pairs of vertices that are
	 * connected. It is symmetric (i.e. matrix[A][B] == matrix[B][A]), and vertices are NOT marked as being connected to
	 * themselves (i.e. matrix[A][A] == false).
	 */
	private List<List<Boolean>> matrix;
	
	/**
	 * Used to accumulate the maximal cliques found by the Bron-Kerbosch algorithm as it recurses. After the algorithm
	 * completes, the largest clique in this set is reported as the maximum clique. Note that this set contains
	 * maxiMAL cliques, which are not all necessarily maxiMUM cliques.
	 */
	private Set<Set<Integer>> maximalCliques = new HashSet<>();
	
	
	
	
	
	/***** 4. PRIVATE API *****/
	
	
	
	/*** 4.1 Private graph methods ***/
	
	/**
	 * Create a new graph using a file reference.
	 * @param index
	 * @param numVertices
	 * @param file
	 */
	private Graph (int index, int numVertices, Scanner file) {
		this(numVertices);
		this.index = index;
		for (int v1 = 0; v1 < numVertices; v1++) {
			matrix.add(new ArrayList<>());
			for (int v2 = 0; v2 < numVertices; v2++)
				matrix.get(v1).add(file.nextInt() == 1 && v1 != v2);
		}
	}
	
	/**
	 * Recursor for the Bron-Kerbosch clique-finder algorithm.
	 * @param R
	 * @param P
	 * @param X
	 */
	private void bronKerbosch (Set<Integer> R, Set<Integer> P, Set<Integer> X) {
		if (P.isEmpty() && X.isEmpty()) maximalCliques.add(R);
		for (Iterator<Integer> PIter = P.iterator(); PIter.hasNext();) {
			int v = PIter.next();
			Set<Integer>
				thisR = clone(R),
				neighborhood = getAdjacentVertices(v);
			thisR.add(v);
			bronKerbosch(thisR, intersect(P, neighborhood), intersect(X, neighborhood));
			PIter.remove();
			X.add(v);
		}
	}
	
	/**
	 * Set two symmetric cells in the matrix to be either true or false.
	 * @param v1
	 * @param v2
	 * @param connected
	 * @return
	 */
	private Graph setMatrix (int v1, int v2, boolean connected) {
		this.matrix.get(v1).set(v2, connected);
		this.matrix.get(v2).set(v1, connected);
		return this;
	}
	
	/*** 4.2 Private helper methods ***/
	
	/**
	 * Create a new EMPTY Integer HashSet.
	 * @return
	 */
	private Set<Integer> set () { return new HashSet<>(); }
	
	/**
	 * Clone an Integer HashSet.
	 * @param c
	 * @return
	 */
	private Set<Integer> clone (Collection<Integer> c) { return new HashSet<>(c); }
	
	/**
	 * Find the intersection of two Integer HashSets.
	 * @param s1
	 * @param s2
	 * @return
	 */
	private Set<Integer> intersect (Set<Integer> s1, Set<Integer> s2) {
		Set<Integer> intersection = clone(s1);
		intersection.retainAll(s2);
		return intersection;
	}
	
	/**
	 * Generate a string representation of the graph
	 * @param withLabels
	 * @return
	 */
	private String toString (boolean withLabels) {
		return
			"G" + index + " ( " + numVertices + (withLabels ? " vertices" : "") + ", " + getEdges().size() +
			(withLabels ? " edges" : "") + ")";
	}
}
