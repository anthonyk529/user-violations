package sonarplugin;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

import sonarplugin.batch.ViolationsByAuthorMetrics;


public final class UserViolationsPlugin extends SonarPlugin {	
	
	public List getExtensions() {		
		return Arrays.asList(
			//definitions
			ViolationsByAuthorMetrics.class,	
			//batch
			ViolationsByAuthorSevDecorator.class,
			//ui
			UserViolationsSevRubyWidget.class);
	}	
}
