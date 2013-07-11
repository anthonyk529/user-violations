package sonarplugin.batch;

import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;

public class MeasureFactory {
	
	public Measure getMeasure(Metric metric) {		
		return new Measure(metric, "");
	}
}
