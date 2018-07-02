/*
  Created by Alex
 */

const login = require('../../javascript/pages/loginPage.po');
const analyzePage = require('../../javascript/pages/analyzePage.po');
const designModePage = require('../../javascript/pages/designModePage.po');
const previewPage = require('../../javascript/pages/previewPage.po');
const commonFunctions = require('../../javascript/helpers/commonFunctions');
const homePage = require('../../javascript/pages/homePage.po');
const using = require('jasmine-data-provider');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
const dataSets = require('../../javascript/data/datasets');

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

  const dataProvider = {
    // 'Combo Chart by admin': {user: 'admin', chartType: 'chart:combo'}, // SAW-4788---disbaled in the UI
    // 'Combo Chart by user': {user: 'userOne', chartType: 'chart:combo'}, // SAW-4744---disbaled in the UI
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'}, // SAW-4793
    'Column Chart by user': {user: 'userOne', chartType: 'chart:column'}, // SAW-4733
    'Bar Chart by admin': {user: 'admin', chartType: 'chart:bar'}, // SAW-4792
    'Bar Chart by user': {user: 'userOne', chartType: 'chart:bar'}, // SAW-4735
    'Stacked Chart by admin': {user: 'admin', chartType: 'chart:stack'}, // SAW-4791
    'Stacked Chart by user': {user: 'userOne', chartType: 'chart:stack'}, // SAW-4737
    'Line Chart by admin': {user: 'admin', chartType: 'chart:line'}, // SAW-4790
    'Line Chart by user': {user: 'userOne', chartType: 'chart:line'}, // SAW-4740
    // 'Area Chart by admin': {user: 'admin', chartType: 'chart:area'}, // SAW-4789 ---disbaled in the UI
    // 'Area Chart by user': {user: 'userOne', chartType: 'chart:area'}, // SAW-4741 ---disbaled in the UI
    'Scatter Plot Chart by admin': {user: 'admin', chartType: 'chart:scatter'}, // SAW-4787
    'Scatter Plot Chart by user': {user: 'userOne', chartType: 'chart:scatter'}, // SAW-4745
    'Bubble Chart by admin': {user: 'admin', chartType: 'chart:bubble'}, // SAW-4786
    'Bubble Chart by user': {user: 'userOne', chartType: 'chart:bubble'} // SAW-4746
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
    it('should verify preview for ' + description, () => {
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
    });
  });
});
