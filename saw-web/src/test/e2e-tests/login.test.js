const login = require('../javascript/pages/common/login.po.js');
const header = require('../javascript/pages/components/header.co.js');
const analyze = require('../javascript/pages/common/analyze.po.js');

describe('Login Tests', () => {

  afterAll(function() {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
  });

  it('should land on login page', () => {
    expect(browser.getCurrentUrl()).toContain('/login');
  });

  it('should enter valid credentials and attempt to login', () => {
    login.userLogin('sawadmin@synchronoss.com', 'Sawsyncnewuser1!');
  });

  it('should be successfully logged in', () => {
    expect(header.headerElements.companyLogo.isPresent()).toBeTruthy();
  });

  it('should log out', () => {
    analyze.main.doAccountAction('logout');
  });

});