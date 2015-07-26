package edu.hit.wilab.trec.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.hit.wilab.trec.index.SearchIndex;
import edu.hit.wilab.trec.index.indri.SearchIndex_Indri;

public class MiningNode2 {

	public List<Integer> list_sort_community = new ArrayList<Integer>();

	private static Logger logger = Logger.getLogger(MiningNode2.class);

	public List<String> miningNode() {

		logger.info("搜索潜在节点...");
		// 与潜在节点相连的节点集合
		Map<String, Set<String>> map_connectNode = new HashMap<String, Set<String>>();

		// 遍历医疗词的map
		Set<String> key = BuildNetwork.map_word.keySet();
		for (Iterator it = key.iterator(); it.hasNext();) {

			String[] word = new String[2];
			word[0] = (String) it.next();// 搜索词
			word[1] = "1";// 权重

			List<String> list_connectNode = BuildNetwork.map_word.get(word[0]);

			// 可做搜索词的限制，当包含该词的文章数大于一定值时

			// 检索相关论文
			List<String[]> list_article = new ArrayList<String[]>();

			if (Config.index_type.equals("0")) {
				SearchIndex si = new SearchIndex();
				// 做搜索结果数量的限制
				list_article = si.searchIndex_word(word, Config.fields_final,
						Config.search_final);
			} else {
				SearchIndex_Indri sii = new SearchIndex_Indri();
				// 做搜索结果数量的限制
				list_article = sii.searchIndex_word(word, Config.fields_final,
						Config.search_final);
			}
			// 设置list_article的数量阈值，同一个词，出现过多的文章，跳过

			if (list_article == null) {
				continue;
			}
			// 遍历搜索文章结果，计算每篇文章结果中的医疗关键词
			// map_article_word<文章ID，0：关键词与搜索得分；1：与该文章相连的文章集合>
			for (String[] article : list_article) {
				if(list_connectNode.contains(article[0])){
					continue;
				}

				if (map_connectNode.containsKey(article[0])) {

					Set<String> set_connectNode = map_connectNode
							.get(article[0]);

					set_connectNode.addAll(list_connectNode);

					map_connectNode.put(article[0], set_connectNode);
				} else {

					Set<String> set_connectNode = new HashSet<String>();

					set_connectNode.addAll(list_connectNode);

					map_connectNode.put(article[0], set_connectNode);
				}

			}

		}

		logger.info("搜索潜在节点完成!");

		logger.info("过滤潜在节点...");

		List<String> list_final = new ArrayList<String>();

		Set<String> key_connectNode = map_connectNode.keySet();
		for (Iterator it_connectNode = key_connectNode.iterator(); it_connectNode
				.hasNext();) {

			String nodeID = (String) it_connectNode.next();// 文章ID

			Set<String> connectNodes = map_connectNode.get(nodeID);

			// 计算连接节点所属的团体
			Map<Integer, Integer> map_num_com = new HashMap<Integer, Integer>();

			for (String connectNode : connectNodes) {

				for (int i = 0; i < Community.list_community.size(); i++) {
					if (Community.list_community.get(i).contains(connectNode)) {
						if (map_num_com.containsKey(i)) {
							map_num_com.put(i, map_num_com.get(i) + 1);
						} else {
							map_num_com.put(i, 1);
						}
					}
				}

			}

			String print_text = new String();
			Set<Integer> key_map_num_com = map_num_com.keySet();
			for (Iterator it_map_num_com = key_map_num_com.iterator(); it_map_num_com
					.hasNext();) {
				Integer s_map_num_com = (Integer) it_map_num_com.next();
				print_text += s_map_num_com + "::"
						+ map_num_com.get(s_map_num_com) + "-";
			}
			logger.info(print_text);

			double iscontain_score = 0.0;
			if (map_num_com.containsKey(Community.important_community)) {
				iscontain_score = (double)map_num_com.get(Community.important_community) / (double)connectNodes.size();
			}
			
			logger.info(nodeID + "::" + iscontain_score);
			
			if (iscontain_score > Config.edge_value && Config.edge_node_max > connectNodes.size() && connectNodes.size() > Config.edge_node_min) {
				list_final.add(nodeID);
			}

		}

		logger.info("过滤潜在节点完成!");

		logger.info("隐藏节点加入!");

		List<String> list_community_sub = Community.list_community
				.get(Community.important_community);

		for (String final_node : list_final) {

			BuildNetwork.list_node.add(final_node);

			Set<String> set_connectNode = map_connectNode.get(final_node);
			double total_score = 0.0;
			for (String connectNode : set_connectNode) {
				total_score += BuildNetwork.map_score.get(connectNode);
				BuildNetwork.map_edge.put(connectNode + "-" + final_node, 1);
			}
			BuildNetwork.map_score.put(final_node, total_score
					/ set_connectNode.size());

			list_community_sub.add(final_node);

		}

		Community.list_community.set(Community.important_community,
				list_community_sub);

		return list_final;

	}

	// public boolean calculate2(Map<String, Integer> map_article, String
	// nodeid) {
	//
	// boolean is_in = false;
	//
	// logger.info("开始判断隐藏节点是否满足条件:" + nodeid);
	//
	// int edge_connect = map_article.size();// 共连接多少个节点
	//
	// logger.info("共连接节点数:" + edge_connect);
	//
	// if (edge_connect <= 60) {
	//
	// int edge_num_in = 0;// 连接节点之间的边数
	//
	// double total_aver = 0.0;// 连接节点的Lucene值之和
	//
	// int community_count = 0;// 连接最大团体中节点的个数
	// boolean iscontain = false;// 是否包含0.2以上的节点
	// double value_community = 0.0;
	// double value_all = 0.0;
	//
	// Set<String> key = map_article.keySet();
	// for (Iterator it = key.iterator(); it.hasNext();) {
	// String s = (String) it.next();
	//
	// int connect_num = map_article.get(s);// 连接权重
	//
	// for (int i = 0; i < Community.list_community.size(); i++) {
	// if (Community.list_community.get(i).contains(s)) {
	//
	// double score = BuildNetwork.map_score.get(s);
	//
	// total_aver += score;
	//
	// logger.info(i + "::" + s + "::" + score + "::"
	// + connect_num);
	//
	// if (i == Community.max_num) {
	// if (score >= 0.2) {
	// iscontain = true;
	// }
	// community_count++;
	// }
	//
	// if (score >= 0.1) {
	// value_all += Double.parseDouble(String.valueOf(
	// score).substring(0, 3))
	// * connect_num;
	// if (i == Community.max_num) {
	// value_community += Double.parseDouble(String
	// .valueOf(score).substring(0, 3))
	// * connect_num;
	// }
	// }
	//
	// break;
	// }
	// }
	//
	// // if(connect_num > Config.edge_num){
	// //
	// // total_aver += BuildNetwork.map_score.get(s);
	// //
	// // Set<String> key2 = map_article.keySet();
	// // for (Iterator it2 = key2.iterator(); it2.hasNext();) {
	// // String s2 = (String) it2.next();
	// //
	// // int connect_num2 = map_article.get(s2);
	// //
	// // if(connect_num2 > Config.edge_num){
	// // if(BuildNetwork.map_edge.containsKey(s + "-" + s2)){
	// // int connect_num_in = BuildNetwork.map_edge.get(s + "-" + s2);
	// // if(connect_num_in > Config.edge_num){
	// // edge_num_in++;
	// // }
	// // }
	// // }
	// //
	// // }
	// //
	// // }
	//
	// }
	//
	// if (value_all / value_community <= 3) {
	// logger.info("node::" + nodeid + "::" + "value::" + value_all
	// / value_community);
	// if (community_count > 2) {
	// if (iscontain) {
	// logger.info("隐藏节点加入!");
	//
	// BuildNetwork.list_node.add(nodeid);
	// BuildNetwork.map_score.put(nodeid, total_aver
	// / edge_connect);
	//
	// Set<String> key_edge = map_article.keySet();
	// for (Iterator it = key_edge.iterator(); it.hasNext();) {
	// String s_node = (String) it.next();
	//
	// int connect_num = map_article.get(s_node);// 连接权重
	//
	// if (connect_num > Config.edge_num) {
	// BuildNetwork.map_edge.put(
	// s_node + "-" + nodeid, connect_num);
	// }
	//
	// }
	//
	// is_in = true;
	// }
	// } else {
	// logger.info("隐藏节点加入!");
	//
	// BuildNetwork.list_node.add(nodeid);
	// BuildNetwork.map_score.put(nodeid, total_aver
	// / edge_connect);
	//
	// Set<String> key_edge = map_article.keySet();
	// for (Iterator it = key_edge.iterator(); it.hasNext();) {
	// String s_node = (String) it.next();
	//
	// int connect_num = map_article.get(s_node);// 连接权重
	//
	// if (connect_num > Config.edge_num) {
	// BuildNetwork.map_edge.put(s_node + "-" + nodeid,
	// connect_num);
	// }
	//
	// }
	//
	// is_in = true;
	// }
	//
	// }
	//
	// // double total = edge_connect*(edge_connect-1)/2;
	// //
	// // double coeffient = edge_num_in/total;
	// //
	// logger.info("coeffient::"+coeffient+"::edge_num_in::"+edge_num_in+"::total::"+total);
	// // if(coeffient > Config.coeffient){
	// //
	// // logger.info("隐藏节点加入!");
	// //
	// // BuildNetwork.list_node.add(nodeid);
	// // BuildNetwork.map_score.put(nodeid, coeffient * total_aver
	// // / edge_connect);
	// //
	// // Set<String> key_edge = map_article.keySet();
	// // for (Iterator it = key_edge.iterator(); it.hasNext();) {
	// // String s = (String) it.next();
	// //
	// // int connect_num = map_article.get(s);//连接权重
	// //
	// // if(connect_num > Config.edge_num){
	// // BuildNetwork.map_edge.put(s + "-" + nodeid, connect_num);
	// // }
	// //
	// // }
	// //
	// // }
	//
	// }
	//
	// return is_in;
	//
	// }

	// public void calculate(Set<String> set_article, String nodeid) {
	//
	// if (set_article.size() > Config.connect_num) {// 连接团体内的节点数设定
	//
	// logger.info("开始判断隐藏节点是否满足条件:" + nodeid);
	//
	// logger.info("判断所属团体...");
	//
	// // 寻找节点所属团体
	// int maxWordCount = 0;
	// int communityNum = 0;
	// // 所属团体中节点列表
	// List<String> list_community_final = new ArrayList<String>();
	// int i = 0;
	// for (List<String> list_community : Community.list_community) {
	// int wordCount = 0;
	// List<String> community_article = new ArrayList<String>();
	// for (String article : set_article) {
	// if (list_community.contains(article)) {
	// community_article.add(article);
	// wordCount++;
	// }
	// }
	// if (wordCount > maxWordCount) {
	// list_community_final = community_article;
	// maxWordCount = wordCount;
	// communityNum = i;
	// }
	// i++;
	// }
	//
	// logger.info("计算团体内产生的边数...");
	//
	// // 计算团体内部实际产生的边
	// int totaledge = 0;
	//
	// for (String article1 : list_community_final) {
	//
	// for (String article2 : list_community_final) {
	//
	// if (BuildNetwork.map_edge.containsKey(article1 + "-"
	// + article2)) {
	// totaledge++;
	// }
	//
	// }
	//
	// }
	//
	// logger.info("计算节点聚集系数...");
	//
	// int nodeCount = list_community_final.size();
	// logger.info("节点的连接边数:" + nodeCount);
	// if (nodeCount > Config.connect_num) {// 连接团体内的节点数设定
	// int total = nodeCount * (nodeCount - 1) / 2;
	//
	// double cc = totaledge / total;
	//
	// logger.info("cc::" + cc);
	// if (cc > Config.coeffient) {// 需要修改阈值
	//
	// logger.info("隐藏节点加入团体!");
	//
	// List<String> list_community_in = Community.list_community
	// .get(communityNum);
	// list_community_in.add(nodeid);
	// Community.list_community.set(communityNum,
	// list_community_in);// 将符合阈值的节点加入到相应团体中
	//
	// double total_aver = 0.0;
	//
	// for (String community_final : list_community_final) {
	// total_aver += BuildNetwork.map_score
	// .get(community_final);
	// }
	//
	// BuildNetwork.list_node.add(nodeid);
	// BuildNetwork.map_score.put(nodeid, cc * total_aver
	// / nodeCount);
	//
	// for (String nodesource : list_community_final) {
	// BuildNetwork.map_edge.put(nodesource + "-" + nodeid, 1);
	// }
	//
	// }
	// }
	//
	// }
	//
	// }

}
