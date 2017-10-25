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
  const btn = parentElem.element(by.css(btnSelector));
  commonFunctions.waitFor.elementToBeClickable(btn);
  btn.click();
  btn.getAttribute('aria-owns').then(id => {
    const elem = element(by.id(id)).element(by.css(optionSelector));
    commonFunctions.waitFor.elementToBeClickable(elem);
    elem.click();
  });
}

function getMdSelectOptions({parentElem, btnSelector}) {
  const btn = parentElem.element(by.css(btnSelector));
  btn.click();
  return btn.getAttribute('aria-owns').then(id => {
    return element(by.id(id));
  });
}

module.exports = {
  hasClass,
  doMdSelectOption,
  getMdSelectOptions
};