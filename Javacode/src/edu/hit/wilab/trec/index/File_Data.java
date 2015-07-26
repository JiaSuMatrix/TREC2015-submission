package edu.hit.wilab.trec.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class File_Data {

	public static List<String> traverseFolder(String path) {

		List<String> list = new ArrayList<String>();

		File file = new File(path);
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files.length == 0) {
				return null;
			} else {
				for (File file2 : files) {
					String fileName = file2.getName();
					if (list.contains(fileName)) {
						continue;
					} else {
						list.add(fileName);
					}
				}
			}
		}

		return list;

	}
	
	public static void empty_file(String filePath){
		
		try {
			File f = new File(filePath);
			
			FileWriter fw = new FileWriter(f);
			
			fw.write("");
			
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

				list.add(line);

			}
			br.close();
			isr.close();
			fis.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;

	}
	
	public static String read_string(String filePath) {

		StringBuffer sb = new StringBuffer();

		try {

			FileInputStream fis = new FileInputStream(filePath);

			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");

			BufferedReader br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null) {
				sb.append(line+" ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();

	}
	
}
