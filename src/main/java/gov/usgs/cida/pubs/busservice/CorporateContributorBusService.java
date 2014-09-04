package gov.usgs.cida.pubs.busservice;

import gov.usgs.cida.pubs.domain.Contributor;
import javax.validation.Validator;

import gov.usgs.cida.pubs.domain.CorporateContributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class CorporateContributorBusService extends BusService<CorporateContributor> {

	@Autowired
	CorporateContributorBusService(final Validator validator) {
		this.validator = validator;
	}

	@Override
	public CorporateContributor getObject(Integer objectId) {
		CorporateContributor result = null;
		if (null != objectId) {
			result = (CorporateContributor) CorporateContributor.getDao().getById(objectId);
		}
		return result;
	}

	@Override
	@Transactional
	public CorporateContributor updateObject(CorporateContributor object) {
		CorporateContributor result = null;
		if (null != object && null != object.getId()) {
			Integer id = object.getId();
			Contributor<CorporateContributor> castObject = object;
			castObject.setValidationErrors(validator.validate(castObject));
			if (castObject.getValidationErrors().isEmpty()) {
				CorporateContributor.getDao().update(castObject);
				result = (CorporateContributor) CorporateContributor.getDao().getById(id);
			}
		}
		return result;
	}
	
    @Override
    @Transactional
    public CorporateContributor createObject(final CorporateContributor object) {
		CorporateContributor result = object;
        if (null != object) {
			Contributor<CorporateContributor> castObject = object;
            castObject.setValidationErrors(validator.validate(castObject));
            if (castObject.getValidationErrors().isEmpty()) {
                Integer id = CorporateContributor.getDao().add(castObject);
                result = (CorporateContributor) CorporateContributor.getDao().getById(id);
            }
        }
        return result;
    }

}
