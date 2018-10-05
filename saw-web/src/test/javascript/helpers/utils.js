const commonFunctions = require('../../javascript/helpers/commonFunctions');

/**
 * Check if an element has class
 */
function hasClass(element, cls) {
  return element
    .getAttribute('class')
    .then(classes => classes.split(' ').includes(cls));
}

/**
 * Gets an option field from the md-Select component
 * opens the menu by clicking the btnSelector,
 * and then chooses an option from the menu based in the optionSelector
 */
function doMdSelectOption({parentElem, btnSelector, optionSelector}) {
  // Cannot declare element before wait because of: element is not clickable error.
  commonFunctions.waitFor.elementToBePresent(parentElem.element(by.css(btnSelector)));
  commonFunctions.waitFor.elementToBeVisible(parentElem.element(by.css(btnSelector)));
  commonFunctions.waitFor.elementToBeClickable(parentElem.element(by.css(btnSelector)));
  parentElem.element(by.css(btnSelector)).click();
  browser.sleep(500);
  commonFunctions.waitFor.elementToBePresent(element(by.xpath('//div[contains(@class,"mat-menu-content")]')));
  commonFunctions.waitFor.elementToBeVisible(element(by.xpath('//div[contains(@class,"mat-menu-content")]')));
  parentElem.element(by.css(btnSelector)).getAttribute('aria-owns').then(id => {
    if (id) {
      commonFunctions.waitFor.elementToBeClickable(element(by.id(id)).element(by.css(optionSelector)));
      element(by.id(id)).element(by.css(optionSelector)).click();
      browser.sleep(500);
    } else {
      const menu = parentElem.element(by.css(`${btnSelector} + mat-menu`));
      menu.getAttribute('class').then(className => {
        commonFunctions.waitFor.elementToBeClickable(element(by.css(`div.${className}`)).element(by.css(optionSelector)));
        element(by.css(`div.${className}`)).element(by.css(optionSelector)).click();
        browser.sleep(500);
      });
    }
  });
}

function getMdSelectOptionsNew({parentElem, btnSelector}) {
  commonFunctions.waitFor.elementToBeClickable(parentElem.element(by.css(btnSelector)));
  parentElem.element(by.css(btnSelector)).click();
  const menuItems = element(by.xpath('//div[contains(@class,"mat-menu-panel")]/parent::div'));
  return menuItems.getAttribute('id').then(id => {
    return element(by.id(id));
  });
}

function getMdSelectOptions({parentElem, btnSelector}) {
  commonFunctions.waitFor.elementToBeClickable(parentElem.element(by.css(btnSelector)));
  parentElem.element(by.css(btnSelector)).click();
  const btn = parentElem.element(by.css(btnSelector));
  return btn.getAttribute('aria-owns').then(id => {
    return element(by.id(id));
  });
}

function arrayContainsArray (superset, subset) {
  if (0 === subset.length) {
    return false;
  }
  return subset.every(function (value) {
    return (superset.indexOf(value) >= 0);
  });
}
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

module.exports = {
  hasClass,
  doMdSelectOption,
  getMdSelectOptions,
  getMdSelectOptionsNew,
  arrayContainsArray,
  getRandomInt
};
