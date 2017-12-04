/*
  Created by Alex
 */

const login = require('../../javascript/pages/common/login.po.js');
const analyzePage = require('../../javascript/pages/common/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const executedAnalysisPage = require('../../javascript/pages/common/executedAlaysis.po');
const using = require('jasmine-data-provider');

describe('create and delete charts', () => {
  const defaultCategory = 'AT Privileges Category DO NOT TOUCH';
  const categoryName = 'AT Analysis Category DO NOT TOUCH';
  const subCategoryName = 'AT Creating Analysis DO NOT TOUCH';
  const chartDesigner = analyzePage.designerDialog.chart;
  const chartName = `e2e chart ${(new Date()).toString()}`;
  const chartDescription = 'e2e test chart description';
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const yAxisName2 = 'Available Items';
  const groupName = 'Source OS';
  const metric = 'MCT TMO Session ES';

  const dataProvider = {
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'},
    /*'Column Chart by user': {user: 'userOne', chartType: 'chart:column'},
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
    'Scatter Plot Chart by user': {user: 'userOne', chartType: 'chart:scatter'}*/
    //TODO add Bubble Chart
  };

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(dataProvider, function (data, description) {
    it('should create ' + description, () => {
      expect(browser.getCurrentUrl()).toContain('/login');
      login.loginAs(data.user);

      //Collapse default category
      homePage.expandedCategory(defaultCategory).click();

      //Navigate to Category/Sub-category
      const collapsedCategory = homePage.collapsedCategory(categoryName);
      const subCategory = homePage.subCategory(subCategoryName);
      commonFunctions.waitFor.elementToBeClickableAndClick(collapsedCategory);
      commonFunctions.waitFor.elementToBeClickableAndClick(subCategory);

      //Create analysis
      analyzePage.analysisElems.addAnalysisBtn.click();
      const newDialog = analyzePage.newDialog;
      newDialog.getMetric(metric).click();
      newDialog.getMethod(data.chartType).click();
      newDialog.createBtn.click();

      //Select fields
      const y = chartDesigner.getYCheckBox(yAxisName);
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

      //Navigate to saved chart and check type
      homePage.savedAnalysis(chartName).click();

      //TODO add check for bar chart after https://jira.synchronoss.net:8443/jira/browse/SAW-1783 done

      /*const columnChartType = executedAnalysisPage.chartTypes.column;
      commonFunctions.waitFor.elementToBePresent(columnChartType)
        .then(() => expect(columnChartType.isPresent()).toBe(true));*/

      //Navigate back
      executedAnalysisPage.backButton.click();

      //Change to Card View
      commonFunctions.waitFor.elementToBeClickableAndClick(analyzePage.analysisElems.cardView);

      //Verify if created appeared in list
      commonFunctions.waitFor.elementToBePresent(createdAnalysis)
        .then(() => expect(createdAnalysis.isPresent()).toBe(true));

      //Verify chart type on home page
      analyzePage.main.getCardTypeByName(chartName).then(actualChartType =>
        expect(actualChartType).toEqual(data.chartType,
          "Chart type on Analyze Page expected to be " + data.chartType + ", but was " + actualChartType));
    });

    it('should delete ' + description, () => {
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

    it('log out ' + data.user, () => {
      analyzePage.main.doAccountAction('logout');
    });
  });
});
