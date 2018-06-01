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
  commonFunctions.waitFor.elementToBeClickableAndClick(parentElem.element(by.css(btnSelector)));
  browser.sleep(100);
  commonFunctions.waitFor.elementToBePresent(element(by.xpath('//div[contains(@class,"mat-menu-content")]')));
  commonFunctions.waitFor.elementToBeVisible(element(by.xpath('//div[contains(@class,"mat-menu-content")]')));
  parentElem.element(by.css(btnSelector)).getAttribute('aria-owns').then(id => {
    if (id) {
      commonFunctions.waitFor.elementToBeClickableAndClick(element(by.id(id)).element(by.css(optionSelector)));
    } else {
      const menu = parentElem.element(by.css(`${btnSelector} + mat-menu`));
      menu.getAttribute('class').then(className => {
        commonFunctions.waitFor.elementToBeClickableAndClick(element(by.css(`div.${className}`)).element(by.css(optionSelector)));
      });
    }
  });
}

function getMdSelectOptionsNew({parentElem, btnSelector}) {
  commonFunctions.waitFor.elementToBeClickableAndClick(parentElem.element(by.css(btnSelector)));
  const menuItems = element(by.xpath('//div[contains(@class,"mat-menu-panel")]/parent::div'));
  return menuItems.getAttribute('id').then(id => {
    return element(by.id(id));
  });
}

function getMdSelectOptions({parentElem, btnSelector}) {
  commonFunctions.waitFor.elementToBeClickableAndClick(parentElem.element(by.css(btnSelector)));
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

module.exports = {
  hasClass,
  doMdSelectOption,
  getMdSelectOptions,
  getMdSelectOptionsNew,
  arrayContainsArray
};
