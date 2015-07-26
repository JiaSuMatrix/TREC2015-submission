package edu.hit.wilab.trec.index;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {

		List<String> list_id = read_list("./Trec_wordsets/qrels2014.txt","2");
//		List<String> list_id_indri = read_list2("./201423#band(#5(term)type)1000.txt");
		System.out.println(list_id.size());
//		int i = 0; 
//		for(String indri : list_id_indri){
//			if(list_id.contains(indri)){
//				System.out.println(indri);
//				i++;
//			}
//		}
//		System.out.println(i);
		
	}

	public static List<String> read_list2(String filePath) {

		List<String> list = new ArrayList<String>();

		try {

			FileInputStream fis = new FileInputStream(filePath);

			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

			BufferedReader br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null) {

				String[] ids = line.split(".nxml")[0].split("\\\\");
				list.add(ids[ids.length-1]);

			}
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}
	
	public static List<String> read_list(String filePath, String number) {

		List<String> list = new ArrayList<String>();

		try {

			FileInputStream fis = new FileInputStream(filePath);

			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

			BufferedReader br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null) {

				if(line.contains(number+"	0") && !line.contains("1"+number+"	0")&&!line.contains("2"+number+"	0")){
					String id = line.split("	0	")[1].split("	")[0];
					String num = line.split("	0	")[1].split("	")[1];
					if(num.equals("2")){
						list.add(id);
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
