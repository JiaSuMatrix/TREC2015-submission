package edu.hit.wilab.trec.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortByValue {

	public static List<Object[]> sortMapByValue(Map<String, Double> map_Data) {
		
		List<Object[]> list_result = new ArrayList<Object[]>();
		
		//转换
		ArrayList<Entry<String, Double>> arrayList = new ArrayList<Map.Entry<String,Double>>(map_Data.entrySet());
		//排序
		Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>(){
		    public int compare(Map.Entry<String, Double> map1, Map.Entry<String,Double> map2) {
		        return ((map2.getValue() - map1.getValue() == 0) ? 0 : (map2.getValue() - map1.getValue() > 0) ? 1 : -1);
		    }
		});
		//输出
		for (Entry<String, Double> entry : arrayList) {
			Object[] object = new Object[2];
			object[0] = entry.getKey();
			object[1] = entry.getValue();
			list_result.add(object);
		}
		
		return list_result;
		
	}

}
