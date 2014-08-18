package gov.usgs.cida.pubs.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import gov.usgs.cida.pubs.BaseSpringTest;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.CorporateContributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ContributorDaoTest extends BaseSpringTest {

    public static final int contributorCnt = 3;

    public static final List<String> IGNORE_PROPERTIES_PERSON = Arrays.asList("validationErrors", "valErrors", "organization");
    public static final List<String> IGNORE_PROPERTIES_CORPORATION = Arrays.asList("validationErrors", "valErrors", "family", 
            "given", "suffix", "email", "affiliation");

    @Test
    public void getByIdInteger() {
        //USGS Contributor
        Contributor<?> contributor = Contributor.getDao().getById(1);
        assertEquals(1, contributor.getId().intValue());
        assertTrue(contributor instanceof UsgsContributor);
        UsgsContributor usgsContributor = (UsgsContributor) contributor;
        assertEquals("ConFirst", usgsContributor.getFamily());
        assertEquals("ConGiven", usgsContributor.getGiven());
        assertEquals("ConSuffix", usgsContributor.getSuffix());
        assertEquals("con@usgs.gov", usgsContributor.getEmail());
        assertEquals(22, usgsContributor.getAffiliation().getId().intValue());

        //Non-USGS Contributor
        contributor = Contributor.getDao().getById(3);
        assertEquals(3, contributor.getId().intValue());
        assertTrue(contributor instanceof OutsideContributor);
        OutsideContributor outsideContributor = (OutsideContributor) contributor;
        assertEquals("outerfamily", outsideContributor.getFamily());
        assertEquals("outerGiven", outsideContributor.getGiven());
        assertEquals("outerSuffix", outsideContributor.getSuffix());
        assertEquals("outer@gmail.com", outsideContributor.getEmail());
        assertEquals(182, outsideContributor.getAffiliation().getId().intValue());

        //Corporate Contributor
        contributor = Contributor.getDao().getById(2);
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByIdString() {
        //USGS Contributor
        Contributor<?> contributor = Contributor.getDao().getById("1");
        assertEquals(1, contributor.getId().intValue());
        assertTrue(contributor instanceof UsgsContributor);
        UsgsContributor usgsContributor = (UsgsContributor) contributor;
        assertEquals("ConFirst", usgsContributor.getFamily());
        assertEquals("ConGiven", usgsContributor.getGiven());
        assertEquals("ConSuffix", usgsContributor.getSuffix());
        assertEquals("con@usgs.gov", usgsContributor.getEmail());
        assertEquals(22, usgsContributor.getAffiliation().getId().intValue());

        //Non-USGS Contributor
        contributor = Contributor.getDao().getById("3");
        assertEquals(3, contributor.getId().intValue());
        assertTrue(contributor instanceof OutsideContributor);
        OutsideContributor outsideContributor = (OutsideContributor) contributor;
        assertEquals("outerfamily", outsideContributor.getFamily());
        assertEquals("outerGiven", outsideContributor.getGiven());
        assertEquals("outerSuffix", outsideContributor.getSuffix());
        assertEquals("outer@gmail.com", outsideContributor.getEmail());
        assertEquals(182, outsideContributor.getAffiliation().getId().intValue());

        //Corporate Contributor
        contributor = Contributor.getDao().getById("2");
        assertEquals(2, contributor.getId().intValue());
        assertTrue(contributor instanceof CorporateContributor);
        CorporateContributor corpContributor = (CorporateContributor) contributor;
        assertEquals("US Geological Survey Ice Survey Team", corpContributor.getOrganization());
    }

    @Test
    public void getByMap() {
        List<Contributor<?>> contributors = Contributor.getDao().getByMap(null);
        assertEquals(contributorCnt, contributors.size());

        Map<String, Object> filters = new HashMap<>();
        filters.put("id", "1");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("name", "con");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("name", "us");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("ipdsContributorId", 1);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(3, contributors.get(0).getId().intValue());

        filters.clear();
        filters.put("corporation", false);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(2, contributors.size());
        assertEquals(1, contributors.get(0).getId().intValue());
        filters.put("name", "out");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(3, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 1);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(3, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 2);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());


        filters.clear();
        filters.put("corporation", true);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("name", "us");
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 2);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(1, contributors.size());
        assertEquals(2, contributors.get(0).getId().intValue());
        filters.put("ipdsContributorId", 1);
        contributors = Contributor.getDao().getByMap(filters);
        assertEquals(0, contributors.size());
    }

    @Test
    public void notImplemented() {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("prodId", 1);
            Contributor.getDao().getObjectCount(params);
            fail("Was able to get count.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
        try {
            Contributor.getDao().add(new CorporateContributor());
            fail("Was able to add.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
        try {
            Contributor.getDao().update(new CorporateContributor());
            fail("Was able to update.");
        } catch (Exception e) {
            assertEquals("NOT IMPLEMENTED.", e.getMessage());
        }
    }

}
