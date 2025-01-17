package gov.usgs.cida.pubs.busservice.ext;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import gov.usgs.cida.pubs.busservice.intfc.IBusService;
import gov.usgs.cida.pubs.dao.PersonContributorDao;
import gov.usgs.cida.pubs.domain.Affiliation;
import gov.usgs.cida.pubs.domain.Contributor;
import gov.usgs.cida.pubs.domain.OutsideContributor;
import gov.usgs.cida.pubs.domain.PersonContributor;
import gov.usgs.cida.pubs.domain.PublicationContributor;
import gov.usgs.cida.pubs.domain.UsgsContributor;

@Service
public class ExtPublicationContributorService {
	private static final Logger LOG = LoggerFactory.getLogger(ExtPublicationContributorService.class);

	private final ExtAffiliationBusService extAffiliationBusService;
	private final IBusService<PersonContributor<?>> personContributorBusService;

	@Autowired
	public ExtPublicationContributorService (
			ExtAffiliationBusService extAffiliationBusService,
			@Qualifier("personContributorBusService") IBusService<PersonContributor<?>> personContributorBusService
			) {
		this.extAffiliationBusService = extAffiliationBusService;
		this.personContributorBusService = personContributorBusService;
	}

	public void processPublicationContributors(Collection<PublicationContributor<?>> publicationContributors) {
		if (!publicationContributors.isEmpty()) {
			for (PublicationContributor<?> publicationContributor : publicationContributors) {
				publicationContributor = processPublicationContributor(publicationContributor);
			}
		}
	}

	protected PublicationContributor<?> processPublicationContributor(PublicationContributor<?> publicationContributor) {
		if (null != publicationContributor.getContributor()) {
			if (publicationContributor.getContributor() instanceof UsgsContributor) {
				publicationContributor.setContributor(processUsgsContributor((UsgsContributor) publicationContributor.getContributor()));
			} else {
				publicationContributor.setContributor(processOutsideContributor((OutsideContributor) publicationContributor.getContributor()));
			}
		}
		return publicationContributor;
	}

	protected UsgsContributor processUsgsContributor(UsgsContributor contributor) {
		UsgsContributor persistedContributor = null;
		if (null != contributor.getOrcid()) {
			persistedContributor = getUsgsContributorByOrcid(contributor.getOrcid());
		}
		if (null == persistedContributor) {
			persistedContributor = createUsgsContributor(contributor);
		}
		return persistedContributor;
	}

	protected UsgsContributor getUsgsContributorByOrcid(String orcid) {
		UsgsContributor filter = new UsgsContributor();
		filter.setOrcid(orcid);
		List<Contributor<?>> contributors = UsgsContributor.getDao().getByPreferred(filter);
		UsgsContributor contributor = null;
		if (contributors.isEmpty()) {
			LOG.debug("No UsgsContributors found for ORCID: {}", orcid);
		} else {
			if (contributors.size() > 1) {
				LOG.warn("Multiple UsgsContributors found for ORCID: {}", orcid);
			}
			contributor = (UsgsContributor) contributors.get(0);
		}
		return contributor;
	}

	protected UsgsContributor createUsgsContributor(UsgsContributor contributor) {
		Set<Affiliation<? extends Affiliation<?>>> affiliations = extAffiliationBusService.processAffiliations(contributor.getAffiliations());
		contributor.setAffiliations(affiliations);

		return (UsgsContributor) personContributorBusService.createObject(contributor);
	}

	protected OutsideContributor processOutsideContributor(OutsideContributor contributor) {
		OutsideContributor persistedContributor = null;
		if (null != contributor.getOrcid()) {
			persistedContributor = getOutsideContributorByOrcid(contributor.getOrcid());
		}
		if (null == persistedContributor && null != contributor.getFamily() && null != contributor.getGiven()) {
			persistedContributor = getByName(contributor.getFamily(), contributor.getGiven());
		}
		if (null == persistedContributor) {
			persistedContributor = createOutsideContributor(contributor);
		}
		return persistedContributor;
	}

	protected OutsideContributor getOutsideContributorByOrcid(String orcid) {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PersonContributorDao.USGS, false);
		filters.put(PersonContributorDao.ORCID, new String[] {orcid});
		List<Contributor<?>> contributors = OutsideContributor.getDao().getByMap(filters);
		OutsideContributor contributor = null;
		if (contributors.isEmpty()) {
			LOG.debug("No OutsideContributors found for ORCID: {}", orcid);
		} else {
			if (contributors.size() > 1) {
				LOG.warn("Multiple OutsideContributors found for ORCID: {}", orcid);
			}
			contributor = (OutsideContributor) contributors.get(0);
		}
		return contributor;
	}

	protected OutsideContributor getByName(String family, String given) {
		Map<String, Object> filters = new HashMap<>();
		filters.put(PersonContributorDao.FAMILY, new String[]{family});
		filters.put(PersonContributorDao.GIVEN, new String[]{given});
		filters.put(PersonContributorDao.USGS, false);
		List<Contributor<?>> contributors = OutsideContributor.getDao().getByMap(filters);
		OutsideContributor contributor = null;
		if (contributors.isEmpty()) {
			LOG.debug("No OutsideContributors found for name: {}, {}",family, given);
		} else {
			if (contributors.size() > 1) {
				LOG.warn("Multiple OutsideContributors found for name: {}, {}",family, given);
			}
			contributor = (OutsideContributor) contributors.get(0);
		}
		return contributor;
	}

	protected OutsideContributor createOutsideContributor(OutsideContributor contributor) {
		Set<Affiliation<? extends Affiliation<?>>> affiliations = extAffiliationBusService.processAffiliations(contributor.getAffiliations());
		contributor.setAffiliations(affiliations);

		return (OutsideContributor) personContributorBusService.createObject(contributor);
	}
}
