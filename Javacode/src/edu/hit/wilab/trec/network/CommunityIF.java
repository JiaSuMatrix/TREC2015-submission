//package edu.hit.wilab.trec.network;
//
//import java.io.BufferedReader;
//import java.io.FileInputStream;
//import java.io.InputStreamReader;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
//
//public class CommunityIF {
//
//	public static List<Double> list_IF = new ArrayList<Double>();
//
//	public static List<String> list_keyword = new ArrayList<String>();
//	
//	public CommunityIF(String type){
//		
//		read_string("./Trec_wordsets/words_"+type);
//		
//	}
//	
//	public void read_string(String filePath) {
//
//		try {
//
//			FileInputStream fis = new FileInputStream(filePath);
//
//			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
//
//			BufferedReader br = new BufferedReader(isr);
//
//			String line = null;
//
//			while ((line = br.readLine()) != null) {
//				list_keyword.add(line.split(";")[0]);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//	
//	public void calculateIF(MaxentTagger tagger) {
//
//		for(List<String> list_subcom : Community.list_community){
//			
//			Set<String> set_word = new HashSet<String>();
//			
//			for(String nodeid : list_subcom){
//				
//				String[] article = BuildNetwork.map_article.get(nodeid);
//				
//				BuildNetwork bn = new BuildNetwork();
//				
//				List<String> list_word = bn.stanfordPOS(article[1]+" "+article[2]+" "+article[3], tagger);
//				
//				if(list_word == null){
//					continue;
//				}
//				
//				for(String newword : list_word){
//					if(list_keyword.contains(newword)){
//						set_word.add(newword);
//					}
//				}
//				
//			}
//			
//			double if_value = set_word.size()/list_keyword.size();
//			list_IF.add(if_value);
//		}
//		
//	}

//}
