const protractor = require('protractor');
const EC = protractor.ExpectedConditions;
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');

const fluentWait = protractorConf.timeouts.fluentWait;

module.exports = {
  waitFor: {
    elementToBeClickable: element => {
      return browser.wait(EC.elementToBeClickable(element), fluentWait, "Element \"" + element.locator() + "\" is not clickable");
    },
    elementToBeVisible: element => {
      return browser.wait(EC.visibilityOf(element), fluentWait, "Element \"" + element.locator() + "\" is not visible");
    },
    elementToBePresent: element => {
      return browser.wait(EC.presenceOf(element), fluentWait, "Element \"" + element.locator() + "\" is not present");
    },
    elementToBeClickableAndClick: element => {
      browser.wait(EC.elementToBeClickable(element), fluentWait, "Element \"" + element.locator() + "\" is not clickable");
      element.click();
    },
    //Eliminates error: is not clickable at point
    elementToBeClickableAndClickByMouseMove: element => {
      browser.wait(EC.elementToBeClickable(element), fluentWait, "Element \"" + element.locator() + "\" is not clickable");
      browser.actions().mouseMove(element).click().perform();
    },
    // Possible options: /analyze/ , /login/
    pageToBeReady: pageName => {
      return browser.driver.wait(() => {
        return browser.driver.getCurrentUrl().then(url => {
          return pageName.test(url);
        });
      }, fluentWait);
    },
    cardsCountToUpdate: (cards, count) => {
      browser.wait(() => {
        return cards.then(() => {
          return cards.count().then(text => {
            return text === (count - 1);
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
  }
};
