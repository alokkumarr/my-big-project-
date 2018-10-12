var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const login = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const homePage = require('../javascript/pages/homePage.po');
const savedAlaysisPage = require('../javascript/pages/savedAlaysisPage.po');
const protractorConf = require('../../protractor.conf');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
const dataSets = require('../javascript/data/datasets');
const designModePage = require('../javascript/pages/designModePage.po.js');
const utils = require('../javascript/helpers/utils');

describe('Create and delete charts: createAndDeleteCharts.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;
  // const chartName = `e2e chart ${(new Date()).toString()}`;
  // const chartDescription = 'descr';
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

  using(testDataReader.testData['CREATEDELETECHART']['createDeleteChartDataProvider'], function (data, description) {
    it('should create and delete ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'CREATEDELETECHART', dp:'createDeleteChartDataProvider'}), () => {
     try {
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
       browser.sleep(1000);
       const createdAnalysis = analyzePage.main.getCardTitle(chartName);

       //Change to Card View
       element(utils.hasClass(homePage.cardViewInput, 'mat-radio-checked').then(function(isPresent) {
         if(isPresent) {
           console.log('Already in card view..')
         } else {
           console.log('Not in card view..')
           commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
           commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
           analyzePage.analysisElems.cardView.click();
         }
       }));

       //Verify if created appeared in list
       commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
       commonFunctions.waitFor.elementToBeClickable(createdAnalysis);
       createdAnalysis.click();
       browser.sleep(1000);
       commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.backButton);
       savedAlaysisPage.backButton.click();
       commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
       /*commonFunctions.waitFor.elementToBePresent(createdAnalysis)
         .then(() => expect(createdAnalysis.isPresent()).toBe(true));*/
       //Verify chart type on home page
       commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.analysisWithType(chartName, data.chartType), 10000);
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
     }catch (e) {
       console.log(e);
     }
    });
  });
});
