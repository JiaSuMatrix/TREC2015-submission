package edu.hit.wilab.trec.network;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

//比较器类  
public class MapValueComparator implements
		Comparator<Map.Entry<String, Double>> {
	public int compare(Entry<String, Double> me1, Entry<String, Double> me2) {
		return me1.getValue().compareTo(me2.getValue());
	}
}
