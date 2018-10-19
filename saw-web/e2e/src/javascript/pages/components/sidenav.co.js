const publicCategories = element(by.css('.sidenav-menu > li:first-child'));
const privateCategories = element(by.css('.sidenav-menu > li:last-child'));

const publicCategoriesToggle = publicCategories.element(by.css('button.md-button-toggle'));
const privateCategoriesToggle = privateCategories.element(by.css('button.md-button-toggle'));

const firstPublicCategory = publicCategories.element(by.css('ul.menu-toggle-list > li:first-child a'));
const firstPrivateCategory = privateCategories.element(by.css('ul.menu-toggle-list > li:first-child a'));

module.exports = {
  menuBtn: element(by.css('[sidenav-target="left-side-nav"]')),
  sidenavMenu: element(by.css('ul.sidenav-menu')),
  publicCategoriesToggle,
  privateCategoriesToggle,
  firstPrivateCategory,
  firstPublicCategory
};
