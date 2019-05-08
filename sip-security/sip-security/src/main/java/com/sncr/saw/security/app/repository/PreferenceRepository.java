package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.UserPreferences;

public interface PreferenceRepository {
   UserPreferences upsertPreferences(UserPreferences userPreferences);

    UserPreferences deletePreferences(UserPreferences userPreferences, boolean inactivateAll);

    UserPreferences fetchPreferences(String userID, String CustID);
}
