const protractor = require('protractor');
const EC = protractor.ExpectedConditions;

module.exports = {
  waitFor: {
    elementToBeClickable: element => {
      return browser.wait(EC.elementToBeClickable(element), 10000);
    },
    elementToBeVisible: element => {
      return browser.wait(EC.visibilityOf(element), 10000);
    },
    elementToBePresent: element => {
      return browser.wait(EC.presenceOf(element), 10000);
    },
    // Possible options: /analyze/ , /login/
    pageToBeReady: pageName => {
      return browser.driver.wait(() => {
        return browser.driver.getCurrentUrl().then(url => {
          return pageName.test(url);
        });
      }, 10000);
    },
    cardsCountToUpdate: (cards, count) => {
      browser.wait(() => {
        return cards.then(() => {
          return cards.count().then(text => {
            return text === (count - 1);
          });
        });
      }, 10000);
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
