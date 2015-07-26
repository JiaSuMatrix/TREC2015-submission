package edu.hit.wilab.trec.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GexfFile {

	public PrintWriter pw = null;
	public int nodeNum = 0;
	public Map<String, Integer> map_node_edge = new HashMap<String, Integer>();

	@SuppressWarnings("unused")
	public void init(String path) {
		File f = new File(path);
		if (f == null) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			FileWriter fw;
			try {
				fw = new FileWriter(f);
				fw.write("");
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		pw.println("<gexf xmlns=\"http://www.gephi.org/gexf\" xmlns:viz=\"http://www.gephi.org/gexf/viz\">");
		pw.println("<graph defaultedgetype=\"undirected\" type=\"static\">");
		pw.println("<attributes class=\"node\" type=\"static\">");
		pw.println("<attribute id=\"0\" title=\"nodeid\" type=\"string\"/>");
		pw.println("</attributes>");
		pw.println("<nodes>");

	}

	public void close() {
		pw.println("</nodes>");
		pw.println("</graph>");
		pw.println("</gexf>");
		pw.close();
	}

	public void printNode(String nodeid) {
		pw.println("<node id=\"" + (nodeNum++) + "\">");
		pw.println("<attvalues>");
		pw.println("<attvalue id=\"0\" value=\"" + nodeid + "\"/>");
		pw.println("</attvalues>");
		pw.println("</node>");
	}

	public void printEdges(List<String[]> edges) {

		int edgeNum = 0;

		pw.println("<edges>");
		for (String[] edge : edges) {

			Integer edgeid0 = map_node_edge.get(edge[0]);
			Integer edgeid1 = map_node_edge.get(edge[1]);

			pw.println("<edge id=\"" + (edgeNum++) + "\" source=\"" + edgeid0
					+ "\" target=\"" + edgeid1 + "\" weight=\"" + edge[2]
					+ "\"/>");
		}
		pw.println("</edges>");
		
	}

	public void doTrain(List<String> list_node, Map<String, Integer> map_edge,
			String writeDatafile) {

		init(writeDatafile);

		int edgeNum = 0;
		for (String node : list_node) {
			map_node_edge.put(node, edgeNum++);
			printNode(node);
		}

		List<String[]> list_edge = new ArrayList<String[]>();

		Set<String> key = map_edge.keySet();
		for (Iterator it = key.iterator(); it.hasNext();) {
			String s = (String) it.next();
			
			if(map_edge.get(s) > Config.edge_num){//设置边权重
				String[] edge = new String[3];
				edge[0] = s.split("-")[0];
				edge[1] = s.split("-")[1];
				edge[2] = String.valueOf(map_edge.get(s));
				list_edge.add(edge);
				// System.out.println(map_edge.get(s));
			}
			
		}

		printEdges(list_edge);

		close();

	}

	public void gexfBuild(List<String> list_node, Map<String, Integer> map_edge, String writeDatafile) {
		GexfFile test = new GexfFile();
		test.doTrain(list_node, map_edge, writeDatafile);
	}

}
