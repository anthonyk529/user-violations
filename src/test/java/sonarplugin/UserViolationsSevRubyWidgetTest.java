package sonarplugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class UserViolationsSevRubyWidgetTest {

	private UserViolationsSevRubyWidget widget;
	@Before
	public void setUp() throws Exception {
		widget = new UserViolationsSevRubyWidget();
	}

	@Test
	public void testGetId() {
		assertEquals(widget.getId(),"user_violations_sev");
	}

	@Test
	public void testGetTitle() {
		assertEquals(widget.getTitle(),"User violations by severity");
	}
	
	@Test
	public void testGetTemplatePath() {
		assertNotNull(widget.getTemplatePath());
	}

}
