package sonarplugin.batch;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MeasureFactoryTest {

	private MeasureFactory measureFactory;
	
	@Before
	public void setUp() throws Exception {
		measureFactory = new MeasureFactory();
	}

	@Test
	public void testGetMeasure() {
		assertNotNull(measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV));
		assertEquals(measureFactory.getMeasure(ViolationsByAuthorMetrics.VIOLATIONS_BY_AUTHOR_SEV).getData(),"");
	}

}
