const testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const commonFunctions = require('../../v2/pages/utils/commonFunctions');
const protractorConf = require('../../protractor.conf');
const dataSets = require('../javascript/data/datasets');
const ChartDesignerPage = require('../../v2/pages/ChartDesignerPage');
const LoginPage = require('../../v2/pages/LoginPage');
const AnalyzePage = require('../../v2/pages/AnalyzePage');
const PreviewPage = require('../../v2/pages/PreviewPage');
const logger = require('../../v2/conf/logger')(__filename);
const analyzePagePO = require('../javascript/pages/analyzePage.po.js');
const designModePage = require('../javascript/pages/designModePage.po.js');

describe('Apply filters to chart: applyFiltersToCharts.js', () => {
  const chartDesigner = analyzePagePO.designerDialog.chart;
  const yAxisName = 'Integer';
  const xAxisName = 'String';
  const filterValue = '123';
  const groupName = 'Date';
  const metricName = dataSets.pivotChart;
  const chartType = 'chart:column';
  const analysisType = '';
  const sizeByName = 'Float';
  const yAxisName2 = 'Long';

  beforeAll(function() {
    jasmine.DEFAULT_TIMEOUT_INTERVAL =
      protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function(done) {
    setTimeout(function() {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function(done) {
    setTimeout(function() {
      commonFunctions.clearLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(
    testDataReader.testData['APPLYFILTERSCHARTS'][
      'applyFiltersChartsDataProvider'
    ],
    function(data, description) {
      it(
        'Should apply filter to column chart ' +
          description +
          ' testDataMetaInfo: ' +
          JSON.stringify({
            test: description,
            feature: 'APPLYFILTERSCHARTS',
            dp: 'applyFiltersChartsDataProvider'
          }),
        () => {
          try {
            const loginPage = new LoginPage();
            loginPage.loginAs(data.user, /analyze/);

            const analyzePage = new AnalyzePage();
            analyzePage.clickOnAddAnalysisButton();
            analyzePage.clickOnChartType(data.chartType);
            analyzePage.clickOnNextButton();
            analyzePage.clickOnDataPods(metricName);
            analyzePage.clickOnCreateButton();

            const chartDesignerPage = new ChartDesignerPage();
            chartDesignerPage.searchInputPresent();
            chartDesignerPage.clickOnAttribute(xAxisName, 'Dimension');
            chartDesignerPage.verifySelectedFiledIsPresent(xAxisName);
            chartDesignerPage.clickOnAttribute(yAxisName, 'Metrics');
            chartDesignerPage.verifySelectedFiledIsPresent(yAxisName);

            if (data.chartType === 'chart:bubble') {
              chartDesignerPage.clickOnAttribute(sizeByName, 'Size');
              chartDesignerPage.verifySelectedFiledIsPresent(sizeByName);
              chartDesignerPage.clickOnAttribute(groupName, 'Color By');
              chartDesignerPage.verifySelectedFiledIsPresent(groupName);
            }
            // If Combo then add one more metric field
            if (data.chartType === 'chart:combo') {
              chartDesignerPage.clickOnAttribute(yAxisName2, 'Metrics');
              chartDesignerPage.verifySelectedFiledIsPresent(yAxisName2);
            } else if (data.chartType !== 'chart:bubble') {
              chartDesignerPage.clickOnAttribute(groupName, 'Group By');
              chartDesignerPage.verifySelectedFiledIsPresent(groupName);
            }

            // Apply filters
            const filters = analyzePagePO.filtersDialogUpgraded;
            const filterAC = filters.getFilterAutocomplete(0);
            const fieldName = yAxisName;
            const operator = 'Equal to';
            commonFunctions.waitFor.elementToBeClickable(
              chartDesigner.filterBtn
            );
            chartDesigner.filterBtn.click();
            commonFunctions.clickOnElement(
              designModePage.filterWindow.addFilter('sample')
            );
            filterAC.sendKeys(
              fieldName,
              protractor.Key.DOWN,
              protractor.Key.ENTER
            );
            commonFunctions.waitFor.elementToBeClickable(
              designModePage.filterWindow.number.operator
            );
            designModePage.filterWindow.number.operator.click();
            commonFunctions.waitFor.elementToBeClickable(
              designModePage.filterWindow.number.operatorDropDownItem(operator)
            );
            designModePage.filterWindow.number
              .operatorDropDownItem(operator)
              .click();
            designModePage.filterWindow.numberInputUpgraded.clear();
            designModePage.filterWindow.numberInputUpgraded.sendKeys(
              filterValue
            );
            commonFunctions.waitFor.elementToBeEnabledAndVisible(
              filters.applyBtn
            );
            commonFunctions.waitFor.elementToBeClickable(filters.applyBtn);
            filters.applyBtn.click();

            const filterDisplayed =
              fieldName + ': ' + operator + ' ' + filterValue;
            const appliedFilters = [filterDisplayed];
            chartDesignerPage.verifyAppliedFilters(appliedFilters);
          } catch (e) {
            console.log(e);
          }
        }
      );
    }
  );
});
