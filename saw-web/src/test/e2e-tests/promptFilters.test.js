/*
 Created by Anudeep
 */

const login = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const commonFunctions = require('../javascript/helpers/commonFunctions.js');
const homePage = require('../javascript/pages/homePage.po');
const savedAlaysisPage = require('../javascript/pages/savedAlaysisPage.po');
const protractorConf = require('../../../../saw-web/conf/protractor.conf');
const using = require('jasmine-data-provider');
const categories = require('../javascript/data/categories');
const subCategories = require('../javascript/data/subCategories');
const dataSets = require('../javascript/data/datasets');
const designModePage = require('../javascript/pages/designModePage.po.js');
let AnalysisHelper = require('../javascript/api/AnalysisHelper');
let ApiUtils = require('../javascript/api/APiUtils');
const globalVariables = require('../javascript/helpers/globalVariables');
const utils = require('../javascript/helpers/utils');
const commonElementsPage = require('../javascript/pages/commonElementsPage.po');

describe('Prompt filter tests: promptFilters.test.js', () => {
  const defaultCategory = categories.privileges.name;
  const categoryName = categories.analyses.name;
  const subCategoryName = subCategories.createAnalysis.name;
  const chartDesigner = analyzePage.designerDialog.chart;

  let analysisId;
  let host;
  let token; 
  const chartDataProvider = {
    // DATES
    'Date data type filter as admin': { 
      user: 'admin',
      fieldType: 'date',
      value: 'This Week',
      fieldName: 'Date'
    },
    'Date data type filter': {
      user: 'userOne',
      fieldType: 'date',
      value: 'This Week',
      fieldName: 'Date'
    },
    //NUMBERS
    'Number data type filter as admin': { 
      user: 'admin',
      fieldType: 'number',
      operator: 'Equal to',
      value: 99999.33,
      fieldName: 'Double'
    },
    'Number data type filter': {
      user: 'userOne',
      fieldType: 'number',
      operator: 'Equal to',
      value: 99999.33,
      fieldName: 'Double'
    }, 
    //STRING
    'String data type filter as admin': { 
      user: 'admin',
      fieldType: 'string',
      operator: 'Equals',
      value: 'string 450',
      fieldName: 'String'
    },
    'String data type filter': {
      user: 'userOne',
      fieldType: 'string',
      operator: 'Equals',
      value: 'string 450',
      fieldName: 'String'
    }
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
      new AnalysisHelper().delete(host, token, protractorConf.config.customerCode, analysisId);
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout);
  });

  using(chartDataProvider, function (data, description) {
    it('should able to apply prompt filter for charts: ' + description, () => {

      let currentTime = new Date().getTime();
      let user = data.user;
      let chartType = 'chart:column';
      let type = chartType.split(":")[1];

      let name = chartType+' ' + globalVariables.e2eId+'-'+currentTime;
      let description ='Description:'+chartType+' for e2e ' + globalVariables.e2eId+'-'+currentTime;
      applyFilters(user, name, description, type, data.fieldName);
      //From analysis detail/view page
      verifyPromptFromDetailPage(data)
      //verifyPromptFromListView and by executing from action menu
      verifyPromptFromListView(name, data, true)
      //verifyPromptFromListView and by clicking on analysis
      verifyPromptFromListView(name, data, false)
      //verifyPromptFromCardView and by executing from action menu
      verifyPromptFromCardView(name, data, true)
      //verifyPromptFromCardView and by clicking on analysis
      verifyPromptFromCardView(name, data, false)
    });
});

/**
 * 
 * @param {*} name 
 * @param {*} data 
 * @param {*} execute 
 */
const verifyPromptFromListView = (name, data, execute)=> {

  //From analysis listview page
  analyzePage.navigateToHome();
  homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);
  //Change to Card View.
  commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.listView);
  commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.listView);
  analyzePage.analysisElems.listView.click();
  if(execute) {
    commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.analysisAction(name));
    commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.analysisAction(name));
    savedAlaysisPage.analysisAction(name).click();
    commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.executeMenuOption);
    commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.executeMenuOption);
    savedAlaysisPage.executeMenuOption.click();
    
  } else {
    //Open the created analysis.
    const analysisName = analyzePage.listViewItem(name);       
    commonFunctions.waitFor.elementToBeVisible(analysisName);
    commonFunctions.waitFor.elementToBeClickable(analysisName);
    analysisName.click();
  }  
  verifyFilters(data);
};
/**
 * 
 * @param {*} data 
 * @param {*} execute 
 */
const verifyPromptFromCardView = (name, data, execute)=> {
 //From analysis card page
 analyzePage.navigateToHome();
 homePage.navigateToSubCategoryUpdated(categoryName, subCategoryName, defaultCategory);
 //Change to Card View.
 commonFunctions.waitFor.elementToBeVisible(analyzePage.analysisElems.cardView);
 commonFunctions.waitFor.elementToBeClickable(analyzePage.analysisElems.cardView);
 analyzePage.analysisElems.cardView.click();
 if(execute) {
   commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.analysisAction(name));
   commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.analysisAction(name));
   savedAlaysisPage.analysisAction(name).click();
   commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.executeMenuOption);
   commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.executeMenuOption);
   savedAlaysisPage.executeMenuOption.click();

 } else {
   //Open the created analysis.
   const analysisName = analyzePage.main.getCardTitle(name);       
   commonFunctions.waitFor.elementToBeVisible(analysisName);
   commonFunctions.waitFor.elementToBeClickable(analysisName);
   analysisName.click();
 }  
 verifyFilters(data);
};
/**
 * 
 * @param {*} data 
 */
const verifyPromptFromDetailPage = (data)=> {
  //Execute the analysis from detail/view page and verify it asks for prompt filter
  commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.actionsMenuBtn);
  commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.actionsMenuBtn);
  savedAlaysisPage.actionsMenuBtn.click();
  commonFunctions.waitFor.elementToBeVisible(savedAlaysisPage.executeMenuOption);
  commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.executeMenuOption);
  savedAlaysisPage.executeMenuOption.click();
  verifyFilters(data);
};
/**
 * 
 * @param {*} data 
 */
const verifyFilters = (data) => {

  //Verify filter dailog
  commonFunctions.waitFor.elementToBeVisible(analyzePage.prompt.filterDialog);
  expect(analyzePage.prompt.filterDialog.isDisplayed()).toBeTruthy();
  expect(analyzePage.prompt.selectedField.getAttribute("value")).toEqual(data.fieldName);
  //apply fileters and execute
  setFilterValue(data.fieldType, data.operator, data.value);
}
/**
 * 
 * @param {*} fieldType 
 * @param {*} operator 
 * @param {*} value1 
 */
const setFilterValue = (fieldType, operator, value1) => {
    // Scenario for dates
    const filterWindow = designModePage.filterWindow;
    if (fieldType === 'date') {
      commonFunctions.waitFor.elementToBeClickable(filterWindow.date.presetDropDown);
      filterWindow.date.presetDropDown.click();
      commonFunctions.waitFor.elementToBeClickable(filterWindow.date.presetDropDownItem(value1));
      filterWindow.date.presetDropDownItem(value1).click();
    }

    // Scenario for numbers
    if (fieldType === 'number') {
      commonFunctions.waitFor.elementToBeClickable(filterWindow.number.operator);
      filterWindow.number.operator.click();
      commonFunctions.waitFor.elementToBeClickable(filterWindow.number.operatorDropDownItem(operator));
      filterWindow.number.operatorDropDownItem(operator).click();
      commonFunctions.waitFor.elementToBeVisible(filterWindow.number.input);
      filterWindow.number.input.click();
      filterWindow.number.input.clear().sendKeys(value1);
    }

    // Scenario for strings
    if (fieldType === 'string') {
      commonFunctions.waitFor.elementToBeClickable(filterWindow.string.operator);
      filterWindow.string.operator.click();
      commonFunctions.waitFor.elementToBeClickable(filterWindow.string.operatorDropDownItem(operator));
      filterWindow.string.operatorDropDownItem(operator).click();
      // Select diffrent input for Is in and Is not in operator TODO: we should be consistent
      if (operator === 'Is in' || operator === 'Is not in') {
        commonFunctions.waitFor.elementToBeVisible(filterWindow.string.isInIsNotInInput);
        filterWindow.string.isInIsNotInInput.clear().sendKeys(value1);
      } else {
        commonFunctions.waitFor.elementToBeVisible(filterWindow.string.input);
        filterWindow.string.input.clear().sendKeys(value1);
      }
    }
    commonFunctions.waitFor.elementToBeClickable(designModePage.applyFiltersBtn);
    designModePage.applyFiltersBtn.click();
    commonElementsPage.ifErrorPrintTextAndFailTest();
  };
/**
 * 
 * @param {*} filters 
 */
  const validateSelectedFilters = (filters) => {

    analyzePage.appliedFiltersDetails.selectedFilters.map(function(elm) {
      return elm.getText();
    }).then(function(displayedFilters) {
      expect(utils.arrayContainsArray(displayedFilters, filters)).toBeTruthy();
    });
  };
/**
 * 
 * @param {*} user 
 * @param {*} name 
 * @param {*} description 
 * @param {*} type 
 * @param {*} fieldName 
 */
const applyFilters = (user, name, description, type, fieldName) => {
   //Create new analysis.
   new AnalysisHelper().createChart(host, token,name,description, type);
   login.loginAs(user);
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
   //get analysis id from current url
   browser.getCurrentUrl().then(url => {
     analysisId = commonFunctions.getAnalysisIdFromUrl(url);
   });
   commonFunctions.waitFor.elementToBeClickable(savedAlaysisPage.editBtn);
   savedAlaysisPage.editBtn.click();
   //apply filters
   const filters = analyzePage.filtersDialogUpgraded;
   const filterAC = filters.getFilterAutocomplete(0);
   commonFunctions.waitFor.elementToBeClickable(chartDesigner.filterBtn);
   chartDesigner.filterBtn.click();
   commonFunctions.waitFor.elementToBeClickable(designModePage.filterWindow.addFilter('sample'));
   designModePage.filterWindow.addFilter('sample').click();
   filterAC.sendKeys(fieldName, protractor.Key.DOWN, protractor.Key.ENTER);
   commonFunctions.waitFor.elementToBeVisible(filters.prompt);
   commonFunctions.waitFor.elementToBeClickable(filters.prompt);
   filters.prompt.click();
   commonFunctions.waitFor.elementToBeClickable(filters.applyBtn);
   filters.applyBtn.click();
   browser.sleep(1000);
   //TODO: Need to check that filters applied or not.
   commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.filterText);
   commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.filterClear);
   commonFunctions.waitFor.elementToBeVisible(analyzePage.appliedFiltersDetails.selectedFiltersText);
   validateSelectedFilters([fieldName]);
    //Save
   const save = analyzePage.saveDialog;
   const designer = analyzePage.designerDialog;
   commonFunctions.waitFor.elementToBeClickable(designer.saveBtn);
   designer.saveBtn.click();
   commonFunctions.waitFor.elementToBeVisible(designer.saveDialog);
   commonFunctions.waitFor.elementToBeClickable(save.saveBtn);
   save.saveBtn.click();

  };

});