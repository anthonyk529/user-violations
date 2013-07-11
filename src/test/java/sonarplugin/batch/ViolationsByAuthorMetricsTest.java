package sonarplugin.batch;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.sonar.api.measures.Metric;

import sonarplugin.batch.ViolationsByAuthorMetrics;

import com.google.common.collect.Iterables;

public class ViolationsByAuthorMetricsTest {

	@Test
	public void testGetMetrics() {
		List<Metric> metrics = new ViolationsByAuthorMetrics().getMetrics();
		
		assertEquals(metrics.size(), 2);
		
		Metric metric = /*Iterables.getOnlyElement(metrics)*/metrics.get(0);		
		assertEquals(metric.getDomain(), "General");
	    assertEquals(metric.getKey(), "violations_by_author");
	    assertEquals(metric.getName(), "Violations by author");
	    assertEquals(metric.getType(), Metric.ValueType.DATA);
	}
}
