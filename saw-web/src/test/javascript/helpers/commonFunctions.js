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
    //Disabled because can't catch overlay by other element
    /*elementToBeClickableAndClick: element => {
     browser.wait(EC.elementToBeClickable(element), fluentWait, "Element \"" + element.locator() + "\" is not clickable");
     },*/
    //Eliminates error: is not clickable at point
    elementToBeClickableAndClick: element => {
      let count = 0;
      click(element, count);
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

function click(element, i) {
  element.click().then(
    function () {
    }, function (err) {
      if (err) {
        console.log("Element '" + element.locator() + "' is not clickable. Retrying. Tempts done: " + (i + 1));
        i++;
        browser.sleep(1000);
        if (i < protractorConf.timeouts.tempts) {
          click(element, i);
        } else {
          throw new Error("Element '" + element.locator() + "' is not clickable after " +
            protractorConf.timeouts.tempts + " tries. Error: " + err);
        }
      }
    });
}
