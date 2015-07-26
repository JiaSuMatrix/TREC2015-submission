//package edu.hit.wilab.trec.network;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.xml.sax.SAXException;
//
//import edu.hit.wilab.trec.index.File_Data;
//import edu.hit.wilab.trec.index.XMLFile;
//
//public class MiningNode {
//
//	public static Map<Integer, Double> map_coefficent = new HashMap<Integer, Double>();
//
//	public void process(){
//		
//		List<String> list_pmc = File_Data.traverseFolder("./dataset");
//		
//		for(String pmc : list_pmc){
//			
//			List<String> list_num = File_Data.traverseFolder("./dataset/"+pmc);
//			
//			for(String num : list_num){
//				
//				List<String> list_xml = File_Data.traverseFolder("./dataset/"+pmc+"/"+num);
//				
//				for(String xml : list_xml){
//					connectNodes("./dataset/"+pmc+"/"+num+"/"+xml, xml.substring(0, xml.length()-5));
//				}
//				
//			}
//			
//		}
//		
//	}
//
//	public void connectNodes(String filePath, String nodeid){
//		
//		XMLFile xmlFile = new XMLFile();
//		
//		List<String> list_word = new ArrayList<String>();
//		
//		try {
//			list_word = xmlFile.getKeyword(filePath);
//		} catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		Set<String> set_article = new HashSet<String>();
//		
//		for(String word : list_word){
//			if(BuildNetwork.map_word.containsKey(word)){
//				List<String> list_article = BuildNetwork.map_word.get(word);
//				for(String article : list_article){
//					set_article.add(article);
//				}
//			}
//		}
//		
//		if(set_article.size() > 2){
//			
//			//寻找节点所属团体
//			int maxWordCount = 0;
//			int communityNum = 0;
//			//所属团体中节点列表
//			List<String> list_community_final = new ArrayList<String>();
//			int i = 0;
//			for(List<String> list_community : Community.list_community){
//				int wordCount = 0;
//				List<String> community_article = new ArrayList<String>();
//				for(String article : set_article){
//					if(list_community.contains(article)){
//						community_article.add(article);
//						wordCount++;
//					}
//				}
//				if(wordCount > maxWordCount){
//					list_community_final = community_article;
//					maxWordCount = wordCount;
//					communityNum = i;
//				}
//				i++;
//			}
//			
//			int edgeCount = 0;//隐藏节点所连接节点实际产生的边
//			double coefficent = 0.0;//团体聚集系数
//			
//			//计算团体聚集系数
//			if(!map_coefficent.containsKey(communityNum)){
//				List<String> list_community_in = Community.list_community.get(communityNum);
//				
//				int allCount = 0;
//				int allNodeCount = list_community_in.size();
//				
//				for(String community_in1 : list_community_in){
//					for(String community_in2 : list_community_in){
//						if(!community_in1.equals(community_in2)){
//							if(BuildNetwork.map_edge.containsKey(community_in1+"-"+community_in2)){
//								allCount++;
//								if(list_community_final.contains(community_in1)&list_community_final.contains(community_in2)){
//									edgeCount++;
//								}
//							}
//						}
//					}
//				}
//				coefficent = 2*allCount/allNodeCount*(allNodeCount-1);
//				map_coefficent.put(communityNum, coefficent);
//			}else{
//				for(String community_final1 : list_community_final){
//					for(String community_final2 : list_community_final){
//						if(BuildNetwork.map_edge.containsKey(community_final1+"-"+community_final2)){
//							edgeCount++;
//						}
//					}
//				}
//				coefficent = map_coefficent.get(communityNum);
//			}
//			
//			int nodeCount = list_community_final.size();
//			
//			int total = nodeCount*(nodeCount-1)/2;
//			
//			double cc = edgeCount/total;
//			
//			if(cc>coefficent){
//				List<String> list_community_in = Community.list_community.get(communityNum);
//				list_community_in.add(nodeid);
//				Community.list_community.add(communityNum, list_community_in);
//				
//				double total_aver = 0.0;
//				
//				for(String community_final : list_community_final){
//					total_aver += BuildNetwork.map_score.get(community_final);
//				}
//				
//				BuildNetwork.list_node.add(nodeid);
//				BuildNetwork.map_score.put(nodeid, cc*total_aver/nodeCount);
//				
//				for(String nodesource : list_community_final){
//					BuildNetwork.map_edge.put(nodesource+"-"+nodeid, 1);
//				}
//				
//			}
//			
//		}
//		
//	}
//}
