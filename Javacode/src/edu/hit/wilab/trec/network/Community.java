package edu.hit.wilab.trec.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.Modularity;
import org.gephi.statistics.plugin.PageRank;
import org.openide.util.Lookup;

public class Community {

	public static List<List<String>> list_community = new ArrayList<List<String>>();
	public static Map<String, Integer> map_degree = new HashMap<String, Integer>();
	public static HashMap<String, Double> map_pagerank = new HashMap<String, Double>();
	
	public static int important_community = 0;
	
	private static Logger logger = Logger.getLogger(Community.class);
	
	public static void main(String[] args) {

		Community c = new Community();
		c.divide("./network/article.gexf");

	}

	public void divide(String filePath) {

		list_community = new ArrayList<List<String>>();

		// 初始化一个工程和工作区间
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// 将各种模型和控制器导入到工作区间中
		AttributeModel attributeModel = Lookup.getDefault()
				.lookup(AttributeController.class).getModel();// 导入属性模型
		GraphModel graphModel = Lookup.getDefault()
				.lookup(GraphController.class).getModel();// 导入图模型
		ImportController importController = Lookup.getDefault().lookup(
				ImportController.class);// 导入导入控制器
		PartitionController partitionController = Lookup.getDefault().lookup(
				PartitionController.class);// 导入分割控制器

		// 导入文件
		Container container = null;
		try {
			File file = new File(filePath);
			container = importController.importFile(file);
			container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); // Force
		} catch (Exception ex) {
			System.err.println(ex);
		}

		// 将数据加入到图形API
		importController.process(container, new DefaultProcessor(), workspace);

		// See visible graph stats
		UndirectedGraph graph = graphModel.getUndirectedGraphVisible();

		Modularity modularity = new Modularity();
		modularity.execute(graphModel, attributeModel);

		// 利用模块度进行团体划分
		AttributeColumn modColumn = attributeModel.getNodeTable().getColumn(
				Modularity.MODULARITY_CLASS);
		Partition partition = partitionController.buildPartition(modColumn,
				graph);
		
		int i = 0 ;
		double total_score = 0.0;
		
		for (Part<Node> part : partition.getParts()) {
			double total_score_temp = 0.0;
			List<String> list_Nodes = new ArrayList<String>();

			for (Node node : part.getObjects()) {
				
				String nodeid = String.valueOf(node.getNodeData()
						.getAttributes().getValue("nodeid"));
				
				list_Nodes.add(nodeid);
				
				if(BuildNetwork.list_node_sort100.contains(nodeid)){
					total_score_temp += BuildNetwork.map_score.get(nodeid);
				}
				
			}
			list_community.add(list_Nodes);
			
			if(total_score < total_score_temp){
				important_community = i;
				total_score = total_score_temp;
			}
			
			i++;
		}
		
		logger.info(partition.getPartsCount() + " partitions found");
		logger.info(important_community + " is important partition");
	}
	
	
	public void degree(String filePath) {

		map_degree = new HashMap<String, Integer>();
		map_pagerank = new HashMap<String, Double>();
		
		// 初始化一个工程和工作区间
		ProjectController pc = Lookup.getDefault().lookup(
				ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// 将各种模型和控制器导入到工作区间中
		AttributeModel attributeModel = Lookup.getDefault()
				.lookup(AttributeController.class).getModel();// 导入属性模型
		GraphModel graphModel = Lookup.getDefault()
				.lookup(GraphController.class).getModel();// 导入图模型
		ImportController importController = Lookup.getDefault().lookup(
				ImportController.class);// 导入导入控制器

		// 导入文件
		Container container = null;
		try {
			File file = new File(filePath);
			container = importController.importFile(file);
			container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED); // Force
		} catch (Exception ex) {
			System.err.println(ex);
		}

		// 将数据加入到图形API
		importController.process(container, new DefaultProcessor(), workspace);

		// See visible graph stats
		UndirectedGraph graph = graphModel.getUndirectedGraphVisible();

		Degree degree = new Degree();
		degree.execute(graphModel, attributeModel);
		
		attributeModel.getNodeTable().getColumn(Degree.DEGREE);
		
		Modularity modularity = new Modularity();
		modularity.execute(graphModel, attributeModel);
		
		PageRank pr = new PageRank();
		pr.execute(graphModel, attributeModel);

		// 统计节点度
		for (Node n : graph.getNodes()) {
			String nodeid = (String) n.getNodeData().getAttributes()
					.getValue("nodeid");
			Integer degree_node = (Integer) n.getNodeData().getAttributes()
					.getValue("Degree");
			Double pagerank_node = (Double) n.getNodeData().getAttributes()
					.getValue("PageRank");
			map_degree.put(nodeid, degree_node);
			map_pagerank.put(nodeid, pagerank_node);
		}

	}

}
