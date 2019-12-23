package com.sncr.saw.security.app.repository;

    import com.sncr.saw.security.app.model.DskEligibleFields;
    import com.sncr.saw.security.app.model.DskField;
    import com.sncr.saw.security.app.model.DskFieldsInfo;
    import com.sncr.saw.security.common.bean.Valid;
    import java.util.List;
    import javax.servlet.http.HttpServletRequest;
    import org.springframework.stereotype.Service;

public interface DskEligibleFieldsRepository {

  Valid createDskEligibleFields(DskEligibleFields dskEligibleFields);

  Valid deleteDskEligibleFields(Long custId, Long prodId, String semanticId);

  Valid updateDskFields(Long customerSysId, Long productId, String semanticId, List<DskField> dskFields);

  DskFieldsInfo fetchAllDskEligibleFields(Long customerSysId, Long defaultProdID);
}
