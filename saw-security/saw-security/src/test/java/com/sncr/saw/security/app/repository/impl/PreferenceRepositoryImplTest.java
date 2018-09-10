package com.sncr.saw.security.app.repository.impl;

import com.sncr.saw.security.app.repository.PreferenceRepository;
import com.sncr.saw.security.common.bean.Preference;
import com.sncr.saw.security.common.bean.UserPreferences;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreferenceRepositoryImplTest {
    @Autowired
    private PreferenceRepository preferenceRepository;
    private UserPreferences userPreferences = new UserPreferences();

    @Before
    public void setUp() {
      //  preferenceRepository = mock(PreferenceRepositoryImpl.class);
        userPreferences.setUserID("1");
        userPreferences.setCustomerID("1");
        List<Preference> preferenceList = new ArrayList<>();
        Preference preference1 = new Preference();
        preference1.setPreferenceName("defaultURL1");
        preference1.setPreferenceValue("http://localhost/saw/observe/1");
        preferenceList.add(preference1);
        Preference preference2 = new Preference();
        preference2.setPreferenceName("defaultURL2");
        preference2.setPreferenceValue("http://localhost/saw/observe/2");
        preferenceList.add(preference2);
        Preference preference3 = new Preference();
        preference3.setPreferenceName("defaultURL3");
        preference3.setPreferenceValue("http://localhost/saw/observe/3");
        preferenceList.add(preference3);
        Preference preference4 = new Preference();
        preference4.setPreferenceName("defaultURL4");
        preference4.setPreferenceValue("http://localhost/saw/observe/4");
        preferenceList.add(preference4);
        userPreferences.setPreferences(preferenceList);
        when(preferenceRepository.createPreferences(userPreferences)).thenReturn(userPreferences);
        when(preferenceRepository.updatePreferences(userPreferences)).thenReturn(userPreferences);
        when(preferenceRepository.deletePreferences(userPreferences)).thenReturn(userPreferences);
        when(preferenceRepository.fetchPreferences("1","1")).thenReturn(userPreferences);
    }

    @Test
    public void TestAddPreferences(){
        UserPreferences userPreferences1 =
        preferenceRepository.createPreferences(userPreferences);
        Assert.assertEquals(userPreferences.getUserID(),userPreferences1.getUserID());
        Assert.assertEquals(userPreferences.getCustomerID(),userPreferences1.getCustomerID());
        Assert.assertEquals(userPreferences.getPreferences().size(),userPreferences1.getPreferences().size());
    }

    @Test
    public void TestUpdatePreferences(){
        UserPreferences userPreferences1 =
            preferenceRepository.updatePreferences(userPreferences);
        Assert.assertEquals(userPreferences.getUserID(),userPreferences1.getUserID());
        Assert.assertEquals(userPreferences.getCustomerID(),userPreferences1.getCustomerID());
        Assert.assertEquals(userPreferences.getPreferences().size(),userPreferences1.getPreferences().size());
    }

    @Test
    public void TestDeletePreferences(){
        UserPreferences userPreferences1 =
            preferenceRepository.deletePreferences(userPreferences);
        Assert.assertEquals(userPreferences.getUserID(),userPreferences1.getUserID());
        Assert.assertEquals(userPreferences.getCustomerID(),userPreferences1.getCustomerID());
        Assert.assertEquals(userPreferences.getPreferences().size(),userPreferences1.getPreferences().size());
    }

    @Test
    public void TestFetchPreferences(){
        UserPreferences userPreferences1 =
            preferenceRepository.fetchPreferences(userPreferences.getUserID(),userPreferences.getCustomerID());
        Assert.assertEquals(userPreferences.getUserID(),userPreferences1.getUserID());
        Assert.assertEquals(userPreferences.getCustomerID(),userPreferences1.getCustomerID());
        Assert.assertEquals(userPreferences.getPreferences().size(),userPreferences1.getPreferences().size());
    }
}
