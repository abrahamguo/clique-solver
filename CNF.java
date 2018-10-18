package npc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CNF {
	
	//number of variables
	private int vCount;
	//the key is the boolean value, the value is an array of verexes on the graph that have that value  
	private HashMap<Integer, ArrayList<Integer>> map = new HashMap<>(); 
	//for holding the values
	private ArrayList<Integer> expressions = new ArrayList<>();
	private Graph cliqueGraph;
	private int k;
	
	CNF(String line){
		String[] cnf3 = line.split(" ");
		vCount = Integer.parseInt(cnf3[0]);
		for(int i = 1; i<cnf3.length;i++){
			expressions.add(Integer.parseInt(cnf3[i]));
		}
		k = expressions.size() / 3;
	}
	
	public int getN(){
		return vCount;
	}
	
	public int getK(){
		return k;
	}
	
	static List<CNF> makeAllCNFsFromFile (String filename) {	
		List<CNF> cnfs = new ArrayList<CNF>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
		    while ((line = br.readLine()) != null) {
		       cnfs.add(new CNF(line));
		    }
		}
		catch (Exception e) {}	    
		return cnfs;
	}
	
	public Graph buildCliqueGraph(){
		cliqueGraph = new Graph();
		//build vertexes and table of vertexes to corresponding boolean values.
		for(int n : expressions ){
			if(!map.containsKey(n))
				map.put(n, new ArrayList<Integer>());
			int value = cliqueGraph.appendVertex();
			map.get(n).add(value);	
		}
		
		//build edges.
		int vertex=0;
		for(int n : expressions){
			//get array of non vertexes.
			ArrayList<Integer> nonEdges = map.containsKey(n*-1) ? new ArrayList<Integer>(map.get(n*-1)) : new ArrayList<Integer>();
			
			//add neighbors(and self) in 3 bool expression
			addNeighbors(nonEdges,vertex);
			
			//make a connection from this vertex to every vertex
			for(int i=0; i<cliqueGraph.getNumVertices(); i++){
				if(!nonEdges.contains(i)){
					cliqueGraph.addEdge(vertex,i);
				}
			}
			vertex++;
		}
		//cliqueGraph.debug(true);
		return cliqueGraph;
	}
	
	public boolean[] getSolution(){
		Graph graph = this.buildCliqueGraph();
		System.out.println("Graph start");
		Set<Integer> maxClique = graph.findLargestClique();
		System.out.println("Graph's done");
		if(maxClique.size() >= k){
			//get values for all n in solution
			Set<Integer> solution = new HashSet<Integer>();
			for(int vertex : maxClique){
				for(Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()){
					if(entry.getValue().contains(vertex)){
						solution.add(entry.getKey());
						break;
					}
				}
			}
			//assign the value true or false to each variable. Note, not all n may be in solution, thus we set default false
			boolean[] assignments = new boolean[vCount];
			for(int i = 0; i<assignments.length;i++){
				assignments[i] = solution.contains(i+1); //since default is false, we only need to check if the true value is in there.
			}
			return assignments;
		}
		else{
			//System.out.println(k + " " + maxClique.size());
			return null;
		}
	}
	
	private void addNeighbors(ArrayList<Integer> nonEdges, int vertex){
		int start = (vertex - (vertex % 3)); // find the starting vertex of the 3 group expression
		for(int i=0;i<3;i++){
			nonEdges.add(start + i);
		}
	}

	
}
