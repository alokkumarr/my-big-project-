package com.sncr.saw.security.app.repository;

    import com.sncr.saw.security.app.model.DskEligibleFields;
    import com.sncr.saw.security.common.bean.Valid;
    import org.springframework.stereotype.Service;

public interface DskEligibleFieldsRepository {

  Valid createDskEligibleFields(DskEligibleFields dskEligibleFields);

  Valid deleteDskEligibleFields(Long custId, Long prodId, String semanticId);

}
