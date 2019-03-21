const testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../v2/pages/utils/commonFunctions');
const protractorConf = require('../../protractor.conf');
const dataSets = require('../javascript/data/datasets');
const ChartDesignerPage = require('../../v2/pages/ChartDesignerPage');
const LoginPage = require('../../v2/pages/LoginPage');
const AnalyzePage = require('../../v2/pages/AnalyzePage');
const logger = require('../../v2/conf/logger')(__filename);
const designModePage = require('../javascript/pages/designModePage.po.js');
const Filter = require('../javascript/data/filter');
const commonElementsPage = require('../javascript/pages/commonElementsPage.po');

describe('Check whether filters throw an error on pivots: pivotFilters.test.js', () => {
  const metricName = dataSets.pivotChart;
  const analysisType = 'table:pivot';
  const dateFieldName = 'Date';
  const numberFieldName = 'Integer';
  const stringFieldName = 'String';
  beforeAll(() => {
    logger.info('Starting pivots/pivotFilters.test.js.....');
    protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(done => {
    setTimeout(() => {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(done => {
    setTimeout(() => {
      // Logout by clearing the storage
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['PIVOTFILTER']['pivotFilterDataProvider'],
    function(data, description) {
      it(
        'Should add filter to pivot:  ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'PIVOTFILTER',
            dp: 'pivotFilterDataProvider'
          }),
        () => {
          try {
            const loginPage = new LoginPage();
            loginPage.loginAs(data.user, /analyze/);

            const analyzePage = new AnalyzePage();
            analyzePage.clickOnAddAnalysisButton();
            analyzePage.clickOnAnalysisType(analysisType);
            analyzePage.clickOnNextButton();
            analyzePage.clickOnDataPods(metricName);
            analyzePage.clickOnCreateButton();

            const chartDesignerPage = new ChartDesignerPage();
            chartDesignerPage.searchInputPresent();
            chartDesignerPage.clickOnAttribute(dateFieldName, 'Row');
            chartDesignerPage.clickOnAttribute(numberFieldName, 'Data');
            chartDesignerPage.clickOnAttribute(stringFieldName, 'Row');

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
              chartDesignerPage.clickOnDataOptions(dateFieldName);
              browser.sleep(2000);

              // select the group by interval value e.g. year/month day etc
              chartDesignerPage.clickOnGroupBySelector();
              chartDesignerPage.clickOnGroupByOption(data.groupInterval);
            }

            // Scenario for aggregate functions
            if (data.aggregateFunction) {
              chartDesignerPage.clickOnDataOptions(numberFieldName);
              browser.sleep(2000); // Needed to avoid element not loaded/active
              chartDesignerPage.clickOnAggregateOption('sum');
              browser.sleep(1000);
              chartDesignerPage.clickOnAggregateOption(data.aggregateFunction);
            }

            // Add filter
            const filterWindow = designModePage.filterWindow;
            chartDesignerPage.clickOnFilterButton();
            commonFunctions.waitFor.elementToBeClickable(
              designModePage.filterWindow.addFilter('sample')
            );
            designModePage.filterWindow.addFilter('sample').click();
            commonFunctions.waitFor.elementToBeClickable(
              filterWindow.columnDropDown
            );
            filterWindow.columnDropDown.click();
            commonFunctions.waitFor.elementToBeClickable(
              filterWindow.columnNameDropDownItem(filter.columnName)
            );
            filterWindow.columnNameDropDownItem(filter.columnName).click();

            // Scenario for dates
            if (data.fieldType === 'date') {
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.date.presetDropDown
              );
              filterWindow.date.presetDropDown.click();
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.date.presetDropDownItem(data.preset)
              );
              filterWindow.date.presetDropDownItem(data.preset).click();
            }

            // Scenario for numbers
            if (data.fieldType === 'number') {
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.number.operator
              );
              filterWindow.number.operator.click();
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.number.operatorDropDownItem(data.operator)
              );
              filterWindow.number.operatorDropDownItem(data.operator).click();
              commonFunctions.waitFor.elementToBeVisible(
                filterWindow.number.input
              );
              filterWindow.number.input.click();
              filterWindow.number.input.clear().sendKeys(data.value);
            }

            // Scenario for strings
            if (data.fieldType === 'string') {
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.string.operator
              );
              filterWindow.string.operator.click();
              commonFunctions.waitFor.elementToBeClickable(
                filterWindow.string.operatorDropDownItem(data.operator)
              );
              filterWindow.string.operatorDropDownItem(data.operator).click();
              // Select different input for Is in and Is not in operator TODO: we should be consistent
              if (data.operator === 'Is in' || data.operator === 'Is not in') {
                commonFunctions.waitFor.elementToBeVisible(
                  filterWindow.string.isInIsNotInInput
                );
                filterWindow.string.isInIsNotInInput
                  .clear()
                  .sendKeys(data.value);
              } else {
                commonFunctions.waitFor.elementToBeVisible(
                  filterWindow.string.input
                );
                filterWindow.string.input.clear().sendKeys(data.value);
              }
            }

            commonFunctions.waitFor.elementToBeClickable(
              designModePage.applyFiltersBtn
            );
            designModePage.applyFiltersBtn.click();
            commonElementsPage.ifErrorPrintTextAndFailTest();
          } catch (e) {
            console.log(e);
          }
        }
      );
    }
  );
});
