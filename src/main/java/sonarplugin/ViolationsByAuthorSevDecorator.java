package sonarplugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorBarriers;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.batch.DependsUpon;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasureUtils;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.Scopes;

import sonarplugin.batch.MeasureFactory;
import sonarplugin.batch.ViolationsByAuthorMetrics;
import sonarplugin.utils.MapUtils;

import com.google.common.collect.ImmutableList;


@DependsUpon(DecoratorBarriers.ISSUES_TRACKED)
public class ViolationsByAuthorSevDecorator implements Decorator {

	private static final Logger LOG = LoggerFactory
			.getLogger(ViolationsByAuthorSevDecorator.class);
	private MapUtils mapUtils = new MapUtils();
	private MeasureFactory measureFactory = new MeasureFactory();
	
	private final ResourcePerspectives perspectives;
	
	public ViolationsByAuthorSevDecorator(ResourcePerspectives p) {
		this.perspectives = p;
	}
	
	@Override
	public boolean shouldExecuteOnProject(Project project) {		
		return true;
	}
	
	@DependedUpon
	public List<Metric> generatesMetric() {
		return ImmutableList.of(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV);
	}
	
	@Override
	public void decorate(Resource resource, DecoratorContext context) {
		Measure measure = measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV);
		Issuable issuable = perspectives.as(Issuable.class, resource);
		if (issuable != null) {
			if (Scopes.isFile(resource)) {
				Measure measureAuthor = context.getMeasure(CoreMetrics.SCM_AUTHORS_BY_LINE);
				if(MeasureUtils.hasData(measureAuthor)) {
					List<Issue> issues = issuable.issues();					
					LOG.info("Analyzed resource : "+resource.getKey());					
					Measure measureFile = generateMeasure(measureAuthor.getData(), issues);
					context.saveMeasure(measureFile);
				}
			}		
			else if (Scopes.isHigherThan(resource, Scopes.FILE)) { 
				Collection<Measure> childrenMeasures = context.getChildrenMeasures(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV);
				Map<String, List<Integer>> globalResultMap;
				globalResultMap = mapUtils.updateGlobalResultMapSev(childrenMeasures);
				LOG.info("globalResultJSONSEV for " + resource.getKey() + " : "+ JSONValue.toJSONString(globalResultMap));
				measure.setData(JSONValue.toJSONString(globalResultMap));			
				context.saveMeasure(measure);
			}
		}
	}
	
	protected Measure generateMeasure(String authors, List<Issue> issues) {
		Measure measure = measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV);
		Map<String, List<Integer>> map = mapUtils.mapAuthorsAndViolationsSev(authors, issues);		
		String json = JSONValue.toJSONString(map);
		measure.setData(json);
		return measure;
	}
		
	public void setMeasureFactory(MeasureFactory measureFactory) {
		this.measureFactory = measureFactory;
	}
	
	public void setMapUtils(MapUtils mapUtils) {
		this.mapUtils = mapUtils;
	}
}
