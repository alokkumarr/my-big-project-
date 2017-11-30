module.exports = {
  accountSettingsMenuBtn: element(by.css('button[e2e="account-settings-menu-btn"]')),
  adminMenuOption: element(by.css('a[e2e="account-settings-selector-admin"]')),
  changePasswordMenuOption: element(by.css('button[e2e="account-settings-selector-change-password"]')),
  //In list view tag is "span". In card view tag is "a"
  savedAnalysis: analysisName => {
    return element(by.xpath(`//*[text() = "${analysisName}"]`))
  },
  expandedCategory: categoryName => {
    return element(by.xpath(`//span[contains(text(),'${categoryName}')]/../../../button`));
  },
  collapsedCategory: categoryName => {
    return element(by.xpath(`//ul[contains(@class,'is-collapsed')]/preceding-sibling::button/div/span[text()='${categoryName}']/../../../../../..`));
  },
  subCategory: subCategoryName => {
    return element(by.xpath(`(//span[text()='${subCategoryName}'])[1]`));
  }
};
