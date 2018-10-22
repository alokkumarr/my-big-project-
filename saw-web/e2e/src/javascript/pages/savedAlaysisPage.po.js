module.exports = {
  forkBtn: element(by.css('button[e2e="action-fork-btn"]')),
  editBtn: element(by.css('button[e2e="action-edit-btn"]')),
  publishBtn: element(by.css('button[e2e="action-publish-btn"]')),
  actionsMenuBtn: element(by.css('button[e2e="actions-menu-toggle"]')),
  executeMenuOption: element(by.css('button[e2e="actions-menu-selector-execute"]')),
  publishMenuOption: element(by.css('button[e2e="actions-menu-selector-publish"]')),
  scheduleMenuOption: element(by.css('button[e2e="actions-menu-selector-publish"]')),
  exportMenuOption: element(by.css('button[e2e="actions-menu-selector-export"]')),
  deleteMenuOption: element(by.css('button[e2e="actions-menu-selector-delete"]')),
  deleteConfirmButton: element(by.xpath('//button[text()="Delete"]')),
  listView: {
    analysisByName: name => element(by.xpath[`//a[text()='${name}']`]),
    actionMenu: element(by.css[`[e2e="actions-menu-toggle"]`]),
  }, 
  analysisAction: name => element(by.xpath(`(//a[text()="${name}"]/following::button[@e2e='actions-menu-toggle'])[position()=1]`)),
  chartTypes: {
    column: element(by.xpath('//*[contains(@class,"highcharts-column-series")]'))
  },
  backButton: element(by.xpath('(//*[contains(@class,"back-button")])[position()=last()]')),
  analysisViewPageElements:{
    text: value => element(by.xpath(`//*[text()="${value}"]`)),
    title: element(by.binding('$ctrl.analysis.name')),
    description: element(by.binding('$ctrl.analysis.description')),
    analysisDetailsCard: element(by.xpath(`//*[text()="Analysis Details"]/parent::*/parent::mat-expansion-panel-header`))
  }
};
