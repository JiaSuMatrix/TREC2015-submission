package edu.hit.wilab.trec.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.hit.wilab.trec.index.File_Data;
import edu.hit.wilab.trec.index.SearchIndex;
import edu.hit.wilab.trec.index.Test;
import edu.hit.wilab.trec.index.indri.SearchIndex_Indri;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class BuildNetwork {

	// 词1：文章1；文章2；文章6...
	public static Map<String, List<String>> map_word = new HashMap<String, List<String>>();
	public static List<String> list_word_MeSH = new ArrayList<String>();
	// 点信息
	public static List<String> list_node_sort100 = new ArrayList<String>();
	public static List<String> list_node = new ArrayList<String>();
	// 点Lucene索引值
	public static Map<String, Double> map_score = new HashMap<String, Double>();
	// 边信息(pmcid1-pmcid2,weight)
	public static Map<String, Integer> map_edge = new HashMap<String, Integer>();

	private static Logger logger = Logger.getLogger(BuildNetwork.class);

	public static void main(String[] args) {

		// 载入Log日志

		Log.loadLogProperties();

		logger.info("日志系统加载成功！");

		// 载入配置文件

		Config config = new Config();

		config.loadConf();

		logger.info("配置文件加载成功！");

		// 构建查询集，带权值

		logger.info("加载查询集...");

		String[] topic_type = Config.type_diag;
		
		GenerationQuery gq = new GenerationQuery();
		List<List<String[]>> sentenceList = new ArrayList<List<String[]>>();
		if(Config.index_type.equals("0")){
			sentenceList = gq.query_non(Config.topic_num,
					topic_type);
		}else{
			sentenceList = gq.query_indri(Config.topic_num,
					topic_type);
		}

		logger.info("加载查询集完成!");

		logger.info("加载MeSH词库...");

		list_word_MeSH = File_Data.read_list(Config.wordFilePath);

		logger.info("MeSH词库加载完成!");

		logger.info("开始构建词语网...");

		// 构建文章网络list_node,map_edge
		BuildNetwork bn = new BuildNetwork();

		bn.process(sentenceList);

		logger.info("词语网构建完成！节点数:" + list_node.size() + ";边数:"
				+ map_edge.size());

		logger.info("开始生成gexf文件...");

		// 生成gexf文件./network/article.gexf
		GexfFile gf = new GexfFile();
		String writeDatafile = "./network/article.gexf";
		gf.gexfBuild(list_node, map_edge, writeDatafile);

		logger.info("gephi文件构建完成！");

		logger.info("团体划分开始...");

		// 团体划分List<List<String>> list_community
		Community c = new Community();
		c.divide(writeDatafile);

		logger.info("团体划分结束！");

		logger.info("挖掘潜在节点...");

		// 挖掘潜在节点
		MiningNode2 miningNode = new MiningNode2();
		List<String> list_mining = miningNode.miningNode();

		logger.info("挖掘潜在节点完成!共" + list_mining.size() + "个节点!");
		
		List<String> list_id = Test.read_list("./Trec_wordsets/qrels2014.txt",Config.topic_num.split("_")[1]);
		logger.info("正确答案共有"+list_id.size()+"个");
		int findNum = 0;
		for(String mining : list_mining){
			if(list_id.contains(mining)){
				logger.info(mining);
				findNum++;
			}
		}
		logger.info("挖掘节点中共有"+findNum+"个节点正确！");
		
		int result_num = 0;

		Map<String, Double> resultMap = MapSort.sortMapByValue(map_score); // 按Value进行排序
		for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
//			logger.info(entry.getKey() + "---" + entry.getValue());
			if(list_id.contains(entry.getKey())){
				result_num++;
				logger.info(entry.getKey());
			}
		}

		logger.info("Lucene挖掘到"+result_num+"个节点正确！");

		// logger.info("共挖掘节点数为:"+list_mining.size());
		//
		// logger.info("挖掘潜在节点完成！共有节点数:" + list_node.size() + ";边数:"
		// + map_edge.size());

		// logger.info("重新生成gexf文件...");
		//
		// // 加入潜在节点后构建gexf文件
		// String writeDatafile2 = "./network/article_all.gexf";
		// gf.gexfBuild(list_node, map_edge, writeDatafile2);
		//
		// logger.info("gephi文件构建完成！");
		//
		// logger.info("重新团体划分开始...");
		//
		// // 团体划分List<List<String>> list_community
		// Community c2 = new Community();
		// c2.divide(writeDatafile2);
		//
		// logger.info("重新团体划分结束！");
		//
		// logger.info("计算节点的度与PR值...");

		// // 计算节点度
		// c.degree(writeDatafile2);
		//
		// logger.info("计算节点度与PR值完成!");
		//
		// logger.info("计算团体的IF值...");
		//
		// // 计算团体IF值
		// CommunityIF communityIf = new CommunityIF("Test");
		// communityIf.calculateIF(tagger);
		//
		// logger.info("团体的IF值计算完成!");
		//
		// logger.info("最终分值生成...");
		//
		// // 遍历list_community,计算每个节点的最终分值
		// Map<String, Double> map_finalscore = new HashMap<String, Double>();
		//
		// for (int i = 0; i < Community.list_community.size(); i++) {
		// List<String> list_subcom = Community.list_community.get(i);
		// for (int j = 0; j < list_subcom.size(); j++) {
		// String nodeid = list_subcom.get(j);
		// double score = CommunityIF.list_IF.get(i)
		// * Community.map_pagerank.get(nodeid)
		// + map_score.get(nodeid);
		// map_finalscore.put(nodeid, score);
		// }
		//
		// }
		//
		// List<Object[]> list_result =
		// SortByValue.sortMapByValue(map_finalscore);
		//
		// for (Object[] result : list_result) {
		// logger.info(result[0] + "::" + result[1]);
		// }

	}

	public void process(List<List<String[]>> sentenceList) {
		// 检索相关论文

		List<String[]> list_article = new ArrayList<String[]>();

		if (Config.index_type.equals("0")) {
			SearchIndex si = new SearchIndex();

			list_article = si.searchIndex(sentenceList, Config.fields_init,
					Config.search_init);
		} else {
			SearchIndex_Indri sii = new SearchIndex_Indri();

			list_article = sii.searchIndex(sentenceList, Config.fields_init,
					Config.search_init);
		}

		logger.info("最终搜索到结果数为::" + list_article.size());

		for (String[] article : list_article) {

			String content_all = article[1] + " " + article[2] + " "
					+ article[3] + " " + article[6];

			buildNetworkbyWord(content_all, article);

		}

		list_node_sort100 = list_node.subList(0, 100);

	}

	public void buildNetworkbyWord(String content_all, String[] article_node) {
		logger.info("正在加入节点" + article_node[0]);
		List<String> list_word = stanfordByword(content_all);

		if (list_word != null) {
			for (String word : list_word) {
				if (map_word.containsKey(word)) {
					List<String> list_article = map_word.get(word);
					for (String article : list_article) {
						if (article.equals(article_node[0])) {
							continue;
						}
						if (!map_edge.containsKey(article_node[0] + "-"
								+ article)) {
							map_edge.put(article_node[0] + "-" + article, 1);
						} else {
							int num = map_edge.get(article_node[0] + "-"
									+ article) + 1;
							map_edge.put(article_node[0] + "-" + article, num);
						}
					}
					list_article.add(article_node[0]);
					map_word.put(word, list_article);
				} else {
					List<String> list_article = new ArrayList<String>();
					list_article.add(article_node[0]);
					map_word.put(word, list_article);
				}
			}

			list_node.add(article_node[0]);
			map_score.put(article_node[0], Double.parseDouble(article_node[5]));
		}
	}

	public List<String> stanfordByword(String content_all) {

		List<String> list_words = new ArrayList<String>();

		for (String mesh : list_word_MeSH) {
			if (mesh.equals("Pain")) {
				continue;
			}
			if (content_all.contains(mesh)) {
				list_words.add(mesh);
			}
		}

		return list_words;

	}

	// ----------------------------old---------------------------------

	// public void buildNetwork(String content_all, String[] node,
	// MaxentTagger tagger) {
	//
	// logger.info("正在加入节点" + node[0]);
	//
	// List<String> list_word = stanfordPOS(content_all, tagger);
	//
	// if (list_word != null) {
	// for (String word : list_word) {
	// if (map_word.containsKey(word)) {
	// List<String> list_article = map_word.get(word);
	// for (String article : list_article) {
	// if (article.equals(node[0])) {
	// continue;
	// }
	// if (!map_edge.containsKey(node[0] + "-" + article)) {
	// map_edge.put(node[0] + "-" + article, 1);
	// }else{
	// int num = map_edge.get(node[0] + "-" + article) + 1;
	// map_edge.put(node[0] + "-" + article, num);
	// }
	// }
	// list_article.add(node[0]);
	// map_word.put(word, list_article);
	// } else {
	// List<String> list_article = new ArrayList<String>();
	// list_article.add(node[0]);
	// map_word.put(word, list_article);
	// }
	// }
	//
	// list_node.add(node[0]);
	// map_article.put(node[0], node);
	// map_score.put(node[0], Double.parseDouble(node[5]));
	// }
	//
	// }
	//
	// public List<String> stanfordPOS(String content_all, MaxentTagger tagger)
	// {
	//
	// List<String> list_words = new ArrayList<String>();
	//
	// String tagged = new String();
	//
	// try {
	// tagged = tagger.tagString(content_all);
	// } catch (OutOfMemoryError e) {
	// // TODO: handle exception
	// return null;
	// }
	//
	// String[] sentences = tagged.split(" ");
	//
	// for (int i = 0; i < sentences.length; i++) {
	//
	// String sentence = sentences[i];
	//
	// String[] words = sentence.split("_");
	//
	// if (words[1].substring(0, 1).equals("N")) {
	// list_words.add(words[0]);
	// }
	//
	// }
	//
	// return list_words;
	//
	// }
	//
	// public List<String> stanfordPOS2(String content_all, MaxentTagger tagger)
	// {
	//
	// List<String> list_words = new ArrayList<String>();
	//
	// String tagged = new String();
	//
	// try {
	// tagged = tagger.tagString(content_all);
	// } catch (OutOfMemoryError e) {
	// // TODO: handle exception
	// return null;
	// }
	//
	// String[] sentences = tagged.split(" ");
	//
	// for (int i = 0; i < sentences.length; i++) {
	//
	// String sentence = sentences[i];
	//
	// String[] words = sentence.split("_");
	//
	// list_words.add(words[0]);
	//
	// }
	//
	// return list_words;
	//
	// }

}
