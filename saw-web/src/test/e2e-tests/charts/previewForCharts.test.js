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
  const xAxisName = 'Integer';
  const yAxisName2 = 'Long';
  const groupName = 'Date';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';

  const dataProvider = {
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'}, // SAW-4793
    'Column Chart by user': {user: 'userOne', chartType: 'chart:column'}, // SAW-4733
    'Bar Chart by admin': {user: 'admin', chartType: 'chart:bar'}, // SAW-4792
    'Bar Chart by user': {user: 'userOne', chartType: 'chart:bar'}, // SAW-4735
    'Stacked Chart by admin': {user: 'admin', chartType: 'chart:stack'}, // SAW-4791
    'Stacked Chart by user': {user: 'userOne', chartType: 'chart:stack'}, // SAW-4737
    'Line Chart by admin': {user: 'admin', chartType: 'chart:line'}, // SAW-4790
    'Line Chart by user': {user: 'userOne', chartType: 'chart:line'}, // SAW-4740
    'Area Chart by admin': {user: 'admin', chartType: 'chart:area'}, // SAW-4789
    'Area Chart by user': {user: 'userOne', chartType: 'chart:area'}, // SAW-4741
    'Combo Chart by admin': {user: 'admin', chartType: 'chart:combo'}, // SAW-4788
    'Combo Chart by user': {user: 'userOne', chartType: 'chart:combo'}, // SAW-4744
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
