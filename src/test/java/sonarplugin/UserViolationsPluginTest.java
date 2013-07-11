package sonarplugin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class UserViolationsPluginTest {

	private UserViolationsPlugin plugin;

	@Before
	public void setUp() throws Exception {		
		plugin = new UserViolationsPlugin();
	}

	@Test
	public void testGetExtensions() {
		assertEquals(plugin.getExtensions().size(), 3);
	}
}
