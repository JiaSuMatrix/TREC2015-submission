package edu.hit.wilab.trec.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapSort {  
    public static void main(String[] args) {  
        Map<String, Double> map = new TreeMap<String, Double>();  
        map.put("KFC", 2.4);  
        map.put("WNBA", 6.6);  
        map.put("NBA", 1.2);  
        map.put("CBA", 3.5);  
		Map<String, Double> resultMap = sortMapByValue(map); // 按Value进行排序
		for (Map.Entry<String, Double> entry : resultMap.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}  
    }  
      
    /** 
     * 使用 Map按value进行排序 
     * @param map 
     * @return 
     */  
    public static Map<String, Double> sortMapByValue(Map<String, Double> map) {  
        if (map == null || map.isEmpty()) {  
            return null;  
        }  
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();  
        List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(map.entrySet());  
        Collections.sort(entryList, new MapValueComparator());  
        Iterator<Map.Entry<String, Double>> iter = entryList.iterator();  
        Map.Entry<String, Double> tmpEntry = null;  
        while (iter.hasNext()) {  
            tmpEntry = iter.next();  
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());  
        }  
        return sortedMap;  
    }  
}  
  
