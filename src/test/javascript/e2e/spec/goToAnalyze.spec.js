const protractor = require('protractor');
const login = require('../pages/common/login.po.js');
const analyze = require('../pages/common/analyze.po.js');
const ec = protractor.ExpectedConditions;

describe('create a new columnChart type analysis', () => {
  it('login as admin', () => {
    browser.sleep(2000);
    expect(browser.getCurrentUrl()).toContain('/login');
    login.loginAs('admin');
  });

  it('should automatically redirect to Analyze page when going to the homepage', () => {
    // browser.driver.get('http://localhost:3000');
    // the app should automatically navigate to the analyze page
    // and when its on there the current module link is disabled
    const alreadyOnAnalyzePage = ec.urlContains('/analyze');

    // wait for the app to automatically navigate to the default page
    browser
      .wait(() => alreadyOnAnalyzePage, 1000)
      .then(() => expect(browser.getCurrentUrl()).toContain('/analyze'));
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });
});
