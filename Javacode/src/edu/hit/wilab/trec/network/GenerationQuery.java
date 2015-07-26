package edu.hit.wilab.trec.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.hit.wilab.trec.index.File_Data;

public class GenerationQuery {

	private static Logger logger = Logger.getLogger(GenerationQuery.class);

	public List<List<String[]>> query_non(String topic_num, String[] topic_type) {

		List<List<String[]>> sentenceList = new ArrayList<List<String[]>>();

		List<String> list_result = File_Data.traverseFolder("./result_jjc/"
				+ topic_num + "/");// ./result/topic_1/

		Set<String> set_word = new HashSet<String>();

		for (String result : list_result) {
			String line_string = File_Data.read_list("./result_jjc/" + topic_num + "/" + result).get(0);
			
			String[] line_result = line_string.split("\\$");
			
			for (int i = 0; i < line_result.length; i++) {
				String[] words = line_result[i].split("_");
				if (!set_word.contains(words[0])) {
					List<String[]> list_word = new ArrayList<String[]>();
					list_word.add(words);
					sentenceList.add(list_word);
					set_word.add(words[0]);
				}
			}
		}

		return sentenceList;

	}
	
	public List<List<String[]>> query(String topic_num, String[] topic_type) {

		List<List<String[]>> sentenceList = new ArrayList<List<String[]>>();

		List<String> list_result = File_Data.traverseFolder("./result_jjc/"
				+ topic_num + "/");// ./result/topic_1/

		List<String[]> list_word = new ArrayList<String[]>();
		Set<String> set_word = new HashSet<String>();

		for (String result : list_result) {
			String line_string = File_Data.read_list(
					"./result_jjc/" + topic_num + "/" + result).get(0);
			String[] line_result = line_string.split("\\$");
			for (int i = 0; i < line_result.length; i++) {
				String[] words = line_result[i].split("_");
				if (!set_word.contains(words[0])) {
					list_word.add(words);
					set_word.add(words[0]);
				}
			}
		}

		for (int i = 0; i < topic_type.length; i++) {
			String[] sentence_topic = topic_type[i].split("_");
			for (String[] words : list_word) {
				List<String[]> list_query = new ArrayList<String[]>();
				String[] query = words[0].split(" ");
				for (int j = 0; j < query.length; j++) {
					String[] sentence = new String[2];
					sentence[0] = query[j];
					sentence[1] = words[1];
					logger.info(sentence[0]);
					list_query.add(sentence);
				}
				logger.info(sentence_topic[0]);
				list_query.add(sentence_topic);
				sentenceList.add(list_query);
			}
		}

		return sentenceList;

	}

	public List<List<String[]>> query_indri(String topic_num,
			String[] topic_type) {

		List<List<String[]>> sentenceList = new ArrayList<List<String[]>>();

		List<String> list_result = File_Data.traverseFolder("./result/"
				+ topic_num + "/");// ./result/topic_1/

		List<String[]> list_word = new ArrayList<String[]>();
		Set<String> set_word = new HashSet<String>();

		for (String result : list_result) {
			String line_string = File_Data.read_list(
					"./result/" + topic_num + "/" + result).get(0);
			String[] line_result = line_string.split("\\$");
			for (int i = 0; i < line_result.length; i++) {
				String[] words = line_result[i].split("_");
				if (!set_word.contains(words[0])) {
					list_word.add(words);
					set_word.add(words[0]);
				}
			}
		}

		for (int i = 0; i < topic_type.length; i++) {
			String[] sentence_topic = topic_type[i].split("_");
			for (String[] words : list_word) {
				List<String[]> list_query = new ArrayList<String[]>();
				String[] sentence = new String[2];
				sentence[0] = words[0];
				sentence[1] = words[1];
				list_query.add(sentence);
				list_query.add(sentence_topic);
				sentenceList.add(list_query);
			}
		}

		return sentenceList;

	}

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
		List<List<String[]>> sentenceList = gq.query_indri(Config.topic_num,
				topic_type);

		String query_text = new String();
		query_text = "#combine(";
		for (List<String[]> sentenceList_temp : sentenceList) {
			String query_text_temp = new String();
			query_text_temp += "#band(";
			for (int i = 0; i < sentenceList_temp.size(); i++) {
				
				if (i == sentenceList_temp.size() - 1) {
					if (sentenceList_temp.get(i)[0].contains(" ")) {
						query_text_temp += "#5(" + sentenceList_temp.get(i)[0]
								+ ")" + ")";
					} else {
						query_text_temp += sentenceList_temp.get(i)[0] + ")";
					}
				} else {
					if (sentenceList_temp.get(i)[0].contains(" ")) {
						query_text_temp += "#5(" + sentenceList_temp.get(i)[0]
								+ ")" + " ";
					} else {
						query_text_temp += sentenceList_temp.get(i)[0] + " ";
					}
				}
			}
			query_text += query_text_temp;
		}
		query_text += ")";

		System.out.println(query_text);
	}
}
