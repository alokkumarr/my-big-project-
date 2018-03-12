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
    elementToBeEnabledAndVisible: element => {
     browser.wait(EC.elementToBeClickable(element), fluentWait, "Element \"" + element.locator() + "\" is not clickable");
     },
    //Eliminates error: is not clickable at point
    elementToBeClickableAndClick: element => {
      let count = 0;
      return click(element, count);
    },
    //Deprecated. Should eliminate error: is not clickable at point - but does not work
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

function click(element, i) {
  element.click().then(
    function () {
    }, function (err) {
      if (err) {
        i++;
        browser.sleep(1000);
        if (i < protractorConf.timeouts.tempts) {
          click(element, i);
        } else {
          throw new Error("Element '" + element.locator() + "' is not clickable after " +
            protractorConf.timeouts.tempts + " attempts. Error: " + err);
        }
      }
    });
}

// TODO write function which will click two elements in sequence.
// If second is not clickable then click first element again
