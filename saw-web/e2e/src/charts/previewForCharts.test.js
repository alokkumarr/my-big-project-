var testDataReader = require('../javascript/testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const login = require('../javascript/pages/loginPage.po');
const analyzePage = require('../javascript/pages/analyzePage.po');
const designModePage = require('../javascript/pages/designModePage.po');
const previewPage = require('../javascript/pages/previewPage.po');
const commonFunctions = require('../javascript/helpers/commonFunctions');
const homePage = require('../javascript/pages/homePage.po');
const protractorConf = require('../../protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
const dataSets = require('../javascript/data/datasets');

describe('Verify preview for charts: previewForCharts.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;
  const yAxisName = 'Double';
  const xAxisName = 'Date';
  const yAxisName2 = 'Long';
  const groupName = 'String';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['PREVIEWCHARTS']['previewChartsDataProvider'], function (data, description) {
    it('should verify preview for '+ description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'PREVIEWCHARTS', dp:'previewChartsDataProvider'}), () => {
      try {

        let chartTyp = data.chartType.split(":")[1];
        login.loginAs(data.user);
        homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);

        let chartName = `e2e ${description} ${(new Date()).toString()}`;
        let chartDescription = `e2e ${description} : description ${(new Date()).toString()}`;

        // Create analysis
        homePage.createAnalysis(metricName, data.chartType);

        //Select fields
        // Wait for field input box.
        commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);

        // Dimension section.
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(xAxisName));
        designModePage.chart.addFieldButton(xAxisName).click();

        // Group by section. i.e. Color by
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(groupName));
        designModePage.chart.addFieldButton(groupName).click();

        // Metric section.
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName));
        designModePage.chart.addFieldButton(yAxisName).click();

        // Size section.
        if (data.chartType === 'chart:bubble') {
          commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(sizeByName));
          designModePage.chart.addFieldButton(sizeByName).click();
        }
        //If Combo then add one more field
        if (data.chartType === 'chart:combo') {
          commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName2));
          designModePage.chart.addFieldButton(yAxisName2).click();
        }

        // Navigate to Preview
        commonFunctions.waitFor.elementToBeClickable(designModePage.previewBtn);
        designModePage.previewBtn.click();

        // Verify axis to be present on Preview Mode
        commonFunctions.waitFor.elementToBePresent(previewPage.axisTitleUpdated(chartTyp, yAxisName, "yaxis"));
        commonFunctions.waitFor.elementToBeVisible(previewPage.axisTitleUpdated(chartTyp, yAxisName, "yaxis"));
        commonFunctions.waitFor.elementToBePresent(previewPage.axisTitleUpdated(chartTyp, xAxisName, "xaxis"));
        commonFunctions.waitFor.elementToBeVisible(previewPage.axisTitleUpdated(chartTyp, xAxisName, "xaxis"));

      }catch (e) {
        console.log(e);
      }
    });
  });
});
