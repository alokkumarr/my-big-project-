var testDataReader = require('../javascript/testdata/testDataReader.js');
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
let AnalysisHelper = require('../javascript/api/AnalysisHelper');
let ApiUtils = require('../javascript/api/APiUtils');
const globalVariables = require('../javascript/helpers/globalVariables');
const Constants = require('../javascript/api/Constants');
const utils = require('../javascript/helpers/utils');

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
  let analysisId;
  let forkedAnalysisId;
  let host;
  let token;
  beforeAll(function () {
    host = new ApiUtils().getHost(browser.baseUrl);
    token = new AnalysisHelper().getToken(host);
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;

  });

  beforeEach(function (done) {
    setTimeout(function () {
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  afterEach(function (done) {
    setTimeout(function () {
      new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, analysisId);
      new AnalysisHelper().deleteAnalysis(host, token, protractorConf.config.customerCode, forkedAnalysisId);
      commonFunctions.logOutByClearingLocalStorage();
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(testDataReader.testData['FORKDELETECHARTS']['forkDeleteChartsDataProvider'], function (data, description) {
    it('should fork, edit and delete ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'FORKDELETECHARTS', dp:'forkDeleteChartsDataProvider'}), () => {
        try {
          let currentTime = new Date().getTime();
          let user = data.user;
          let type = data.chartType.split(":")[1];
          let name = data.chartType+' ' + globalVariables.e2eId+'-'+currentTime;
          let description ='Description:'+data.chartType+' for e2e ' + globalVariables.e2eId+'-'+currentTime;

          //Create new analysis.
          new AnalysisHelper().createNewAnalysis(host, token, name, description, Constants.CHART, type);

          login.loginAs(data.user);
          browser.sleep(500);
          homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);

          //Change to Card View.
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
          //Open the created analysis.
          const createdAnalysis = analyzePage.main.getCardTitle(name);

          commonFunctions.waitFor.elementToBeVisible(createdAnalysis);
          commonFunctions.waitFor.elementToBeClickable(createdAnalysis);
          createdAnalysis.click();
          //get analysis id from current url
          browser.getCurrentUrl().then(url => {
            analysisId = commonFunctions.getAnalysisIdFromUrl(url);
          });
          commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.forkBtn);
          commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.forkBtn);
          savedAlaysisPage.forkBtn.click();
          browser.waitForAngular();
          browser.sleep(2000);
          const designer = analyzePage.designerDialog;
          //Clear all fields.
          designModePage.filterWindow.deleteFields.then(function(deleteElements) {
            for (var i = 0; i < deleteElements.length; ++i) {
              commonFunctions.waitFor.elementToBeVisible(deleteElements[i]);
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
          commonFunctions.waitFor.elementToBeVisible(designer.saveBtn);
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

          commonFunctions.waitFor.elementToBeVisible(save.saveBtn);
          commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
          save.saveBtn.click();
          browser.waitForAngular();
          browser.sleep(1000);
          commonFunctions.waitFor.elementToBeNotVisible(analyzePage.designerDialog.chart.filterBtn);
          browser.ignoreSynchronization = false;
          analyzePage.navigateToHome();
          browser.ignoreSynchronization = true;
          browser.sleep(500);
          homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);

          element(utils.hasClass(homePage.cardViewInput, 'mat-radio-checked').then(function(isPresent) {
            if(isPresent) {
              //console.log('Already in card view..')
            } else {
              //console.log('Not in card view..')
              commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
              commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
              analyzePage.analysisElems.cardView.click();
            }
          }));

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
          //get analysis id from current url
          browser.getCurrentUrl().then(url => {
            forkedAnalysisId = commonFunctions.getAnalysisIdFromUrl(url);
          });
          //Verify updated details.
          expect(savedAlaysisPage.analysisViewPageElements.text(forkedName).getText()).toBe(forkedName);

          commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.analysisViewPageElements.analysisDetailsCard);
          commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.analysisViewPageElements.analysisDetailsCard);
          commonFunctions.scrollIntoView(savedAlaysisPage.analysisViewPageElements.analysisDetailsCard);

          element(utils.hasClass(savedAlaysisPage.analysisViewPageElements.analysisDetailsCard, 'mat-expanded').then(function(isPresent) {
            if(!isPresent) {
              savedAlaysisPage.analysisViewPageElements.analysisDetailsCard.click();
              commonFunctions.scrollIntoView(savedAlaysisPage.analysisViewPageElements.text(forkedDescription));
              expect(savedAlaysisPage.analysisViewPageElements.text(forkedDescription).getText()).toBe(forkedDescription);
            } else {
              commonFunctions.scrollIntoView(savedAlaysisPage.analysisViewPageElements.text(forkedDescription));
              expect(savedAlaysisPage.analysisViewPageElements.text(forkedDescription).getText()).toBe(forkedDescription);
            }
          }));
        }catch (e) {
          console.log(e);
        }
    });
  });
});
