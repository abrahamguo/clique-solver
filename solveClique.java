import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class solveClique {

	public static void main (String[] args) {
		String filename = args[0];
		List<Graph> graphs = Graph.makeAllGraphsFromFile(filename);
		System.out.println("* Max Cliques in graphs in " + filename);
		System.out.println("\t(|V|,|E|) Cliques (size, ms used)");
		//for (Graph graph: graphs) {
			Graph graph = graphs.get(2);
			long start = System.currentTimeMillis();
			Set<Integer> maxClique = graph.findLargestClique();
			System.out.println(
				graph + " {" + maxClique.stream().map(v -> v.toString()).collect(Collectors.joining(", ")) +
				"} (size=" + maxClique.size() + ", " + (System.currentTimeMillis() - start) + " ms)"
			);
			graph.debug();
		//}
		System.out.println("***");
	}
	
}
