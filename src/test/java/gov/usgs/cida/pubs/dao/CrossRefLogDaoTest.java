package gov.usgs.cida.pubs.dao;

import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.IntegrationTest;
import gov.usgs.cida.pubs.domain.CrossRefLog;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import gov.usgs.cida.pubs.dao.intfc.ICrossRefLogDao;
import org.springframework.beans.factory.annotation.Autowired;

@Category(IntegrationTest.class)
@DatabaseSetups({
	@DatabaseSetup("classpath:/testCleanup/clearAll.xml"),
	@DatabaseSetup("classpath:/testData/publicationType.xml"),
	@DatabaseSetup("classpath:/testData/publicationSubtype.xml"),
	@DatabaseSetup("classpath:/testData/publicationSeries.xml"),
	@DatabaseSetup("classpath:/testData/dataset.xml")
})
public class CrossRefLogDaoTest extends BaseSpringTest {

	@Autowired
	protected ICrossRefLogDao crossRefLogDao;
	
	@Test
	@ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,
		table="CROSS_REF_LOG",
		query="select batch_id,prod_id,xmlserialize(content cross_ref_xml as text) cross_ref_xml from cross_ref_log order by 1",
		value="classpath:/testResult/xrefLog.xml")
	public void testCreate() {
		CrossRefLog log = new CrossRefLog();
		log.setBatchId("123");
		log.setProdId("456");
		log.setCrossrefXml("<root/>");
		crossRefLogDao.add(log);

		log = new CrossRefLog("abc", 666, "<root2/>");
		crossRefLogDao.add(log);
	}

}
