package npc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class solveISet {
	
	public static void main (String[] args) {
		String filename = args[0];
		List<Graph> graphs = Graph.makeAllGraphsFromFile(filename);
		System.out.println("* Max Independent Sets in graphs in " + filename);
		System.out.println("\t(|V|,|E|) Independent Set (size, ms used)");
		for (Graph graph: graphs) {
			//Graph graph = graphs.get(6);
			//graph.debug(true);
			String origGraph = graph.toString();
			long start = System.currentTimeMillis();
			Set<Integer> maxClique = graph.invertGraph().findLargestClique();
			System.out.println(
					origGraph + " {" + maxClique.stream().map(v -> v.toString()).collect(Collectors.joining(", ")) +
					"} (size=" + maxClique.size() + ", " + (System.currentTimeMillis() - start) + " ms)"
				);
			//graph.debug();
		}
	}

}
