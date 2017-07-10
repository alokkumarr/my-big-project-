const cannedAnalysisCategories = element(by.css('.sidenav-menu > li:first-child'));
const myAnalysisCategories = element(by.css('.sidenav-menu > li:last-child'));

const cannedAnalysisCategoriesToggle = cannedAnalysisCategories.element(by.css('button.md-button-toggle'));
const myAnalysisCategoriestoggle = myAnalysisCategories.element(by.css('button.md-button-toggle'));

const firstCannedAnalysisCategory = cannedAnalysisCategories.element(by.css('ul.menu-toggle-list > li:first-child a'));
const firstMyAnalysisCategory = cannedAnalysisCategories.element(by.css('ul.menu-toggle-list > li:first-child a'));

module.exports = {
  sidenavElements: {
    menuBtn: element(by.css('[sidenav-target="left-side-nav"]')),
    sidenavMenu: element(by.css('ul.sidenav-menu')),
    cannedAnalysisCategoriesToggle,
    myAnalysisCategoriestoggle,
    firstCannedAnalysisCategory,
    firstMyAnalysisCategory
  }
};
