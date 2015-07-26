package edu.hit.wilab.trec.metamap;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.hit.wilab.trec.index.File_Data;

public class BuildCmd {

	public static void main(String[] args) {
		
		BuildCmd cmd = new BuildCmd();
		
		List<String> list_topic = File_Data.read_list("./topic_word/topic_2014.txt");
		for(int i = 0 ; i < list_topic.size() ; i++){
			Set<String> set_word = new HashSet<String>();
			String[] words = list_topic.get(i).split(":")[1].split(";");
			for(String word : words){ 
				if(!set_word.contains(word)){
					cmd.buildBat(word, i+1);
					set_word.add(word);
				}
			}
		}
		
	}
	
	public void buildBat(String word, int i) {

		String bat = "echo "+word+" | .\\public_mm\\bin\\metamap13 -I";
		
		File_Data.write_string("./run/topic_"+i+"/"+word+".bat", bat);
		
	}
}
