const protractor = require('protractor');
const EC = protractor.ExpectedConditions;
const protractorConf = require('../../../../conf/protractor.conf');

const fluentWait = protractorConf.timeouts.fluentWait;
var fs = require('fs');

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
     },elementToBeNotVisible: element =>{
      browser.wait(EC.not(EC.presenceOf(element)), fluentWait, "Element \"" + element.locator() + "\" is present");
    },textToBePresent:(element, value)=>{
      browser.wait(EC.textToBePresentInElement(element, value), fluentWait);
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
  },
  dragAndDrop(dragElement, dropElement) {
    // You can also use the `dragAndDrop` convenience action.
    browser.actions().dragAndDrop(dragElement, dropElement).mouseUp().perform();
  },
  openBaseUrl() {
    browser.get(protractorConf.config.baseUrl);
  },
  logOutByClearingLocalStorage() {
    //browser.executeScript('window.sessionStorage.clear();');
     browser.executeScript('window.localStorage.clear();')
  },
  scrollIntoView(element) {
    arguments[0].scrollIntoView();
  },
  writeScreenShot(data, filename) {
    var stream = fs.createWriteStream(filename);
    stream.write(new Buffer(data, 'base64'));
    stream.end();
  },
  getAnalysisIdFromUrl(url){
    let ulrParts = url.split("analyze/analysis/")[1];
    return ulrParts.split("/")[0];
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
