package gov.usgs.cida.pubs.webservice.mp;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONArrayAs;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.PubsConstants;
import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.mp.MpListDaoTest;
import gov.usgs.cida.pubs.domain.mp.MpList;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class MpListMvcServiceTest extends BaseSpringTest {

	@Mock
	private IBusService<MpList> busService;

    private MockMvc mockMvc;

    private MpListMvcService mvcService;
    
    @Before
    public void setup() {
    	MockitoAnnotations.initMocks(this);
    	mvcService = new MpListMvcService(busService);
    	mockMvc = MockMvcBuilders.standaloneSetup(mvcService).build();
    }

    @SuppressWarnings("unchecked")
	@Test
    public void getListsTest() throws Exception {
        when(busService.getObjects(anyMap())).thenReturn(getListOfMpList());
        MvcResult rtn = mockMvc.perform(get("/lists?mimetype=json").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(content().encoding(PubsConstants.DEFAULT_ENCODING))
        .andReturn();

        assertThat(new JSONArray(rtn.getResponse().getContentAsString()),
                sameJSONArrayAs(new JSONArray("[{\"id\":1,\"text\":\"List 1\",\"description\":\"Description 1\",\"type\":\"SPN\"},"
                		+ "{\"id\":2,\"text\":\"List 2\",\"description\":\"Description 2\",\"type\":\"SPN\"}]")));
    }

    public static List<MpList> getListOfMpList() {
    	List<MpList> rtn = new ArrayList<>();
    	rtn.add(MpListDaoTest.buildMpList(1));
    	rtn.add(MpListDaoTest.buildMpList(2));
    	return rtn;
    }
    
}
