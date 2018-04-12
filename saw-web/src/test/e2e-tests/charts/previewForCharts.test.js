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
  const sizeByName = 'Activated Active Subscriber Count';

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
    it('should verify preview for ' + description, () => {
      if (data.chartType === 'chart:bubble') {
        metricName = 'PTT Subscr Detail';
        yAxisName = 'Call Billed Unit';
        xAxisName = 'Account Segment';
        groupName = 'Account Name';
      }

      login.loginAs(data.user);
      homePage.mainMenuExpandBtn.click();
      navigateToSubCategory();
      homePage.mainMenuCollapseBtn.click();

      //Create analysis
      homePage.createAnalysis(metricName, data.chartType);

      //Select fields
      if (data.chartType === 'chart:bubble') {       // if chart is bubble then select Y radio instead of checkbox
        y = chartDesigner.getYRadio(yAxisName);

        // Also select Color by
        const sizeBy = chartDesigner.getZRadio(sizeByName);
        commonFunctions.waitFor.elementToBeClickableAndClick(sizeBy);
      } else if (data.chartType === 'chart:stack') {  // if chart is stacked - select Y radio instead of checkbox
        y = chartDesigner.getYRadio(yAxisName);
      } else {
        y = chartDesigner.getYCheckBox(yAxisName);    // for the rest of the cases - select Y checkbox
      }
      commonFunctions.waitFor.elementToBeClickableAndClick(chartDesigner.getXRadio(xAxisName));
      commonFunctions.waitFor.elementToBeClickableAndClick(y);
      commonFunctions.waitFor.elementToBeClickableAndClick(chartDesigner.getGroupRadio(groupName));

      //If Combo then add one more field
      if (data.chartType === 'chart:combo') {
        const y2 = chartDesigner.getYCheckBox(yAxisName2);
        commonFunctions.waitFor.elementToBeClickableAndClick(y2);
      }

      //Refresh
      commonFunctions.waitFor.elementToBeClickableAndClick(chartDesigner.refreshBtn);

      // Navigate to Preview
      commonFunctions.waitFor.elementToBeClickableAndClick(designModePage.previewBtn);

      // Verify axis to be present on Preview Mode
      commonFunctions.waitFor.elementToBePresent(previewPage.axisTitle(yAxisName));
      commonFunctions.waitFor.elementToBePresent(previewPage.axisTitle(xAxisName));
    });

    // Navigates to specific category where analysis creation should happen
    const navigateToSubCategory = () => {
      //Collapse default category
      commonFunctions.waitFor.elementToBeClickableAndClick(homePage.expandedCategory(defaultCategory));

      //Navigate to Category/Sub-category
      const collapsedCategory = homePage.collapsedCategory(categoryName);
      const subCategory = homePage.subCategory(subCategoryName);
      commonFunctions.waitFor.elementToBeClickableAndClick(collapsedCategory);
      commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);
    };
  });
});
