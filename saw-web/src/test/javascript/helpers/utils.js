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
  commonFunctions.waitFor.elementToBeClickableAndClickByMouseMove(parentElem.element(by.css(btnSelector)));
  parentElem.element(by.css(btnSelector)).getAttribute('aria-owns').then(id => {
    commonFunctions.waitFor.elementToBeClickableAndClickByMouseMove(element(by.id(id)).element(by.css(optionSelector)));
  });
}

function getMdSelectOptions({parentElem, btnSelector}) {
  commonFunctions.waitFor.elementToBeClickableAndClick(parentElem.element(by.css(btnSelector)));
  const btn = parentElem.element(by.css(btnSelector));
  return btn.getAttribute('aria-owns').then(id => {
    return element(by.id(id));
  });
}

module.exports = {
  hasClass,
  doMdSelectOption,
  getMdSelectOptions
};
