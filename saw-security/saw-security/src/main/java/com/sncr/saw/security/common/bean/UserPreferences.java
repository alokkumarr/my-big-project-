package com.sncr.saw.security.common.bean;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {

   private String userID;
   private String customerID;
   private List<Preference> preferences = new ArrayList<>();

    /**
     * Gets userID
     *
     * @return value of userID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Gets customerID
     *
     * @return value of customerID
     */
    public String getCustomerID() {
        return customerID;
    }

    /**
     * Sets customerID
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    /**
     * Gets preferences
     *
     * @return value of preferences
     */
    public List<Preference> getPreferences() {
        return preferences;
    }

    /**
     * Sets preferences
     */
    public void setPreferences(List<Preference> preferences) {
        this.preferences = preferences;
    }

}
