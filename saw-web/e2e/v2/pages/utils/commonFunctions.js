'use strict';

const protractor = require('protractor');
const EC = protractor.ExpectedConditions;
const protractorConf = require('../../conf/protractor.conf');

const fluentWait = protractorConf.timeouts.fluentWait;

module.exports = {
  waitFor: {
    elementToBeClickable: (element, wait = null) => {
      return browser.wait(
        EC.elementToBeClickable(element),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is not clickable'
      );
    },
    elementToBeVisible: (element, wait = null) => {
      return browser.wait(
        EC.visibilityOf(element),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is not visible'
      );
    },
    elementToBePresent: (element, wait = null) => {
      return browser.wait(
        EC.presenceOf(element),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is not present'
      );
    },
    elementToBeEnabledAndVisible: (element, wait = null) => {
      browser.wait(
        EC.elementToBeClickable(element),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is not clickable'
      );
    },
    elementToBeNotVisible: (element, wait = null) => {
      return browser.wait(
        EC.not(EC.presenceOf(element)),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is present'
      );
    },
    textToBePresent: (element, value, wait = null) => {
      return browser.wait(
        EC.textToBePresentInElement(element, value),
        wait ? wait : fluentWait
      );
    },
    //Eliminates error: is not clickable at point
    elementToBeClickableAndClick: element => {
      let count = 0;
      return click(element, count);
    },
    //Deprecated. Should eliminate error: is not clickable at point - but does not work
    elementToBeClickableAndClickByMouseMove: (element, wait = null) => {
      browser.wait(
        EC.elementToBeClickable(element, (wait = null)),
        wait ? wait : fluentWait,
        'Element "' + element.locator() + '" is not clickable'
      );
      browser
        .actions()
        .mouseMove(element)
        .click()
        .perform();
    },
    // Possible options: /analyze/ , /login/
    pageToBeReady: pageName => {
      return browser.wait(() => {
        return browser.getCurrentUrl().then(url => {
          return pageName.test(url);
        });
      }, fluentWait);
    },
    cardsCountToUpdate: (cards, count) => {
      browser.wait(() => {
        return cards.then(() => {
          return cards.count().then(text => {
            return text === count - 1;
          });
        });
      }, fluentWait);
    }
  },
  find: {
    parent: element => {
      return element.element(by.xpath(`parent::*`));
    }
  },
  dismissDropdownMenu: () => {
    element(by.css('md-backdrop')).click();
    expect(element(by.css('md-backdrop')).isPresent()).toBe(false);
  },
  dragAndDrop(dragElement, dropElement) {
    // You can also use the `dragAndDrop` convenience action.
    browser
      .actions()
      .dragAndDrop(dragElement, dropElement)
      .mouseUp()
      .perform();
  },
  openBaseUrl() {
    browser.get(protractorConf.config.baseUrl);
  },
  clearLocalStorage() {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  },
  scrollIntoView(element) {
    browser.executeScript('arguments[0].scrollIntoView()', element);
  },
  writeScreenShot(data, filename) {
    let fs = require('fs');
    let stream = fs.createWriteStream(filename);
    stream.write(new Buffer(data, 'base64'));
    stream.end();
  },
  getAnalysisIdFromUrl(url) {
    let ulrParts = url.split('analyze/analysis/')[1];
    return ulrParts.split('/')[0];
  },
  slideHorizontally(element, x_axis) {
    browser
      .actions()
      .dragAndDrop(element, { x: x_axis, y: 0 })
      .perform();
  },
  goToHome() {
    browser.get(browser.baseUrl);
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return /saw/.test(url);
      });
    }, protractorConf.timeouts.fluentWait);
  }
};
