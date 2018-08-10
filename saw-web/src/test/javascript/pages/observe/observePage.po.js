const commonFunctions = require('../../../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../../../../conf/protractor.conf');

module.exports = {
  addDashboardButton: element(by.xpath('//span[contains(text(),"+ DASHBOARD")]/parent::button')),
  addWidgetButton: element(by.xpath('//button[contains(@class,"add-analysis")]')),
  existingAnalysisLink: element(by.xpath('//strong[text()="Existing Analysis"]')),
  category: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  subCategory: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  addAnalysisByName: name => element(by.xpath(`//strong[text()="${name}"]/parent::*/descendant::button`)),
  saveButton: element(by.xpath('//span[contains(text(),"Save")]/parent::button')),
  categorySelect: element(by.xpath('//mat-select[@name="dashboardCat"]')),
  dashboardName: element(by.xpath('//input[@name="dashboardName"]')),
  dashboardDesc: element(by.xpath('//textarea[@name="dashboardDesc"]')),
  subCategorySelect: name => element(by.xpath(`//span[@class="mat-option-text"and contains(text(),"${name}")]`)),
  saveDialogBtn: element(by.xpath(`(//div[contains(@class,"mat-dialog-actions")]/descendant::button)[1]`)),
  dashboard:{
    dashboardTitle: name => element(by.xpath(`//h2[text()="${name}"]`)),
    dashboardAction: action => element(by.xpath(`//span[text()="${action}"]`)),
    dashboardConfirmDeleteButton: element(by.xpath('//button[text()="Delete"]'))



  }
};
