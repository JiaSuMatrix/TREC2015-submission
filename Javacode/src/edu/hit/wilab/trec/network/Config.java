package edu.hit.wilab.trec.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {

	private static Logger logger = Logger.getLogger(Config.class);

	private Properties propertie;
	private FileInputStream inputFile;

	public static int search_init;// 初始检索结果数量
	public static String[] fields_init;//初始检索域
	public static int search_final;// 每个关键词挖掘多少隐藏节点
	public static String[] fields_final;//挖掘影藏节点时检索域
	
	public static int edge_num;// 设置边权重
	
	public static int edge_node_max;// 设置边权重
	public static int edge_node_min;//潜在节点连接边的权重
	public static double edge_value;//潜在节点连接边的权重
	
	public static String[] type_diag;
	public static String type_test;
	public static String[] type_treatment;
	public static String wordFilePath;//MeSH医疗词列表文件地址
	
	public static String topic_num;//运行topic编号
	
	public static String index_type;//搜索引擎类型，0：Lucene，1：Indri
	
	public static String indri_install_path;//Indri的安装路径
	public static String indri_file_path;//Indri的文件路径
	public static String indri_index_path;//Indri的索引路径
	
	public void getLink(String filePath) {
		propertie = new Properties();
		try {
			inputFile = new FileInputStream(filePath);
			propertie.load(inputFile);
			inputFile.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	public String getValue(String key) {
		if (propertie.containsKey(key)) {
			String value = propertie.getProperty(key);
			return value;
		} else
			return "";
	}

	public void loadConf() {
		Config config = new Config();
		config.getLink("." + File.separator + "conf.properties");

		search_init = Integer.parseInt(config.getValue("search_init").trim());
		search_final = Integer.parseInt(config.getValue("search_final").trim());
		fields_init = config.getValue("fields_init").trim().split("_");
		fields_final = config.getValue("fields_final").trim().split("_");
		edge_num = Integer.parseInt(config.getValue("edge_num").trim());
		edge_node_max = Integer.parseInt(config.getValue("edge_node_max").trim());
		edge_node_min = Integer.parseInt(config.getValue("edge_node_min").trim());
		edge_value = Double.parseDouble(config.getValue("edge_value").trim());
		topic_num = config.getValue("topic_num").trim();
		
		type_diag = config.getValue("type_diag").trim().split(" ");
		type_test = config.getValue("type_test").trim();
		type_treatment = config.getValue("type_treatment").trim().split(" ");
		
		indri_install_path = config.getValue("indri_install_path").trim();
		indri_file_path = config.getValue("indri_file_path").trim();
		indri_index_path = config.getValue("indri_index_path").trim();
		
		index_type = config.getValue("index_type").trim();
		
		wordFilePath = config.getValue("wordFilePath").trim();
		
	}

}
