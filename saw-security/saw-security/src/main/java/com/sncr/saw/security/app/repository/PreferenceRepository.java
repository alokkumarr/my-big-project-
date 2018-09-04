package com.sncr.saw.security.app.repository;

import com.sncr.saw.security.common.bean.UserPreferences;

public interface PreferenceRepository {
   UserPreferences createPreferences(UserPreferences userPreferences);
   UserPreferences updatePreferences(UserPreferences userPreferences);
   UserPreferences deletePreferences(UserPreferences userPreferences);
   UserPreferences fetchPreferences(String userID,String CustID);
}
