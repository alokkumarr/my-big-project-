module.exports = {
  forkBtn: element(by.css('button[e2e="action-fork-btn"]')),
  editBtn: element(by.css('button[e2e="action-edit-btn"]')),
  publishBtn: element(by.css('button[e2e="action-publish-btn"]')),
  actionsMenuBtn: element(by.css('button[e2e="actions-menu-toggle"]')),
  executeMenuOption: element(by.css('button[e2e="actions-menu-selector-execute"]')),
  exportMenuOption: element(by.css('button[e2e="actions-menu-selector-export"]')),
  deleteMenuOption: element(by.css('button[e2e="actions-menu-selector-delete"]')),
};

exports.isChartType = function (expectedChartType) {
  switch (expectedChartType) {
    case 'column':
      if (element(by.xpath('//*[contains(@class,"highcharts-column-series")]')).isPresent()) {
        return true;
      }
      break;
    default:
      return false;
  }
};
