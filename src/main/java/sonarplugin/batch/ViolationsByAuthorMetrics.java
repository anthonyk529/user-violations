package sonarplugin.batch;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public class ViolationsByAuthorMetrics implements Metrics {
	public static final Metric VIOLATIONS_BY_AUTHOR = new Metric.Builder(
			"violations_by_author", "Violations by author",	Metric.ValueType.DATA)
			.setDescription("Violations by author")
			.setDirection(Metric.DIRECTION_NONE)
			.setDomain(CoreMetrics.DOMAIN_GENERAL)
			.setQualitative(false)
			.create();
	
	public static final Metric VIOLATIONS_BY_AUTHOR_SEV = new Metric.Builder(
			"violations_by_author_sev", "Violations by author and severity", Metric.ValueType.DATA)
			.setDescription("Violations by author and severity")
			.setDirection(Metric.DIRECTION_NONE)
			.setDomain(CoreMetrics.DOMAIN_GENERAL)
			.setQualitative(false)
			.create();

	public List<Metric> getMetrics() {
		return Arrays.asList(VIOLATIONS_BY_AUTHOR,
							VIOLATIONS_BY_AUTHOR_SEV);
	}
}

