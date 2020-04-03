const using = require('jasmine-data-provider');
const testDataReader = require('../../testdata/testDataReader.js');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const dataSets = require('../../helpers/data-generation/datasets');
const commonFunctions = require('../../pages/utils/commonFunctions');
const FilterOptions = require('../../helpers/FilterOptions');

const LoginPage = require('../../pages/LoginPage');
const AnalyzePage = require('../../pages/AnalyzePage');
const ChartDesignerPage = require('../../pages/ChartDesignerPage');
const PreviewPage = require('../../pages/PreviewPage');
const Header = require('../../pages/components/Header');

describe('Executing pivot filter tests cases from pivots/pivotFilters.test.js', () => {
  const metricName = dataSets.pivotChart;
  const analysisType = 'table:pivot';
  const dateFieldName = 'Date';
  const numberFieldName = 'Integer';
  const numberFieldNameDataOption = 'SUM(Integer)';
  const stringFieldName = 'String';
  const fieldName = 'field';
  beforeAll(() => {
    logger.info('Starting pivots/pivotFilters.test.js.....');
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.timeoutInterval;
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
    testDataReader.testData['PIVOT']['pivotFilters']
      ? testDataReader.testData['PIVOT']['pivotFilters']
      : {},
    (data, id) => {
      it(`${id}:${data.description}`, () => {
        logger.info(`Executing test case with id: ${id}`);
        const loginPage = new LoginPage();
        loginPage.loginAs(data.user, /analyze/);
        browser.sleep(2000);
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
        const filter = new FilterOptions({
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
          //chartDesignerPage.clickOnDataOptions(dateFieldName);
          browser.sleep(2000);

          // select the group by interval value e.g. year/month day etc
          chartDesignerPage.clickOnGroupBySelector();
          chartDesignerPage.clickOnGroupByOption(data.groupInterval);
        }

        // Scenario for aggregate functions
        if (data.aggregateFunction) {
          chartDesignerPage.clickOnDataOptions(numberFieldNameDataOption);
          browser.sleep(2000); // Needed to avoid element not loaded/active
          chartDesignerPage.clickOnAggregateOption('sum');
          browser.sleep(1000);
          chartDesignerPage.clickOnAggregateOption(data.aggregateFunction);
        }

        chartDesignerPage.clickOnFilterButton();
        chartDesignerPage.clickOnAddFilterButtonByField(fieldName);
        //chartDesignerPage.clickOnAddFilterButtonByTableName();
        chartDesignerPage.clickOnColumnInput();
        chartDesignerPage.clickOnColumnDropDown(filter.columnName);

        // Scenario for dates
        if (data.fieldType === 'date') {
          chartDesignerPage.selectPreset(data.preset);
        }
        // Scenario for numbers
        if (data.fieldType === 'number') {
          chartDesignerPage.selectNumberOperatorAndValue(
            data.operator,
            data.value
          );
        }

        // Scenario for strings
        if (data.fieldType === 'string') {
          chartDesignerPage.selectStringOperatorAndValue(
            data.operator,
            data.value
          );
        }
        chartDesignerPage.clickOnApplyFilterButton();
      }).result.testInfo = {
        testId: id,
        data: data,
        feature: 'PIVOT',
        dataProvider: 'pivotFilters'
      };
    }
  );
});
