package sonarplugin;

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.WidgetCategory;
import org.sonar.api.web.WidgetScope;
import org.sonar.api.web.WidgetProperties;
import org.sonar.api.web.WidgetProperty;
import org.sonar.api.web.WidgetPropertyType;

import static org.sonar.api.web.WidgetScope.GLOBAL;

@WidgetCategory("Issues")
@WidgetScope(GLOBAL)
@WidgetProperties({
  @WidgetProperty(key = "filter", type = WidgetPropertyType.FILTER, optional = false)
})
public class UserViolationsSevRubyWidget extends AbstractRubyTemplate implements
		RubyRailsWidget {
	
	public String getId() {		
		return "user_violations_sev";
	}

	public String getTitle() {
		return "User violations by severity";
	}

	protected String getTemplatePath() {
		return "/org/sonar/plugins/userviolations/userviolationssev_widget.html.erb";		
	}
}
