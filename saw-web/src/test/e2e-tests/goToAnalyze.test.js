const protractor = require('protractor');
const login = require('../javascript/pages/loginPage.po.js');
const analyze = require('../javascript/pages/analyzePage.po.js');
const ec = protractor.ExpectedConditions;

describe('should go to Analyze page after landing on home page', () => {
  afterAll(function() {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('login as admin', () => {
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
