/*
 Created by Alex
 */

const login = require('../../javascript/pages/loginPage.po.js');
const analyzePage = require('../../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const savedAlaysisPage = require('../../javascript/pages/savedAlaysisPage.po');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const using = require('jasmine-data-provider');

describe('Create and delete charts: createAndDeleteCharts.test.js', () => {
  const defaultCategory = 'AT Privileges Category DO NOT TOUCH';
  const categoryName = 'AT Analysis Category DO NOT TOUCH';
  const subCategoryName = 'AT Creating Analysis DO NOT TOUCH';

  const chartDesigner = analyzePage.designerDialog.chart;
  const chartName = `e2e chart ${(new Date()).toString()}`;
  const chartDescription = 'descr';
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
      commonFunctions.logOutByClearingLocalStorage();
      commonFunctions.openBaseUrl();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(dataProvider, function (data, description) {
    it('should create and delete ' + description, () => {
      if (data.chartType === 'chart:bubble') {
        metricName = 'PTT Subscr Detail';
        yAxisName = 'Activated Active Subscriber Count';
        xAxisName = 'Activated Inactive Subscriber Count';
        groupName = 'Apps Version';
      }

      login.loginAs(data.user);
      homePage.mainMenuExpandBtn.click();
      homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);
      homePage.mainMenuCollapseBtn.click();
      // Create analysis
      homePage.createAnalysis(metricName, data.chartType);

      // Select fields
      // Wait for field input box.
      commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);
      // Search field and add that into metric section.
      // analyzePage.designerDialog.chart.fieldSearchInput.clear();
      // browser.sleep(500);
      analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(yAxisName);
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(yAxisName));
      // Search field and add that into dimension section.
      // analyzePage.designerDialog.chart.fieldSearchInput.clear();
      // browser.sleep(500);
      analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(xAxisName);
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(xAxisName));

      // Search field and add that into size section.
      if (data.chartType === 'chart:bubble') {
        // analyzePage.designerDialog.chart.fieldSearchInput.clear();
        // browser.sleep(500);
        analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(sizeByName);
        commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(sizeByName));
      }
      // Search field and add that into group by section. i.e. Color by
      // analyzePage.designerDialog.chart.fieldSearchInput.clear();
      // browser.sleep(500);
      analyzePage.designerDialog.chart.fieldSearchInput.clear().sendKeys(groupName);
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.designerDialog.chart.getFieldPlusIcon(groupName));

      //Save
      const save = analyzePage.saveDialog;
      const designer = analyzePage.designerDialog;
      commonFunctions.waitFor.elementToBeClickableAndClick(designer.saveBtn);

      commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
      save.nameInput.clear().sendKeys(chartName);
      save.descriptionInput.clear().sendKeys(chartDescription);
      commonFunctions.waitFor.elementToBeClickableAndClick(save.selectedCategoryUpdated);
      commonFunctions.waitFor.elementToBeClickableAndClick(save.selectCategoryToSave(subCategoryName));
      commonFunctions.waitFor.elementToBeClickableAndClick(save.saveBtn);
      const createdAnalysis = analyzePage.main.getCardTitle(chartName);

      //Change to Card View
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

      //Verify if created appeared in list
      commonFunctions.waitFor.elementToBeClickableAndClick(createdAnalysis);
      commonFunctions.waitFor.elementToBeClickableAndClick(savedAlaysisPage.backButton);
      /*commonFunctions.waitFor.elementToBePresent(createdAnalysis)
        .then(() => expect(createdAnalysis.isPresent()).toBe(true));*/

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
  });
});
