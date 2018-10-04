package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.PreferenceRepository;
import com.sncr.saw.security.common.bean.Preference;
import com.sncr.saw.security.common.bean.UserPreferences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PreferenceRepositoryImpl implements PreferenceRepository {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceRepositoryImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Preferences belong to specific user, So Entity type defined as USER.
    // Currently user preference is limited to only one options for default home screen setting for user
    // So , put that value as default preference.
    private final String CONFIG_VAL_OBJ_TYPE = "USER_PREFERENCES";

    @Override
    public UserPreferences upsertPreferences(UserPreferences userPreferences) {
        String createSQL = "INSERT IGNORE INTO CONFIG_VAL(`CONFIG_VAL_CODE`, `CONFIG_VALUE`, `CONFIG_VAL_DESC`, `CONFIG_VAL_OBJ_TYPE`," +
            "`CONFIG_VAL_OBJ_GROUP`, `ACTIVE_STATUS_IND`, `CREATED_DATE` , CREATED_BY ) " +
            "VALUES( ?,?,?,?,?,'1',now(),?)";
         int[][] insertResult = jdbcTemplate.batchUpdate(createSQL, userPreferences.getPreferences(), 1000,
            (ps, preference) -> {
                ps.setString(1,preference.getPreferenceName());
                ps.setString(2,preference.getPreferenceValue());
                ps.setString(3,preference.getPreferenceName());
                ps.setString(4,CONFIG_VAL_OBJ_TYPE);
                ps.setString(5,userPreferences.getUserID());
                ps.setString(6,userPreferences.getUserID());
            });
         logger.trace(insertResult.length + " Preferences created successfully.");
        // update the sql .
        String updateSQL = "UPDATE CONFIG_VAL SET CONFIG_VALUE = ? ,ACTIVE_STATUS_IND = '1', MODIFIED_DATE = now(), MODIFIED_BY =? " +
            " WHERE CONFIG_VAL_CODE = ? AND CONFIG_VAL_OBJ_TYPE=? AND CONFIG_VAL_OBJ_GROUP= ?";
        int[][] updateResult = jdbcTemplate.batchUpdate(updateSQL, userPreferences.getPreferences(), 1000,
            (ps, preference) -> {
                ps.setString(1,preference.getPreferenceValue());
                ps.setString(2,userPreferences.getUserID());
                ps.setString(3,preference.getPreferenceName());
                ps.setString(4,CONFIG_VAL_OBJ_TYPE);
                ps.setString(5,userPreferences.getUserID());
            });
        logger.trace(updateResult.length + " Preferences updated successfully.");
        userPreferences.setMessage("Preferences updated successfully");
        return userPreferences;
    }

    @Override
    public UserPreferences deletePreferences(UserPreferences userPreferences, boolean inactivateAll) {
        if (!inactivateAll) {
            String deleteSql = "UPDATE CONFIG_VAL SET ACTIVE_STATUS_IND = '0', INACTIVATED_DATE = now() , " +
                "INACTIVATED_BY = ? , MODIFIED_DATE = now(), MODIFIED_BY =? " +
                " WHERE CONFIG_VAL_CODE = ? AND CONFIG_VAL_OBJ_TYPE=? AND CONFIG_VALUE= ? "+
                " AND CONFIG_VAL_OBJ_GROUP= ?";
            int[][] deleteResult = jdbcTemplate.batchUpdate(deleteSql, userPreferences.getPreferences(), 1000,
                (ps, preference) -> {
                    ps.setString(1, userPreferences.getUserID());
                    ps.setString(2, userPreferences.getUserID());
                    ps.setString(3, preference.getPreferenceName());
                    ps.setString(4, CONFIG_VAL_OBJ_TYPE);
                    ps.setString(5, preference.getPreferenceValue());
                    ps.setString(6, userPreferences.getUserID());
                });

            logger.trace(deleteResult.length + " Preferences removed successfully.");
            userPreferences.setMessage(deleteResult.length + " Preferences removed successfully");
        }
        else {
            // In case of remove the preferences for configured for any user.
          String inActivateSql = "UPDATE CONFIG_VAL SET ACTIVE_STATUS_IND = '0', INACTIVATED_DATE = now() , " +
                "INACTIVATED_BY = ? , MODIFIED_DATE = now(), MODIFIED_BY =? " +
                " WHERE CONFIG_VAL_CODE = ? AND CONFIG_VAL_OBJ_TYPE=? AND CONFIG_VALUE= ?";
            int[][] Result = jdbcTemplate.batchUpdate(inActivateSql, userPreferences.getPreferences(), 1000,
                (ps, preference) -> {
                    ps.setString(1, userPreferences.getUserID());
                    ps.setString(2, userPreferences.getUserID());
                    ps.setString(3, preference.getPreferenceName());
                    ps.setString(4, CONFIG_VAL_OBJ_TYPE);
                    ps.setString(5, preference.getPreferenceValue());
                });
            logger.trace(Result.length + " Preferences inactivated successfully.");
            userPreferences.setMessage(Result.length + " Preferences inactivated successfully");
        }
        return userPreferences;
    }

    @Override
    public UserPreferences fetchPreferences(String userID, String customerID) {
      String fetchSQL ="SELECT CONFIG_VAL_CODE,CONFIG_VALUE FROM CONFIG_VAL WHERE CONFIG_VAL_OBJ_TYPE =?" +
          " AND CONFIG_VAL_OBJ_GROUP=? AND ACTIVE_STATUS_IND = '1'";
       List<Preference> preferences = jdbcTemplate.query(fetchSQL, ps -> {
            ps.setString(1,CONFIG_VAL_OBJ_TYPE);
            ps.setString(2,userID);
            }, resultSet -> {
            List<Preference> preferenceList = new ArrayList<>();
            while(resultSet.next()) {
                Preference preference = new Preference();
                preference.setPreferenceName(resultSet.getString("CONFIG_VAL_CODE"));
                preference.setPreferenceValue(resultSet.getString("CONFIG_VALUE"));
                preferenceList.add(preference);
            }
            return preferenceList;
        });
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setUserID(userID);
        userPreferences.setCustomerID(customerID);
        userPreferences.setPreferences(preferences);
        return userPreferences;
    }
}
