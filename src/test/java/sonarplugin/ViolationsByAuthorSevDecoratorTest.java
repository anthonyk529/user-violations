package sonarplugin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;
import org.sonar.api.rule.Severity;

import sonarplugin.batch.MeasureFactory;
import sonarplugin.batch.ViolationsByAuthorMetrics;
import sonarplugin.utils.MapUtils;

import com.google.common.collect.Iterables;

public class ViolationsByAuthorSevDecoratorTest {

	ViolationsByAuthorSevDecorator violationsByAuthorSevDecorator;
	Project project;
	Resource resource;
	DecoratorContext context;
	ResourcePerspectives perspectives;
	Issuable issuable;

	List<Issue> issues;
	Issue issue1;
	MeasureFactory measureFactory;
	Measure measure;
	Measure measure1;
	
	MapUtils mapUtils;
	
	@Before
	public void setUp() throws Exception {
		perspectives = mock(ResourcePerspectives.class);
		issuable = mock(Issuable.class);
		violationsByAuthorSevDecorator = new ViolationsByAuthorSevDecorator(perspectives);
		project=mock(Project.class);		
		resource = mock(Resource.class);
		context = mock(DecoratorContext.class);
		
		issues = new ArrayList<Issue>();
		issue1 = mock(Issue.class);
		measureFactory = mock(MeasureFactory.class);
		violationsByAuthorSevDecorator.setMeasureFactory(measureFactory);
		
		measure = mock(Measure.class);
		measure1 = mock(Measure.class);
		
		mapUtils = mock(MapUtils.class);
		
		violationsByAuthorSevDecorator.setMapUtils(mapUtils);
	}

	@Test
	public void testShouldExecuteOnProject() {
		assertTrue(violationsByAuthorSevDecorator.shouldExecuteOnProject(project));
	}
	
	@Test
	public void testGeneratesMetric() {		
		List<Metric> metrics = violationsByAuthorSevDecorator.generatesMetric();		
		assertEquals(metrics.size(), 1);
		
		Metric metric = Iterables.getOnlyElement(metrics);
	    assertEquals(metric.getKey(), "violations_by_author_sev");
	}
	
	@Test
	public void testDecorateFileScopeFile() {
		when(resource.getScope()).thenReturn(Scopes.FILE);
		when(measure.getData()).thenReturn("{\"ak\":[1,0,0,1,0,0]}");
		when(measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV)).thenReturn(measure);
		
		when(perspectives.as(Issuable.class, resource)).thenReturn(issuable);
		when(issuable.issues()).thenReturn(issues);
		
		when(measure1.getData()).thenReturn("1=ak;2=fd;3=ak");
		when(context.getMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE)).thenReturn(measure1);
		
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);		
		
		Map<String,List<Integer>> map = new HashMap<String,List<Integer>>();		
		map.put("ak", Arrays.asList(1,0,0,1,0,0));
		when(mapUtils.mapAuthorsAndViolationsSev("1=ak;2=fd;3=ak", issues)).thenReturn(map);
		
		when(measure.setData(JSONValue.toJSONString(map))).thenReturn(measure);				
		when(context.saveMeasure(measure1)).thenReturn(context);
		
		violationsByAuthorSevDecorator.decorate(resource, context);
		
		verify(context).saveMeasure(measure);
	}
	
	@Test
	public void testDecorateFileScopeFileWithNoMeasure() {
		when(resource.getScope()).thenReturn(Scopes.FILE);
		when(measure.getData()).thenReturn("{\"ak\":[1,0,0,1,0,0]}");
		when(measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV)).thenReturn(measure);
		
		when(perspectives.as(Issuable.class, resource)).thenReturn(issuable);
		when(issuable.issues()).thenReturn(issues);
		
		when(measure1.getData()).thenReturn("");
		when(context.getMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE)).thenReturn(measure1);
		
		when(issue1.line()).thenReturn(1);		
		when(issue1.severity()).thenReturn(Severity.MAJOR);
		issues.add(issue1);
		
		violationsByAuthorSevDecorator.decorate(resource, context);
		verify(context, never()).saveMeasure(measure);
	}
	
	@Test
	public void testDecorateFileScopeIsHigherThan() {		
		when(perspectives.as(Issuable.class, resource)).thenReturn(issuable);
		when(issuable.issues()).thenReturn(issues);
		
		Collection<Measure> measuresCollection = new ArrayList<Measure>();
		measuresCollection.add(measure);
		measuresCollection.add(measure1);
		
		when(resource.getScope()).thenReturn(Scopes.PROJECT);		
		when(context.getChildrenMeasures(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV)).thenReturn(measuresCollection);
		
		Map<String, List<String>> globalResultMap = new HashMap<String, List<String>>();
		List<String> listString = new ArrayList<String>();
		listString.add("string1");
		globalResultMap.put("key", listString);
		
		when(measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV)).thenReturn(measure1);
		
		violationsByAuthorSevDecorator.decorate(resource, context);
		
		verify(measure1).setData("{}");
		verify(context).saveMeasure(measure1);
	}

}
