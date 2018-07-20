/*
 Created by Alex
 */
var appRoot = require('app-root-path');
const loginPage = require(appRoot + '/src/test/javascript/pages/loginPage.po.js');
const analyzePage = require(appRoot + '/src/test/javascript/pages/analyzePage.po.js');
const commonElementsPage = require(appRoot + '/src/test/javascript/pages/commonElementsPage.po');
const commonFunctions = require(appRoot + '/src/test/javascript/helpers/commonFunctions.js');
const homePage = require(appRoot + '/src/test/javascript/pages/homePage.po');
const protractorConf = require(appRoot + '/conf/protractor.conf');
const using = require('jasmine-data-provider');
const designModePage = require(appRoot + '/src/test/javascript/pages/designModePage.po.js');
const Filter = require(appRoot + '/src/test/javascript/data/filter');
const dataSets = require(appRoot + '/src/test/javascript/data/datasets');

describe('Check whether filters throw an error on pivots: pivotFilters.test.js', () => {
  const metricName = dataSets.pivotChart;
  const analysisType = 'table:pivot';
  const dateFieldName = 'Date';
  const numberFieldName = 'Integer';
  const stringFieldName = 'String';

  const dataProvider = {
    // DATES
    'Date, This Week, Group Interval: not specified, as admin': { // SAW-3473
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'This Week'
    },
    'Date, MTD (Month to Date), Group Interval: Year, as admin': { // SAW-3474
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Year',
      preset: 'MTD (Month to Date)'
    },
    'Date, YTD (Year to Date), Group Interval: Quarter, as admin': { // SAW-3475
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Quarter',
      preset: 'YTD (Year to Date)'
    },
    'Date, Last Week, Group Interval: Month, as admin': { // SAW-3476
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Month',
      preset: 'Last Week'
    },
    'Date, Last 2 Weeks, Group Interval: Date, as admin': { // SAW-3477
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Date',
      preset: 'Last 2 Weeks'
    },
    'Date, Last Month, Group Interval: not specified, as admin': { // SAW-3478
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last Month'
    },
    'Date, Last Quarter, Group Interval: not specified, as admin': { // SAW-3479
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last Quarter'
    },
    'Date, Last 3 Months, Group Interval: not specified, as admin': { // SAW-3480
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last 3 Months'
    },
    'Date, Last 6 Months, Group Interval: not specified, as admin': { // SAW-3481
      user: 'admin',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last 6 Months'
    },
    'Date, This Week, Group Interval: not specified, as user': { // SAW-3473
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'This Week'
    },
    'Date, MTD (Month to Date), Group Interval: Year, as user': { // SAW-3474
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Year',
      preset: 'MTD (Month to Date)'
    },
    'Date, YTD (Year to Date), Group Interval: Quarter, as user': { // SAW-3475
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Quarter',
      preset: 'YTD (Year to Date)'
    },
    'Date, Last Week, Group Interval: Month, as user': { // SAW-3476
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Month',
      preset: 'Last Week'
    },
    'Date, Last 2 Weeks, Group Interval: Date, as user': { // SAW-3477
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: true,
      groupInterval: 'Date',
      preset: 'Last 2 Weeks'
    },
    'Date, Last Month, Group Interval: not specified, as user': { // SAW-3478
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last Month'
    },
    'Date, Last Quarter, Group Interval: not specified, as user': { // SAW-3479
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last Quarter'
    },
    'Date, Last 3 Months, Group Interval: not specified, as user': { // SAW-3480
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last 3 Months'
    },
    'Date, Last 6 Months, Group Interval: not specified, as user': { // SAW-3481
      user: 'userOne',
      fieldType: 'date',
      groupIntervalSpecified: false,
      preset: 'Last 6 Months'
    },

    // STRINGS
    'String, Equals, as admin': { // SAW-3461
      user: 'admin',
      fieldType: 'string',
      operator: 'Equals',
      value: 10
    },
    'String, Not equal, as admin': { // SAW-3462
      user: 'admin',
      fieldType: 'string',
      operator: 'Not equal',
      value: 10
    },
      'String, Is in, as admin': { // SAW-3463
        user: 'admin',
        fieldType: 'string',
        operator: 'Is in',
        value: 10
      },
      'String, Is not in, as admin': { // SAW-3464
        user: 'admin',
        fieldType: 'string',
        operator: 'Is not in',
        value: 10
      },
      'String, Contains, as admin': { // SAW-3465
        user: 'admin',
        fieldType: 'string',
        operator: 'Contains',
        value: 10
      },
      'String, Starts with, as admin': { // SAW-3466
        user: 'admin',
        fieldType: 'string',
        operator: 'Starts with',
        value: 10
      },
      'String, Ends with, as admin': { // SAW-3467
        user: 'admin',
        fieldType: 'string',
        operator: 'Ends with',
        value: 10
      },
      'String, Equals, as user': { // SAW-3461
        user: 'userOne',
        fieldType: 'string',
        operator: 'Equals',
        value: 10
      },
      'String, Not equal, as user': { // SAW-3462
        user: 'userOne',
        fieldType: 'string',
        operator: 'Not equal',
        value: 10
      },
      'String, Is in, as user': { // SAW-3463
        user: 'userOne',
        fieldType: 'string',
        operator: 'Is in',
        value: 10
      },
      'String, Is not in, as user': { // SAW-3464
        user: 'userOne',
        fieldType: 'string',
        operator: 'Is not in',
        value: 10
      },
      'String, Contains, as user': { // SAW-3465
        user: 'userOne',
        fieldType: 'string',
        operator: 'Contains',
        value: 10
      },
      'String, Starts with, as user': { // SAW-3466
        user: 'userOne',
        fieldType: 'string',
        operator: 'Starts with',
        value: 10
      },
      'String, Ends with, as user': { // SAW-3467
        user: 'userOne',
        fieldType: 'string',
        operator: 'Ends with',
        value: 10
      },

      // NUMBERS
      'Number, Greater than, Aggregate function: Average, as admin': { // SAW-3507
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: "AVG",
        operator: 'Greater than',
        value: 10
      },
      'Number, Less than, Aggregate function: Minimum, as admin': { // SAW-3508
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: "MIN",
        operator: 'Less than',
        value: 10
      },
      'Number, Greater than or equal to, Aggregate function: Maximum, as admin': { // SAW-3509
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: "MAX",
        operator: 'Greater than or equal to',
        value: 10
      },
      'Number, Less than or equal to, Aggregate function: Count, as admin': { // SAW-3510
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: "Count",
        operator: 'Less than or equal to',
        value: 10
      },
      'Number, Greater than, Aggregate function: default, as admin': { // SAW-3511
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Greater than',
        value: 10
      },
      'Number, Less than, Aggregate function: default, as admin': { // SAW-3512
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Less than',
        value: 10
      },
      'Number, Greater than ot equal to, Aggregate function: default, as admin': { // SAW-3513
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Greater than or equal to',
        value: 10
      },
      'Number, Less than or equal to, Aggregate function: default, as admin': { // SAW-3514
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Less than or equal to',
        value: 10
      },
      'Number, Equal to, Aggregate function: default, as admin': { // SAW-3515
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Equal to',
        value: 10
      },
      'Number, Not equal to, Aggregate function: default, as admin': { // SAW-3516
        user: 'admin',
        fieldType: 'number',
        aggregateFunction: false,
        operator: 'Not equal to',
        value: 10
      }

    //TODO add between operator for number as admin
    //TODO add custom date for admin
    //TODO add number with between operator as admin

    //TODO add between operator for number as user
    //TODO add custom date for user
    //TODO add number with between operator as user

    //TODO add check for multiple filters. Make it as separate test
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    commonFunctions.logOutByClearingLocalStorage();
  });

  using(dataProvider, function (data, description) {
    it('Should add filter to pivot:  ' + description, () => {
      loginPage.loginAs(data.user);
      commonFunctions.waitFor.elementToBeVisible(homePage.cardViewButton);
      commonFunctions.waitFor.elementToBeClickable(homePage.cardViewButton);
      homePage.cardViewButton.click();

      // Create Pivot
      homePage.createAnalysis(metricName, analysisType);

      // Add fields (string, date, number)
      commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.addFieldButton(dateFieldName));
      designModePage.pivot.addFieldButton(dateFieldName).click();
      commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.addFieldButton(numberFieldName));
      designModePage.pivot.addFieldButton(numberFieldName).click();
      commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.addFieldButton(stringFieldName));
      designModePage.pivot.addFieldButton(stringFieldName).click();

      // Create filter object. Specify type and preset/operator
      const filter = new Filter({
        preset: data.preset,
        operator: data.operator,
        from: data.from,
        to: data.to,
        moreThen: data.moreThen,
        lessThen: data.lessThen
      });

      if (data.fieldType === 'date') {
        filter.columnName = dateFieldName;
      } else if (data.fieldType === 'number') {
        filter.columnName = numberFieldName;
      } else if (data.fieldType === 'string') {
        filter.columnName = stringFieldName;
      }



      // Scenario for group intervals
      if (data.groupIntervalSpecified) {
        commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.expandSelectedFieldPropertiesButton(dateFieldName));
        designModePage.pivot.expandSelectedFieldPropertiesButton(dateFieldName).click();
        commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.groupIntervalDropDown);
        designModePage.pivot.groupIntervalDropDown.click();
        commonFunctions.waitFor.elementToBeClickable(designModePage.pivot.groupIntervalDropDownElement(data.groupInterval));
        designModePage.pivot.groupIntervalDropDownElement(data.groupInterval).click();
      }

      // Scenario for aggregate functions
      if (data.aggregateFunction) {
        commonFunctions.waitFor.elementToBeClickable(designModePage.aggregateFunctionButton("Sum"));
        designModePage.aggregateFunctionButton("Sum").click();
        // Have to add sleep for elements to be rendered. They appear in DOM faster than they can be actually clicked
        commonFunctions.waitFor.elementToBeVisible(designModePage.aggregateFunctionMenuItem(data.aggregateFunction));
        commonFunctions.waitFor.elementToBeClickable(designModePage.aggregateFunctionMenuItem(data.aggregateFunction));
        designModePage.aggregateFunctionMenuItem(data.aggregateFunction).click();
      }

      // Add filter
      const filterWindow = designModePage.filterWindow;
      commonFunctions.waitFor.elementToBeVisible(designModePage.filterBtn);
      commonFunctions.waitFor.elementToBeClickable(designModePage.filterBtn);
      designModePage.filterBtn.click();
      commonFunctions.waitFor.elementToBeClickable(designModePage.filterWindow.addFilter('sample'));
      designModePage.filterWindow.addFilter('sample').click();
      commonFunctions.waitFor.elementToBeClickable(filterWindow.columnDropDown);
      filterWindow.columnDropDown.click();
      commonFunctions.waitFor.elementToBeClickable(filterWindow.columnNameDropDownItem(filter.columnName));
      filterWindow.columnNameDropDownItem(filter.columnName).click();

      // Scenario for dates
      if (data.fieldType === 'date') {
        commonFunctions.waitFor.elementToBeClickable(filterWindow.date.presetDropDown);
        filterWindow.date.presetDropDown.click();
        commonFunctions.waitFor.elementToBeClickable(filterWindow.date.presetDropDownItem(data.preset));
        filterWindow.date.presetDropDownItem(data.preset).click();
      }

      // Scenario for numbers
      if (data.fieldType === 'number') {
        commonFunctions.waitFor.elementToBeClickable(filterWindow.number.operator);
        filterWindow.number.operator.click();
        commonFunctions.waitFor.elementToBeClickable(filterWindow.number.operatorDropDownItem(data.operator));
        filterWindow.number.operatorDropDownItem(data.operator).click();
        commonFunctions.waitFor.elementToBeVisible(filterWindow.number.input);
        filterWindow.number.input.click();
        filterWindow.number.input.clear().sendKeys(data.value);
      }

      // Scenario for strings
      if (data.fieldType === 'string') {
        commonFunctions.waitFor.elementToBeClickable(filterWindow.string.operator);
        filterWindow.string.operator.click();
        commonFunctions.waitFor.elementToBeClickable(filterWindow.string.operatorDropDownItem(data.operator));
        filterWindow.string.operatorDropDownItem(data.operator).click();
        // Select diffrent input for Is in and Is not in operator TODO: we should be consistent
        if (data.operator === 'Is in' || data.operator === 'Is not in') {
          commonFunctions.waitFor.elementToBeVisible(filterWindow.string.isInIsNotInInput);
          filterWindow.string.isInIsNotInInput.clear().sendKeys(data.value);
        } else {
          commonFunctions.waitFor.elementToBeVisible(filterWindow.string.input);
          filterWindow.string.input.clear().sendKeys(data.value);
        }        
      }

      commonFunctions.waitFor.elementToBeClickable(designModePage.applyFiltersBtn);
      designModePage.applyFiltersBtn.click();
      commonElementsPage.ifErrorPrintTextAndFailTest();
    });
  });
});
