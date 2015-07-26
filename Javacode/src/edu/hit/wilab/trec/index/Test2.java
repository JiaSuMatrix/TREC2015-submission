package edu.hit.wilab.trec.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test2 {

	public static void main(String[] args) {

		List<String> list = read_list("./Trec_wordsets/words_Test");

		for(String word : list){
			write_string("./Trec_wordsets/test_word", word);
		}
		
	}

	public static void write_string(String filePath, String content) {

		File file = new File(filePath);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				System.err.println(e);
			}
		}

		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(content + "\n");
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			System.err.println(e);
		} catch (IOException e) {
			System.err.println(e);
		}

	}

	public static List<String> read_list(String filePath) {

		List<String> list = new ArrayList<String>();

		try {

			FileInputStream fis = new FileInputStream(filePath);

			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

			BufferedReader br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null) {

				String line1 = line.split(";")[0];
				if (line1.contains(",")) {
					if(!list.contains(line1.split(",")[0])){
						list.add(line1.split(",")[0]);
					}
				}else{
					if(!list.contains(line1)){
						list.add(line1);
					}
				}

			}
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}

}
