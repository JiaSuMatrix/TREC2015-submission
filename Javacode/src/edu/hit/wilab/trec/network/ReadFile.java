package edu.hit.wilab.trec.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.ansj.util.FilterModifWord;

public class ReadFile {

	public static HashMap<String, String> readFileByLines(String fileName) {

		HashMap<String, String> updateDic = new HashMap<String, String>();

		File file = new File(fileName);

		BufferedReader reader = null;
		try {

			InputStreamReader isr = new InputStreamReader(new FileInputStream(
					file), "UTF-8");
			reader = new BufferedReader(isr);
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {

				updateDic.put(tempString, FilterModifWord._stop);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return updateDic;
	}

}
