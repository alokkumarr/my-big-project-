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
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
const dataSets = require('../../javascript/data/datasets');
const designModePage = require('../../javascript/pages/designModePage.po.js');

describe('Create and delete charts: createAndDeleteCharts.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;
  // const chartName = `e2e chart ${(new Date()).toString()}`;
  // const chartDescription = 'descr';
  const yAxisName = 'Double';
  const yAxisName2 = 'Long';
  const xAxisName = 'Integer';
  const groupName = 'Date';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';

  const dataProvider = {
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'}, //SAWQA-323
    // 'Column Chart by user': {user: 'userOne', chartType: 'chart:column'}, //SAWQA-4475
    // 'Bar Chart by admin': {user: 'admin', chartType: 'chart:bar'}, //SAWQA-569
    // 'Bar Chart by user': {user: 'userOne', chartType: 'chart:bar'}, //SAWQA-4477
    // 'Stacked Chart by admin': {user: 'admin', chartType: 'chart:stack'}, //SAWQA-832
    // 'Stacked Chart by user': {user: 'userOne', chartType: 'chart:stack'}, //SAWQA-4478
    // 'Line Chart by admin': {user: 'admin', chartType: 'chart:line'}, //SAWQA-1095
    // 'Line Chart by user': {user: 'userOne', chartType: 'chart:line'}, //SAWQA-4672
    // 'Area Chart by admin': {user: 'admin', chartType: 'chart:area'}, //SAWQA-1348
    // 'Area Chart by user': {user: 'userOne', chartType: 'chart:area'}, //SAWQA-4676
    // 'Combo Chart by admin': {user: 'admin', chartType: 'chart:combo'}, //SAWQA-1602
    // 'Combo Chart by user': {user: 'userOne', chartType: 'chart:combo'}, //SAWQA-4678
    // 'Scatter Plot Chart by admin': {user: 'admin', chartType: 'chart:scatter'}, //SAWQA-1851
    // 'Scatter Plot Chart by user': {user: 'userOne', chartType: 'chart:scatter'}, //SAWQA-4679
    // 'Bubble Chart by admin': {user: 'admin', chartType: 'chart:bubble'}, //SAWQA-2100
    // 'Bubble Chart by user': {user: 'userOne', chartType: 'chart:bubble'} //SAWQA-4680
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
    it('should create and delete ' + description, () => {
      login.loginAs(data.user);
      homePage.mainMenuExpandBtn.click();
      homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);
      homePage.mainMenuCollapseBtn.click();

      let chartName = `e2e ${description} ${(new Date()).toString()}`;
      let chartDescription = `e2e ${description} : description ${(new Date()).toString()}`;

      // Create analysis
      homePage.createAnalysis(metricName, data.chartType);

      //Select fields
      // Wait for field input box.
      commonFunctions.waitFor.elementToBeVisible(analyzePage.designerDialog.chart.fieldSearchInput);
      // Search field and add that into metric section.
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName));
      designModePage.chart.addFieldButton(yAxisName).click();

      // Search field and add that into dimension section.
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(xAxisName));
      designModePage.chart.addFieldButton(xAxisName).click();

      // Search field and add that into size section.
      if (data.chartType === 'chart:bubble') {
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(sizeByName));
        designModePage.chart.addFieldButton(sizeByName).click();
      }
       //If Combo then add one more field
       if (data.chartType === 'chart:combo') {
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(yAxisName2));
        designModePage.chart.addFieldButton(yAxisName2).click();
      }

      // Search field and add that into group by section. i.e. Color by
      commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(groupName));
      designModePage.chart.addFieldButton(groupName).click();
      //Save
      const save = analyzePage.saveDialog;
      const designer = analyzePage.designerDialog;
      commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
      designer.saveBtn.click();

      commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
      save.nameInput.clear().sendKeys(chartName);
      save.descriptionInput.clear().sendKeys(chartDescription);
      commonFunctions.waitFor.elementToBeClickable(save.selectedCategoryUpdated);
      save.selectedCategoryUpdated.click();
      commonFunctions.waitFor.elementToBeClickable(save.selectCategoryToSave(subCategoryName));
      save.selectCategoryToSave(subCategoryName).click();
      commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
      save.saveBtn.click();
      const createdAnalysis = analyzePage.main.getCardTitle(chartName);

      //Change to Card View
      commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
      commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
      analyzePage.analysisElems.cardView.click();
      //Verify if created appeared in list
      commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
      commonFunctions.waitFor.elementToBeClickable(createdAnalysis);
      createdAnalysis.click();
      commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.backButton);
      savedAlaysisPage.backButton.click();
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
        commonFunctions.waitFor.elementToBeClickable(main.confirmDeleteBtn);
        main.confirmDeleteBtn.click();
        commonFunctions.waitFor.cardsCountToUpdate(cards, count);

        //Expect to be deleted
        expect(main.getAnalysisCards(chartName).count()).toBe(count - 1);
      });
    });
  });
});
