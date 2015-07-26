package edu.hit.wilab.trec.metamap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.hit.wilab.trec.index.File_Data;
import edu.hit.wilab.trec.network.Config;
import edu.hit.wilab.trec.network.Log;

public class WordExtension {

	private static Logger logger = Logger.getLogger(WordExtension.class);

	public static void main(String[] args) {

		// 载入Log日志
		Log.loadLogProperties();
		logger.info("日志系统加载成功！");

		// 载入配置文件

		Config config = new Config();
		config.loadConf();
		logger.info("配置文件加载成功！");

		WordExtension we = new WordExtension();
		
		List<String> list_topic = File_Data.traverseFolder("./run/");
		
		for(String topic : list_topic){
			logger.info("topic::"+topic);
			we.runcmd(topic);
		}
		
	}

	public void runcmd(String topic_num) {

		List<String> list_bat = File_Data.traverseFolder("./run/"+topic_num+"/");
		
		if(list_bat != null){
			
			for (String bat_filename : list_bat) {
				logger.info("bat_filename::"+bat_filename);
				Set<String> set_word = new HashSet<String>();
				set_word.add(bat_filename.substring(0,bat_filename.length()-4));
				Process process;
				try {
					// 执行命令
					
					process = Runtime.getRuntime().exec(
							"./run/"+topic_num+"/" + bat_filename);
					// 取得命令结果的输出流
					// 用一个读输出流类去读
					BufferedReader input = new BufferedReader(
							new InputStreamReader(process.getInputStream(), "UTF-8"));

					String line = null;

					boolean flag = false;

					while ((line = input.readLine()) != null) {

						if (line.contains("Meta Mapping") && flag == true) {
							flag = false;
						}
						
						if (flag) {
							if(!line.contains(":") || !line.contains("[")){
								continue;
							}
							
							String candidate = new String();
							
							if(line.contains("%")){
								line = line.replaceAll("%", "").trim();
							}
							
							String line_temp = line.split(":")[1].split("\\[")[0];
							if (line_temp.contains("(")) {
								candidate = line_temp.split("\\(")[0].trim();
							} else {
								candidate = line_temp.trim();
							}
							if (!set_word.contains(candidate)) {
								set_word.add(candidate);
							}
						}

						if (line.contains("Meta Candidates") && flag == false) {
							flag = true;
						}
						
					}

					String input_candidate = new String();

					for (String candidate : set_word) {
						input_candidate += candidate + "_1.0f$";
					}
					
					if(input_candidate.length() > 1){
						File_Data.write_string("./result/"+topic_num+"/" + bat_filename
								+ ".result", input_candidate.substring(0,
								input_candidate.length() - 1));
					}else{
						logger.error("候选词为空!");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			
		}

	}

}
