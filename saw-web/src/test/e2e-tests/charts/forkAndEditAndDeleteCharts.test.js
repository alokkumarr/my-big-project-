/*
 Created by Anudeep
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
let AnalysisHelper = require('../../javascript/api/AnalysisHelper');
let ApiUtils = require('../../javascript/api/APiUtils');
const globalVariables = require('../../javascript/helpers/globalVariables');

describe('Fork & Edit and delete charts: forkAndEditAndDeleteCharts.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;
  //updated fields
  const metrics = 'Integer';
  const dimension = 'String';
  const yAxisName2 = 'Long';
  const groupName = 'Date';
  const metricName = dataSets.pivotChart;
  const sizeByName = 'Float';

  let host;
  let token; 
  const dataProvider = {
    // 'Combo Chart by admin': {user: 'admin', chartType: 'chart:combo'}, //SAWQA-1602 ---disbaled in the UI
    // 'Combo Chart by user': {user: 'userOne', chartType: 'chart:combo'}, //SAWQA-4678 ---disbaled in the UI
    'Column Chart by admin': {user: 'admin', chartType: 'chart:column'}, //SAWQA-323
    'Column Chart by user': {user: 'userOne', chartType: 'chart:column'}, //SAWQA-4475
    'Bar Chart by admin': {user: 'admin', chartType: 'chart:bar'}, //SAWQA-569
    'Bar Chart by user': {user: 'userOne', chartType: 'chart:bar'}, //SAWQA-4477
    'Stacked Chart by admin': {user: 'admin', chartType: 'chart:stack'}, //SAWQA-832
    'Stacked Chart by user': {user: 'userOne', chartType: 'chart:stack'}, //SAWQA-4478
    'Line Chart by admin': {user: 'admin', chartType: 'chart:line'}, //SAWQA-1095
    'Line Chart by user': {user: 'userOne', chartType: 'chart:line'}, //SAWQA-4672
    // 'Area Chart by admin': {user: 'admin', chartType: 'chart:area'}, //SAWQA-1348 ---disbaled in the UI
    // 'Area Chart by user': {user: 'userOne', chartType: 'chart:area'}, //SAWQA-4676 ---disbaled in the UI
    'Scatter Plot Chart by admin': {user: 'admin', chartType: 'chart:scatter'}, //SAWQA-1851
    'Scatter Plot Chart by user': {user: 'userOne', chartType: 'chart:scatter'}, //SAWQA-4679
    'Bubble Chart by admin': {user: 'admin', chartType: 'chart:bubble'}, //SAWQA-2100
    'Bubble Chart by user': {user: 'userOne', chartType: 'chart:bubble'} //SAWQA-4680
  };

  beforeAll(function () {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
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
    it('should fork, edit and delete ' + description, () => {
        let currentTime = new Date().getTime();
        let name = data.chartType+' ' + globalVariables.e2eId+'-'+currentTime;
        let description ='Description:'+data.chartType+' for e2e ' + globalVariables.e2eId+'-'+currentTime;
        let type = data.chartType.split(":")[1];
        //Create new analysis.
        new AnalysisHelper().createChart(host, token,name,description, type);

        login.loginAs(data.user);
       
        homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);

        //Change to Card View.
        commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
        commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
        analyzePage.analysisElems.cardView.click();
        //Open the created analysis.
        const createdAnalysis = analyzePage.main.getCardTitle(name);
        
        commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
        commonFunctions.waitFor.elementToBeClickable(createdAnalysis);
        createdAnalysis.click();
        
        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.forkBtn);
        savedAlaysisPage.forkBtn.click();
        
        const designer = analyzePage.designerDialog;
        //Clear all fields.
        designModePage.filterWindow.deleteFields.then(function(deleteElements) {
            for (var i = 0; i < deleteElements.length; ++i) {
                commonFunctions.waitFor.elementToBeClickable(deleteElements[i]);
                deleteElements[i].click();
            }
        });
        //Add new feilds.
        //Dimension section.
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(dimension));
        designModePage.chart.addFieldButton(dimension).click();

        // Group by section. i.e. Color by
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(groupName));
        designModePage.chart.addFieldButton(groupName).click();

        // Metric section.
        commonFunctions.waitFor.elementToBeClickable(designModePage.chart.addFieldButton(metrics));
        designModePage.chart.addFieldButton(metrics).click();

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
        //Verify chart axis and group by
        commonFunctions.waitFor.elementToBePresent(designModePage.chart.getAxisLabel(type, metrics, "yaxis"));
        commonFunctions.waitFor.elementToBePresent(designModePage.chart.getAxisLabel(type, dimension, "xaxis"));
        commonFunctions.waitFor.elementToBePresent(designModePage.chart.groupBy(type));
        //Save
        const save = analyzePage.saveDialog;
        commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
        designer.saveBtn.click();
        let forkedName = name +' forked';
        let forkedDescription = description + 'forked';
        commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
        save.nameInput.clear().sendKeys(forkedName);
        save.descriptionInput.clear().sendKeys(forkedDescription);
        commonFunctions.waitFor.elementToBeClickable(save.selectedCategoryUpdated);
        save.selectedCategoryUpdated.click();
        commonFunctions.waitFor.elementToBeClickable(save.selectCategoryToSave(subCategoryName));
        save.selectCategoryToSave(subCategoryName).click();
        commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
        save.saveBtn.click();

        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.forkBtn);
        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.backButton);
        savedAlaysisPage.backButton.click();
        //Verify chart type on home page
        analyzePage.main.getCardTypeByName(forkedName).then(actualChartType =>
        expect(actualChartType).toEqual(data.chartType,
          "Chart type on Analyze Page expected to be " + data.chartType + ", but was " + actualChartType));
        
          const forkedAnalysis = analyzePage.main.getCardTitle(forkedName);
        //Change to Card View
        commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
        commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
        analyzePage.analysisElems.cardView.click();
        //Verify if created appeared in list
        commonFunctions.waitFor.elementToBeVisible(forkedAnalysis);
        commonFunctions.waitFor.elementToBeClickable(forkedAnalysis);
        forkedAnalysis.click();

        //Verify updated details.
        expect(savedAlaysisPage.analysisViewPageElements.title.getText()).toBe(forkedName);
        expect(savedAlaysisPage.analysisViewPageElements.description.getText()).toBe(forkedDescription);
        //Delete created chart
        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.actionsMenuBtn);
        savedAlaysisPage.actionsMenuBtn.click();
        commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.deleteMenuOption);
        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.deleteMenuOption);
        savedAlaysisPage.deleteMenuOption.click();
        commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.deleteConfirmButton);
        commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.deleteConfirmButton);
        savedAlaysisPage.deleteConfirmButton.click();
    });
  });
});