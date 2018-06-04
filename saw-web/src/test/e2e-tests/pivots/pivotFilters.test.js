/*
 Created by Alex
 */

const loginPage = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const commonElementsPage = require('../../javascript/pages/commonElementsPage.po');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const protractorConf = require('../../../../conf/protractor.conf');
const using = require('jasmine-data-provider');
const designModePage = require('../../javascript/pages/designModePage.po.js');
const Filter = require('../../javascript/data/filter');

describe('Check whether filters throw an error on pivots: pivotFilters.test.js', () => {
  const metricName = 'MCT TMO Session ES';
  const analysisType = 'table:pivot';
  const dateFieldName = 'Transfer Date';
  const numberFieldName = 'Transfer Count';
  const stringFieldName = 'Session Status';

  const dataProvider = {
    // DATES
    /*'Date, This Week, Group Interval: not specified, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'This Week'
     },
     'Date, MTD (Month to Date), Group Interval: Year, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Year',
     preset: 'MTD (Month to Date)'
     },
     'Date, YTD (Year to Date), Group Interval: Quarter, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Quarter',
     preset: 'YTD (Year to Date)'
     },
     'Date, Last Week, Group Interval: Month, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Month',
     preset: 'Last Week'
     },
     'Date, Last 2 Weeks, Group Interval: Date, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Date',
     preset: 'Last 2 Weeks'
     },
     'Date, Last Month, Group Interval: not specified, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last Month'
     },
     'Date, Last Quarter, Group Interval: not specified, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last Quarter'
     },
     'Date, Last 3 Months, Group Interval: not specified, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last 3 Months'
     },
     'Date, Last 6 Months, Group Interval: not specified, as admin': {
     user: 'admin',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last 6 Months'
     },
     'Date, This Week, Group Interval: not specified, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'This Week'
     },
     'Date, MTD (Month to Date), Group Interval: Year, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Year',
     preset: 'MTD (Month to Date)'
     },
     'Date, YTD (Year to Date), Group Interval: Quarter, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Quarter',
     preset: 'YTD (Year to Date)'
     },
     'Date, Last Week, Group Interval: Month, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Month',
     preset: 'Last Week'
     },
     'Date, Last 2 Weeks, Group Interval: Date, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: true,
     groupInterval: 'Date',
     preset: 'Last 2 Weeks'
     },
     'Date, Last Month, Group Interval: not specified, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last Month'
     },
     'Date, Last Quarter, Group Interval: not specified, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last Quarter'
     },
     'Date, Last 3 Months, Group Interval: not specified, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last 3 Months'
     },
     'Date, Last 6 Months, Group Interval: not specified, as user': {
     user: 'userOne',
     fieldType: 'date',
     groupIntervalSpecified: false,
     preset: 'Last 6 Months'
     }*/

    // STRINGS
    /*'String, EQUALS, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'EQUALS',
     value: 10
     },
     'String, NOT_EQUAL, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'NOT_EQUAL',
     value: 10
     },
     'String, IS_IN, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'IS_IN',
     value: 10
     },
     'String, IS_NOT_IN, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'IS_NOT_IN',
     value: 10
     },
     'String, CONTAINS, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'CONTAINS',
     value: 10
     },
     'String, STARTS_WITH, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'STARTS_WITH',
     value: 10
     },
     'String, ENDS_WITH, as admin': {
     user: 'admin',
     fieldType: 'string',
     operator: 'ENDS_WITH',
     value: 10
     },
     'String, EQUALS, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'EQUALS',
     value: 10
     },
     'String, NOT_EQUAL, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'NOT_EQUAL',
     value: 10
     },
     'String, IS_IN, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'IS_IN',
     value: 10
     },
     'String, IS_NOT_IN, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'IS_NOT_IN',
     value: 10
     },
     'String, CONTAINS, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'CONTAINS',
     value: 10
     },
     'String, STARTS_WITH, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'STARTS_WITH',
     value: 10
     },
     'String, ENDS_WITH, as user': {
     user: 'userOne',
     fieldType: 'string',
     operator: 'ENDS_WITH',
     value: 10
     },*/

    // NUMBERS
    'Number, Greater than, Aggregate function: Average, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: "AVG",
      operator: 'Greater than',
      value: 10
    },
    'Number, Less than, Aggregate function: Minimum, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: "MIN",
      operator: 'Less than',
      value: 10
    },
    'Number, Greater than or equal to, Aggregate function: Maximum, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: "MAX",
      operator: 'Greater than or equal to',
      value: 10
    },
    'Number, Less than or equal to, Aggregate function: Count, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: "Count",
      operator: 'Less than or equal to',
      value: 10
    },
    'Number, Greater than, Aggregate function: default, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: false,
      operator: 'Greater than',
      value: 10
    },
    'Number, Less than, Aggregate function: default, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: false,
      operator: 'Less than',
      value: 10
    },
    'Number, Greater than ot equal to, Aggregate function: default, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: false,
      operator: 'Greater than or equal to',
      value: 10
    },
    'Number, Less than or equal to, Aggregate function: default, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: false,
      operator: 'Less than or equal to',
      value: 10
    },
    'Number, Equal to, Aggregate function: default, as admin': {
      user: 'admin',
      fieldType: 'number',
      aggregateFunction: false,
      operator: 'Equal to',
      value: 10
    },
    'Number, Not equal to, Aggregate function: default, as admin': {
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
      //expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
     // analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    commonFunctions.logOutByClearingLocalStorage();
  });

  using(dataProvider, function (data, description) {
    it('Should add filter to pivot:  ' + description, () => {
      loginPage.loginAsV2(data.user);

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
        // TODO rewrite since there is layout error
        commonFunctions.waitFor.elementToBeVisible(filterWindow.string.input);
        filterWindow.string.input.click();
        filterWindow.string.input.clear().sendKeys(data.value);
      }

      commonFunctions.waitFor.elementToBeClickable(designModePage.applyFiltersBtn);
      designModePage.applyFiltersBtn.click();
      commonElementsPage.ifErrorPrintTextAndFailTest();
    });
  });
});
