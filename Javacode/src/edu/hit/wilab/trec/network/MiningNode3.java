//package edu.hit.wilab.trec.network;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import edu.hit.wilab.trec.index.SearchIndex;
//
//public class MiningNode3 {
//
//	public void miningNode() {
//
//		Map<String, Set<String>> map_set = new HashMap<String, Set<String>>();
//
//		Set<String> key = BuildNetwork.map_word.keySet();
//		for (Iterator it = key.iterator(); it.hasNext();) {
//			String[] word = new String[2];
//			word[0] = (String) it.next();// 搜索词
//			word[1] = "1";// 权重
//			List<String[]> list_sentence = new ArrayList<String[]>();
//			list_sentence.add(word);// 搜索列表
//
//			List<String> list_article_in = BuildNetwork.map_word.get(word[0]);
//
//			// 检索相关论文
//			SearchIndex si = new SearchIndex();
//			String[] fields = { "title", "keywords" };
//			List<String[]> list_article = si.searchIndex(list_sentence, fields);
//
//			for (String[] article : list_article) {
//				if (list_article_in.contains(article[0])) {
//					continue;
//				}
//				if (map_set.containsKey(article[0])) {
//					Set<String> set_article = map_set.get(article[0]);
//					set_article.addAll(list_article_in);
//					map_set.put(article[0], set_article);
//				} else {
//					Set<String> set_article = new HashSet<String>();
//					set_article.addAll(list_article_in);
//					map_set.put(article[0], set_article);
//					BuildNetwork.map_article.put(article[0], article);
//					BuildNetwork.list_node.add(article[0]);
//				}
//			}
//
//		}
//
//		Set<String> key_set = map_set.keySet();
//		for (Iterator it = key_set.iterator(); it.hasNext();) {
//			String nodeid = (String) it.next();
//			Set<String> set_sub = map_set.get(nodeid);
//			for (String connectnode : set_sub) {
//				BuildNetwork.map_edge.put(connectnode + "-" + nodeid, 1);
//			}
//		}
//
//	}
//
//}
