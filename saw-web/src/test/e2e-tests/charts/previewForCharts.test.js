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

describe('Verify preview for charts: previewForCharts.test.js', () => {
  const defaultCategory = 'AT Privileges Category DO NOT TOUCH';
  const categoryName = 'AT Analysis Category DO NOT TOUCH';
  const subCategoryName = 'AT Creating Analysis DO NOT TOUCH';
  const chartDesigner = analyzePage.designerDialog.chart;
  let xAxisName = 'Source Manufacturer';
  let yAxisName = 'Available MB';
  const yAxisName2 = 'Available Items';
  let groupName = 'Source OS';
  let metricName = 'MCT TMO Session ES';
  const sizeByName = 'Activated No Usage Subscriber Count';

  const dataProvider = {
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'},
    'Column Chart by user': {user: 'userOne', chartType: 'chart:column'},
    'Bar Chart by admin': {user: 'admin', chartType: 'chart:bar'},
    'Bar Chart by user': {user: 'userOne', chartType: 'chart:bar'},
    'Stacked Chart by admin': {user: 'admin', chartType: 'chart:stack'},
    'Stacked Chart by user': {user: 'userOne', chartType: 'chart:stack'},
    'Line Chart by admin': {user: 'admin', chartType: 'chart:line'},
    'Line Chart by user': {user: 'userOne', chartType: 'chart:line'},
    'Area Chart by admin': {user: 'admin', chartType: 'chart:area'},
    'Area Chart by user': {user: 'userOne', chartType: 'chart:area'},
    'Combo Chart by admin': {user: 'admin', chartType: 'chart:combo'},
    'Combo Chart by user': {user: 'userOne', chartType: 'chart:combo'},
    'Scatter Plot Chart by admin': {user: 'admin', chartType: 'chart:scatter'},
    'Scatter Plot Chart by user': {user: 'userOne', chartType: 'chart:scatter'},
    'Bubble Chart by admin': {user: 'admin', chartType: 'chart:bubble'},
    'Bubble Chart by user': {user: 'userOne', chartType: 'chart:bubble'}
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
      if (data.chartType === 'chart:bubble') {
        metricName = 'PTT Subscr Detail';
        yAxisName = 'Activated Active Subscriber Count';
        xAxisName = 'Activated Inactive Subscriber Count';
        groupName = 'Apps Version';
      }

      let chartTyp = data.chartType.split(":")[1];

      login.loginAs(data.user);
      homePage.mainMenuExpandBtn.click();
      homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);
      homePage.mainMenuCollapseBtn.click();
      //Create analysis
      homePage.createAnalysis(metricName, data.chartType);

      // Select fields
      // Wait for field input box.
      commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);
      // Metric section.
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName));
      designModePage.chart.addFieldButton(yAxisName).click();
      // Dimension section.
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(xAxisName));
      designModePage.chart.addFieldButton(xAxisName).click();
      // Size section.
      if (data.chartType === 'chart:bubble') {
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(sizeByName));
        designModePage.chart.addFieldButton(sizeByName).click();
      }
      // Group by section. i.e. Color by
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(groupName));
      designModePage.chart.addFieldButton(groupName).click();

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
