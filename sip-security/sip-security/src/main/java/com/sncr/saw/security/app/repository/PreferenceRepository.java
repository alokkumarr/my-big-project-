package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.UserPreferences;
import com.sncr.saw.security.common.bean.Valid;
import com.sncr.saw.security.common.bean.repo.ConfigValDetails;

public interface PreferenceRepository {
   UserPreferences upsertPreferences(UserPreferences userPreferences);

    UserPreferences deletePreferences(UserPreferences userPreferences, boolean inactivateAll);

    UserPreferences fetchPreferences(String userID, String CustID);

    ConfigValDetails getConfigDetails(String customerCode);

    Valid addConfigVal(ConfigValDetails configValDetails);

    Valid updateConfigVal(String customerCode, int activeStatusInd);
}
