/*
  Created by Alex
 */

const login = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const executedAnalysisPage = require('../../javascript/pages/savedAlaysisPage.po');
const using = require('jasmine-data-provider');

describe('Create and delete charts: createAndDeleteCharts.test.js', () => {
  const defaultCategory = 'AT Privileges Category DO NOT TOUCH';
  const categoryName = 'AT Analysis Category DO NOT TOUCH';
  const subCategoryName = 'AT Creating Analysis DO NOT TOUCH';
  const chartDesigner = analyzePage.designerDialog.chart;
  const chartName = `e2e chart ${(new Date()).toString()}`;
  const chartDescription = 'e2e test chart description';
  let xAxisName = 'Source Manufacturer';
  let yAxisName = 'Available MB';
  const yAxisName2 = 'Available Items';
  let groupName = 'Source OS';
  let metric = 'MCT TMO Session ES';
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
    jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000000;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, 1000)
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, 1000)
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(dataProvider, function (data, description) {
    it('should create and delete ' + description, () => {
      if (data.chartType === 'chart:bubble') {
        metric = 'PTT Subscr Detail';
        yAxisName = 'Call Billed Unit';
        xAxisName = 'Account Segment';
        groupName = 'Account Name';
      }

      login.loginAs(data.user);
      navigateToSubCategory();

      //Create analysis
      analyzePage.analysisElems.addAnalysisBtn.click();
      const newDialog = analyzePage.newDialog;
      newDialog.getMetric(metric).click();
      newDialog.getMethod(data.chartType).click();
      newDialog.createBtn.click();

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
      chartDesigner.getXRadio(xAxisName).click();
      commonFunctions.waitFor.elementToBeClickableAndClick(y);
      chartDesigner.getGroupRadio(groupName).click();

      //If Combo then add one more field
      if (data.chartType === 'chart:combo') {
        const y2 = chartDesigner.getYCheckBox(yAxisName2);
        commonFunctions.waitFor.elementToBeClickableAndClick(y2);
      }

      //Refresh
      chartDesigner.refreshBtn.click();

      //Save
      const save = analyzePage.saveDialog;
      const designer = analyzePage.designerDialog;
      commonFunctions.waitFor.elementToBeClickableAndClick(designer.saveBtn);

      commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
      save.nameInput.clear().sendKeys(chartName);
      save.descriptionInput.clear().sendKeys(chartDescription);
      save.saveBtn.click();
      const createdAnalysis = analyzePage.main.getCardTitle(chartName);

      //Change to Card View
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

      //Verify if created appeared in list
      commonFunctions.waitFor.elementToBePresent(createdAnalysis)
        .then(() => expect(createdAnalysis.isPresent()).toBe(true));

      //Verify chart type on home page
      analyzePage.main.getCardTypeByName(chartName).then(actualChartType =>
        expect(actualChartType).toEqual(data.chartType,
          "Chart type on Analyze Page expected to be " + data.chartType + ", but was " + actualChartType));

      //Delete created chart
      const main = analyzePage.main;
      const cards = main.getAnalysisCards(chartName);
      cards.count().then(count => {
        main.doAnalysisAction(chartName, 'delete');
        commonFunctions.waitFor.elementToBeClickableAndClick(main.confirmDeleteBtn);

        commonFunctions.waitFor.cardsCountToUpdate(cards, count);

        //Expect to be deleted
        expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
      });
    });

    // Navigates to specific category where analysis creation should happen
    const navigateToSubCategory = () => {
      //Collapse default category
      homePage.expandedCategory(defaultCategory).click();

      //Navigate to Category/Sub-category
      const collapsedCategory = homePage.collapsedCategory(categoryName);
      const subCategory = homePage.subCategory(subCategoryName);
      commonFunctions.waitFor.elementToBeClickableAndClick(collapsedCategory);
      commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);
    };
  });
});
