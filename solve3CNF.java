package npc;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class solve3CNF {

	public static void main (String[] args) {
		String filename = args[0];
		List<CNF> cnfs = CNF.makeAllCNFsFromFile(filename);
		System.out.println("* Solve 3CNF in "+filename+": (reduced to K-Clique) *");
		int num = 0;
		for (CNF cnf: cnfs) {
			//CNF cnf = cnfs.get(12);
			long start = System.currentTimeMillis();
			boolean[] assignments = cnf.getSolution();
			System.out.print("3CNF No."+(++num)+"[n="+cnf.getN()+" k="+cnf.getK()+"] ");
			if(assignments != null){
				StringBuilder sb = new StringBuilder();
				sb.append("[ ");
				for(int i =0;i<assignments.length;i++){
					sb.append("A"+(i+1)+"="+(assignments[i]?"T":"F")+" ");
				}
				sb.append("]");
				System.out.print(sb.toString());
			}
			else
				System.out.print("No "+cnf.getK()+"-clique; no solution ");
			System.out.println("("+(System.currentTimeMillis() - start) + " ms)");
		}
		System.out.println("***");
	}
	
}
