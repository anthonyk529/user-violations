package sonarplugin.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.rules.RulePriority;

public class MapUtils {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(MapUtils.class);
	private JSONParser parser = new JSONParser();
	
	public Map<String, List<Integer>> updateGlobalResultMapSev(
			Collection<Measure> measures) {
		Map<String, List<Integer>> resultMap = new HashMap<String, List<Integer>>();
		for (Measure m : measures) {
			if(MeasureUtils.hasData(m)) {
				LOG.info("Children measures : "+m.getData());
				try {
					JSONObject measureMap2 = (JSONObject) parser.parse(m.getData());										
					Iterator i = measureMap2.entrySet().iterator();
					while (i.hasNext()) {
						Map.Entry<String, JSONArray> e = (Map.Entry<String, JSONArray>)i.next();						
						JSONArray array = (JSONArray) e.getValue();						
						List<Integer> list = jSonArrayToList(array);						
						if(resultMap.containsKey(e.getKey())) {							
							List<Integer> severityList = sumSeverityList(resultMap.get(e.getKey()), list);
							resultMap.put(e.getKey(), severityList);
						}
						else {							
							resultMap.put(e.getKey(), list);
						}
					}
				} catch (ParseException e) {
					LOG.error(e.getMessage());
				}
			}
		}
		return resultMap;
	}
	
	public Map<String, List<Integer>> mapAuthorsAndViolationsSev(String authors,
			List<Issue> issues) {
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();		
		String[] tabAuthors;
		String name;
		int line;
		
		tabAuthors = authors.split(";");
		for (Issue i : issues) {			
			Integer l = i.line();
			line = (l==null ? 0 : l.intValue());
			if (line>0 && line <= tabAuthors.length) {
				name = ((tabAuthors[line - 1]).split("="))[1];				
				int position = ArrayUtils.indexOf(RulePriority.values(), RulePriority.valueOf(i.severity().toString()));
				List<Integer> severityList;
				if (map.containsKey(name)) {
					severityList = updateSeverityList(map.get(name), position);					
					map.put(name, severityList);					
				} else {					
					severityList = initializeSeverityList(position);
					map.put(name, severityList);					
				}				
			}
		}
		return map;		
	}
	
	protected List<Integer> jSonArrayToList(JSONArray array) {
		List<Integer> list = new ArrayList<Integer>();
		Iterator<Number> iterator = array.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next().intValue());
		}
		return list;
	}
	
	protected List<Integer> updateSeverityList(List<Integer> severityList, int position) {
		int value = severityList.get(position+1)+1;
		int totalSum = severityList.get(0)+1;
		severityList.set(position+1,value);
		severityList.set(0, totalSum);
		return severityList;
	}
	
	protected List<Integer> initializeSeverityList(int position) {		
		List<Integer> severityList = Arrays.asList(0,0,0,0,0,0);
		severityList.set(0,1);
		severityList.set(position+1, 1);
		return severityList;		
	}
	
	protected List<Integer> sumSeverityList(List<Integer> list1, List<Integer> list2) {
		List<Integer> result = new ArrayList<Integer>();
		int i = 0;		
		while (i<list1.size()) {
			int sum = list1.get(i) + list2.get(i).intValue();
			result.add(sum);
			i++;
		}
		return result;
	}	
}
