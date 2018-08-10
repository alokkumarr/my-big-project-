const commonFunctions = require('../../../javascript/helpers/commonFunctions.js');
const protractorConf = require('../../../../../conf/protractor.conf');

module.exports = {
  addDashboardButton: element(by.css('button[e2e="dashboard-new-dashboard-button"]')),
  addWidgetButton: element(by.css('button[e2e="dashboard-add-widget-button"]')),
  existingAnalysisLink: element(by.css('button[e2e="dashboard-add-widget-type-analysis"]')),
  category: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  subCategory: name => element(by.xpath(`//span[contains(text(),"${name}")]`)),
  addAnalysisByName: name => element(by.xpath(`//strong[text()="${name}"]/parent::*/descendant::button`)),
  addAnalysisById: id => element(by.xpath(`//button[@e2e="dashboard-add-analysis-button-${id}"]`)),
  removeAnalysisById: id => element(by.xpath(`//button[@e2e="dashboard-remove-analysis-button-${id}"]`)),
  saveButton: element(by.css('button[e2e="dashboard-designer-save-button"]')),
  categorySelect: element(by.css('[e2e="dashboard-save-category-select"]')),
  dashboardName: element(by.css('input[e2e="dashboard-save-name-input"]')),
  dashboardDesc: element(by.css('textarea[e2e="dashboard-save-description-input"]')),
  subCategorySelect: name => element(by.xpath(`//span[@class="mat-option-text"and contains(text(),"${name}")]`)),
  saveDialogBtn: element(by.css(`[e2e="dashboard-save-save-button"]`)),
  dashboard:{
    dashboardTitle: name => element(by.xpath(`//h2[text()="${name}"]`)),
    dashboardAction: action => element(by.xpath(`//span[contains(text(),"${action}")]`)),
    dashboardConfirmDeleteButton: element(by.css('[e2e="dashboard-confirm-dialog-confirm"]'))



  }
};
