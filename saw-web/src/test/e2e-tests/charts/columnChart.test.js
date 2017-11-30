/*
  Created by Alex
 */

const login = require('../../javascript/pages/common/login.po.js');
const analyze = require('../../javascript/pages/common/analyzePage.po.js');
const commonFunctions = require('../../javascript/helpers/commonFunctions.js');
const homePage = require('../../javascript/pages/homePage.po');
const executedAnalysisPage = require('../../javascript/pages/common/executedAlaysis.po');
const using = require('jasmine-data-provider');

describe('create Column Chart type analysis', () => {
  const defaultCategory = 'AT Privileges Category DO NOT TOUCH';
  const categoryName = 'AT Analysis Category DO NOT TOUCH';
  const subCategoryName = 'AT Creating Analysis DO NOT TOUCH';
  const chartDesigner = analyze.designerDialog.chart;
  const chartName = `e2e column chart ${(new Date()).toString()}`;
  const chartDescription = 'e2e test chart description';
  const xAxisName = 'Source Manufacturer';
  const yAxisName = 'Available MB';
  const groupName = 'Source OS';
  const metric = 'MCT TMO Session ES';
  const method = 'chart:column';

  const userDataProvider = {
    'admin': {handle: 'admin'},
    'user': {handle: 'userOne'}
  };

  afterAll(function () {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  using(userDataProvider, function (data, description) {
    it('should create column chart by ' + description, () => {
      expect(browser.getCurrentUrl()).toContain('/login');
      login.loginAs(data.handle);

      //Collapse default category
      homePage.expandedCategory(defaultCategory).click();

      //Navigate to Category/Sub-category
      const collapsedCategory = homePage.collapsedCategory(categoryName);
      const subCategory = homePage.subCategory(subCategoryName);
      commonFunctions.waitFor.elementToBeClickable(collapsedCategory);
      collapsedCategory.click();
      commonFunctions.waitFor.elementToBeClickable(subCategory);
      subCategory.click();

      //Create analysis
      analyze.analysisElems.addAnalysisBtn.click();
      const newDialog = analyze.newDialog;
      newDialog.getMetric(metric).click();
      newDialog.getMethod(method).click();
      newDialog.createBtn.click();

      //Select fields
      const y = chartDesigner.getYCheckBox(yAxisName);
      chartDesigner.getXRadio(xAxisName).click();
      commonFunctions.waitFor.elementToBeClickable(y);
      y.click();
      chartDesigner.getGroupRadio(groupName).click();

      //Refresh
      chartDesigner.refreshBtn.click();

      //Save
      const save = analyze.saveDialog;
      const designer = analyze.designerDialog;
      commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
      designer.saveBtn.click();

      commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
      save.nameInput.clear().sendKeys(chartName);
      save.descriptionInput.clear().sendKeys(chartDescription);
      save.saveBtn.click();
      const createdAnalysis = analyze.main.getCardTitle(chartName);

      //Navigate to saved chart and check type
      homePage.savedAnalysis(chartName).click();
      const columnChartType = executedAnalysisPage.chartTypes.column;
      commonFunctions.waitFor.elementToBePresent(columnChartType)
        .then(() => expect(columnChartType.isPresent()).toBe(true));

      //Navigate back
      executedAnalysisPage.backButton.click();

      //Change to Card View
      commonFunctions.waitFor.elementToBeClickable(analyze.analysisElems.cardView);
      analyze.analysisElems.cardView.click();

      //Verify if created appeared in list
      commonFunctions.waitFor.elementToBePresent(createdAnalysis)
        .then(() => expect(createdAnalysis.isPresent()).toBe(true));
    });

    it('delete the created column chart as ' + description, () => {
      const main = analyze.main;
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

    it('log out ' + description, () => {
      analyze.main.doAccountAction('logout');
    });
  });
});
