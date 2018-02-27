/*
 Created by Alex
 */

const loginPage = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const protractorConf = require('../../../../conf/protractor.conf');
const using = require('jasmine-data-provider');
const designModePage = require('../../javascript/pages/designModePage.po.js');

describe('Check whether filters throw an error on pivots: pivotFilters.test.js', () => {
  // TODO rollback after development src/main/javascript/app/common/services/toastMessage.service.js

  const metricName = 'MCT TMO Session ES';
  const analysisType = 'table:pivot';
  const dateFieldName = 'Transfer Date';
  const numberFieldName = 'Session Status';
  const stringFieldName = 'Transfer Count';

  const dataProvider = {
    'Date, This Week, Group Interval: not specified, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'This Week'
    },
    'Date, MTD(Month to Date), Group Interval: Year, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Year',
      datePreset: 'MTD(Month to Date)'
    },
    'Date, YTD(Year to Date), Group Interval: Quarter, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Quarter',
      datePreset: 'YTD(Year to Date)'
    },
    'Date, Last Week, Group Interval: Month, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Month',
      datePreset: 'Last Week'
    },
    'Date, Last 2 Weeks, Group Interval: Date, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Date',
      datePreset: 'Last 2 Weeks'
    },
    'Date, Last Month, Group Interval: not specified, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last Month'
    },
    'Date, Last Quarter, Group Interval: not specified, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last Quarter'
    },
    'Date, Last 3 Months, Group Interval: not specified, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last 3 Months'
    },
    'Date, Last 6 Months, Group Interval: not specified, as admin': {
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last 6 Months'
    },
    //TODO add custom for admin
    //TODO add all other types with agg functions


    'Date, This Week, Group Interval: not specified, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'This Week'
    },
    'Date, MTD(Month to Date), Group Interval: Year, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Year',
      datePreset: 'MTD(Month to Date)'
    },
    'Date, YTD(Year to Date), Group Interval: Quarter, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Quarter',
      datePreset: 'YTD(Year to Date)'
    },
    'Date, Last Week, Group Interval: Month, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Month',
      datePreset: 'Last Week'
    },
    'Date, Last 2 Weeks, Group Interval: Date, as user': {
      user: 'user',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Date',
      datePreset: 'Last 2 Weeks'
    },
    'Date, Last Month, Group Interval: not specified, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last Month'
    },
    'Date, Last Quarter, Group Interval: not specified, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last Quarter'
    },
    'Date, Last 3 Months, Group Interval: not specified, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last 3 Months'
    },
    'Date, Last 6 Months, Group Interval: not specified, as user': {
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      datePreset: 'Last 6 Months'
    }
    //TODO add custom for user
    //TODO add all other types with agg functions
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(dataProvider, function (data, description) {
    it('Should add filter to pivot:  ' + description, () => {
      loginPage.loginAs(data.user);
      commonFunctions.waitFor.elementToBeClickable(homePage.cardViewButton);
      homePage.cardViewButton.click();

      // Create Pivot
      homePage.createAnalysis(metricName, analysisType);

      // Add fields (string, date, number)
      designModePage.pivot.addFieldButton(dateFieldName).click();
      designModePage.pivot.addFieldButton(numberFieldName).click();
      designModePage.pivot.addFieldButton(stringFieldName).click();

      // Scenario for dates
      if (data.fieldType === 'date') {

        //Scenario for group intervals
        if (data.groupIntervalSpecified) {
          designModePage.pivot.expandSelectedFieldPropertiesButton(dateFieldName).click();
          designModePage.pivot.groupIntervalDropDown.click();
        } else {

        }

      }

      // Scenario for numbers
      if (data.fieldType === 'number') {

      }

      //Scenario for strings
      if (data.fieldType === 'string') {

      }
    });
  });
});
