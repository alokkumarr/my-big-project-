module.exports = {
  forkBtn: element(by.css('button[e2e="action-fork-btn"]')),
  editBtn: element(by.css('button[e2e="action-edit-btn"]')),
  publishBtn: element(by.css('button[e2e="action-publish-btn"]')),
  actionsMenuBtn: element(by.css('button[e2e="actions-menu-toggle"]')),
  executeMenuOption: element(by.css('button[e2e="actions-menu-selector-execute"]')),
  exportMenuOption: element(by.css('button[e2e="actions-menu-selector-export"]')),
  deleteMenuOption: element(by.css('button[e2e="actions-menu-selector-delete"]')),
  deleteConfirmButton: element(by.xpath('//button[text()="Delete"]')),
  
  chartTypes: {
    column: element(by.xpath('//*[contains(@class,"highcharts-column-series")]'))
  },
  backButton: element(by.xpath('//button[contains(@class,"back-button")]')),
  analysisViewPageElements:{
    title: element(by.binding('$ctrl.analysis.name')),
    description: element(by.binding('$ctrl.analysis.description'))
  }
};
