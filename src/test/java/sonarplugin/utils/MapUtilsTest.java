package sonarplugin.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.Measure;
import org.sonar.api.rule.Severity;

public class MapUtilsTest {
	
	private MapUtils mapUtils;	
	
	private List<Issue> issues;
	private Issue issue1;
	private Issue issue2;
	private Issue issue3;

	@Before
	public void setUp() throws Exception {
		mapUtils = new MapUtils();
		
		issues = new ArrayList<Issue>();
		issue1 = mock(Issue.class);
		issue2 = mock(Issue.class);
		issue3 = mock(Issue.class);
	}
	
	@Test
	public void testUpdateGlobalResultMapSev() {
		List<Measure> measures = new ArrayList<Measure>();		
		Measure measure1 = mock(Measure.class);
		when(measure1.getData()).thenReturn("{\"fd\":[1,0,0,1,0,0],\"ak\":[2,2,0,0,0,0]}");		
		Measure measure2 = mock(Measure.class);
		when(measure2.getData()).thenReturn("{\"fd\":[3,1,1,1,0,0],\"ao\":[1,1,0,0,0,0]}");		
		measures.add(measure1);
		measures.add(measure2);
		
		Map<String, List<Integer>> result = mapUtils.updateGlobalResultMapSev(measures);
		assertEquals(result.keySet().size(), 3);
		assertTrue(result.keySet().contains("ao"));
		assertEquals(result.get("fd").size(), 6);
		assertEquals(result.get("fd").get(0).intValue(), 4);
	}
	
	@Test
	public void testUpdateGlobalResultMapSevWithNoData() {
		List<Measure> measures = new ArrayList<Measure>();		
		Measure measure1 = mock(Measure.class);
		when(measure1.getData()).thenReturn("{\"fd\":[1,0,0,1,0,0],\"ak\":[2,2,0,0,0,0]}");		
		Measure measure2 = mock(Measure.class);
		when(measure2.getData()).thenReturn("");		
		measures.add(measure1);
		measures.add(measure2);
		
		Map<String, List<Integer>> result = mapUtils.updateGlobalResultMapSev(measures);
		assertEquals(result.keySet().size(), 2);		
		assertEquals(result.get("fd").size(), 6);
		assertEquals(result.get("fd").get(0).intValue(), 1);
	}

	@Test
	public void testMapAuthorsAndViolationsSev() {		
		String author = "1=ak;2=fd;3=ak";		
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.INFO);
		when(issue2.line()).thenReturn(2);		
		when(issue2.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);
		issues.add(issue2);		
		Map<String, List<Integer>> result = mapUtils.mapAuthorsAndViolationsSev(author, issues);
		Map<String, List<Integer>> expectedResult = new HashMap<String, List<Integer>>();
		expectedResult.put("ak", Arrays.asList(1,1,0,0,0,0));
		expectedResult.put("fd", Arrays.asList(1,0,0,1,0,0));		
		assertEquals(expectedResult, result);		
	}
	
	@Test
	public void testMapAuthorsAndViolationsSevWithNoLine() {
		String author = "1=ak;2=fd;3=ak";
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.INFO);
		when(issue2.line()).thenReturn(null);
		when(issue2.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);
		issues.add(issue2);
		Map<String, List<Integer>> result = mapUtils.mapAuthorsAndViolationsSev(author, issues);
		Map<String, List<Integer>> expectedResult = new HashMap<String, List<Integer>>();
		expectedResult.put("ak", Arrays.asList(1,1,0,0,0,0));				
		assertEquals(expectedResult, result);		
	}
	
	@Test
	public void testMapAuthorsAndViolationsSevWithBadLine() {
		String author = "1=ak;2=fd;3=ak";
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.INFO);
		when(issue2.line()).thenReturn(4);
		when(issue2.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);
		issues.add(issue2);
		Map<String, List<Integer>> result = mapUtils.mapAuthorsAndViolationsSev(author, issues);
		Map<String, List<Integer>> expectedResult = new HashMap<String, List<Integer>>();
		expectedResult.put("ak", Arrays.asList(1,1,0,0,0,0));				
		assertEquals(expectedResult, result);		
	}
	
	@Test
	public void testMapAuthorsAndViolationsSevWithExistingKey() {
		String author = "1=ak;2=fd;3=ak";		
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.INFO);
		when(issue2.line()).thenReturn(2);		
		when(issue2.severity()).thenReturn(Severity.MAJOR);
		when(issue3.line()).thenReturn(3);		
		when(issue3.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);
		issues.add(issue2);
		issues.add(issue3);		
		Map<String, List<Integer>> result = mapUtils.mapAuthorsAndViolationsSev(author, issues);
		Map<String, List<Integer>> expectedResult = new HashMap<String, List<Integer>>();
		expectedResult.put("ak", Arrays.asList(2,1,0,1,0,0));
		expectedResult.put("fd", Arrays.asList(1,0,0,1,0,0));		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testUpdatSeverityList() {
		List<Integer> severityList = Arrays.asList(1,0,1,0,0,0);		
		int position = 1;
		severityList = mapUtils.updateSeverityList(severityList, position);
		assertEquals(severityList.get(2).intValue(), 2);
		assertEquals(severityList.get(0).intValue(), 2);
	}
	
	@Test
	public void testInitializeSeverityList() {
		int position = 3;		
		List<Integer> expectedResult = Arrays.asList(1,0,0,0,1,0);
		List<Integer> severityList = mapUtils.initializeSeverityList(position);
		assertEquals(severityList.size(), 6);
		assertEquals(expectedResult, severityList);
	}
	
	@Test
	public void testSumSeverityList() {
		List<Integer> list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(2);
		List<Integer> list2 = new ArrayList<Integer>();
		list2.add(3);
		list2.add(4);
		List<Integer> expectedResult = new ArrayList<Integer>();
		expectedResult.add(4);
		expectedResult.add(6);
		List<Integer> result = mapUtils.sumSeverityList(list1, list2);
		assertEquals(expectedResult, result);
	}
}
