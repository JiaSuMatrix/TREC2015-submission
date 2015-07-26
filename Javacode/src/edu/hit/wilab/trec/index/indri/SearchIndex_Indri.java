package edu.hit.wilab.trec.index.indri;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import edu.hit.wilab.trec.index.File_Data;
import edu.hit.wilab.trec.index.Test;
import edu.hit.wilab.trec.index.XMLFile;
import edu.hit.wilab.trec.network.Config;
import edu.hit.wilab.trec.network.GenerationQuery;
import edu.hit.wilab.trec.network.Log;

public class SearchIndex_Indri {

	private static Logger logger = Logger.getLogger(SearchIndex_Indri.class);

	public static void main(String[] args) {

		// 载入Log日志

		Log.loadLogProperties();

		logger.info("日志系统加载成功！");

		// 载入配置文件

		Config config = new Config();

		config.loadConf();

		logger.info("配置文件加载成功！");

		logger.info("加载查询集...");

		// String[] topic_type = Config.type_diag;

		// GenerationQuery gq = new GenerationQuery();
		// List<List<String[]>> sentenceList = gq.query_non(Config.topic_num,
		// topic_type);

		logger.info("加载查询集完成!");

		long start = System.currentTimeMillis();

		SearchIndex_Indri sii = new SearchIndex_Indri();
		List<String[]> list_article = sii.searchIndex_string(Config.type_test,
				Config.fields_init, Config.search_init);

		long end = System.currentTimeMillis();

		System.out.println("搜索时间：" + (end - start) + "ms！");

		// List<String> list_id =
		// Test.read_list("./Trec_wordsets/qrels2014.txt",
		// Config.topic_num.split("_")[1]);
		// logger.info("正确答案共有" + list_id.size() + "个");

		int result_num = 1;

		for (String[] article : list_article) {
			if (article == null) {
				continue;
			}
			// if (list_id.contains(article[0])) {
			// result_num++;
			
			String[] ids = article[0].split("/");
			
			String id = ids[ids.length-1].split(".nxml")[0];
			
			String score = new String();
			if(article[1].length() > 5){
				score = article[1].substring(0, 8);
			}else{
				score = article[1];
			}
			
			logger.info(Config.type_diag[0] + " Q0 " + id + " "
					+ result_num + " " + score + " run_artificial");
			// }
			result_num++;
		}

		logger.info("Indri挖掘到" + list_article.size() + "个节点！");

	}

	public List<String[]> searchIndex(List<List<String[]>> sentenceList,
			String[] fields, int search_init) {

		buildQueryXML(sentenceList, fields, search_init);

		List<String[]> list_file_path = runEXE();

		List<String[]> list_article = parser(list_file_path);

		return list_article;

	}

	public List<String[]> searchIndex_string(String sentence, String[] fields,
			int search_init) {

		buildQueryXML_string(sentence, fields, search_init);

		List<String[]> list_file_path = runEXE();

//		List<String[]> list_article = parser(list_file_path);

		return list_file_path;

	}

	public List<String[]> searchIndex_word(String[] sentence, String[] fields,
			int search_init) {

		buildQueryXML_word(sentence, fields, search_init);

		List<String[]> list_file_path = runEXE();

		List<String[]> list_article = parser(list_file_path);

		return list_article;

	}

	public List<String[]> parser(List<String[]> list_file_path) {

		List<String[]> list_return = new ArrayList<String[]>();

		for (String[] filePath : list_file_path) {

			XMLFile xmlFile = new XMLFile();

			try {
				String[] article = xmlFile.parserXML_Indri(filePath);
				list_return.add(article);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return list_return;

	}

	public List<String[]> runEXE() {

		List<String[]> list_file_path = new ArrayList<String[]>();

		Process process;

		try {
			// 执行命令

			process = Runtime.getRuntime().exec(
					Config.indri_install_path
							+ "IndriRunQuery.exe query_para_file.xml");

			// 取得命令结果的输出流
			// 用一个读输出流类去读

			BufferedReader input = new BufferedReader(new InputStreamReader(
					process.getInputStream(), "UTF-8"));

			String line = null;

			while ((line = input.readLine()) != null) {
				
				String shortpath = line.split("TREC/")[1].split(" ")[0];

				String[] article = new String[2];
				article[0] = Config.indri_file_path + "/"
						+ shortpath.replaceAll("\\\\", "/");
				Double score = Double.parseDouble(line.split(" -")[1]
						.split(" ")[0]);
				article[1] = String.valueOf(score);

				list_file_path.add(article);
			}

			double max = Double.parseDouble(list_file_path.get(0)[1]);
			double min = Double.parseDouble(list_file_path.get(list_file_path
					.size() - 1)[1]);
			
			

			for (int i = 0; i < list_file_path.size(); i++) {
				String[] article = list_file_path.get(i);
				double score = (Double.parseDouble(article[1]) - min)
						/ (max - min);
				article[1] = String.valueOf(score);
				list_file_path.set(i, article);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return list_file_path;

	}

	public void buildQueryXML_string(String query_text, String[] fields,
			int search_init) {
		String field_string = new String();

		for (int i = 0; i < fields.length; i++) {
			if (i == fields.length - 1) {
				field_string += "field:" + fields[i];
			} else {
				field_string += "field:" + fields[i] + ",";
			}

		}

		File_Data
				.empty_file(Config.indri_install_path + "/query_para_file.xml");

		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<parameters>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<rule>method:d,mu," + field_string
				+ "</rule>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex00</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex01</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex02</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex03</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<count>" + search_init + "</count>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<query><text>" + query_text
				+ "</text></query>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<trecFormat>true</trecFormat>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml",
				"<queryOffset>1</queryOffset><runID>indri_query</runID>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "</parameters>");
	}

	public void buildQueryXML(List<List<String[]>> sentenceList,
			String[] fields, int search_init) {
		String field_string = new String();

		for (int i = 0; i < fields.length; i++) {
			if (i == fields.length - 1) {
				field_string += "field:" + fields[i];
			} else {
				field_string += "field:" + fields[i] + ",";
			}

		}

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

		File_Data
				.empty_file(Config.indri_install_path + "/query_para_file.xml");

		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<parameters>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<rule>method:d,mu," + field_string
				+ "</rule>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex00</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex01</index>");
		// File_Data.write_string(Config.indri_path + "/query_para_file.xml",
		// "<index>"+Config.indri_index_path+"/IndriIndex02</index>");
		// File_Data.write_string(Config.indri_path + "/query_para_file.xml",
		// "<index>"+Config.indri_index_path+"/IndriIndex03</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<count>" + search_init + "</count>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<query><text>" + query_text
				+ "</text></query>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<trecFormat>true</trecFormat>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml",
				"<queryOffset>1</queryOffset><runID>indri_query</runID>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "</parameters>");
	}

	public void buildQueryXML_word(String[] sentence, String[] fields,
			int search_init) {
		String field_string = new String();

		for (int i = 0; i < fields.length; i++) {
			if (i == fields.length - 1) {
				field_string += "field:" + fields[i];
			} else {
				field_string += "field:" + fields[i] + ",";
			}

		}

		String query_text = "#combine(#band(" + sentence[0] + "))";

		File_Data
				.empty_file(Config.indri_install_path + "/query_para_file.xml");

		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<parameters>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<rule>method:d,mu," + field_string
				+ "</rule>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex00</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex01</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex02</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<index>" + Config.indri_index_path
				+ "/IndriIndex03</index>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<count>" + search_init + "</count>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<query><text>" + query_text
				+ "</text></query>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "<trecFormat>true</trecFormat>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml",
				"<queryOffset>1</queryOffset><runID>indri_query</runID>");
		File_Data.write_string(Config.indri_install_path
				+ "/query_para_file.xml", "</parameters>");
	}

}
