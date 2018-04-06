const protractor = require('../../../../saw-web/conf/protractor.conf');

describe('BrowserStack Local Testing', function() {
  it('can check tunnel working', function() {
    browser.driver.get('https://sawdev-bda-velocity-vacum-np.sncrcorp.net/').then(function() {
      console.log("link opened");
      browser.sleep(10000);
    });
  });
});
