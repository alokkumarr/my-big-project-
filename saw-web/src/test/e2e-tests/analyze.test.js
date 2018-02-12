/*
  Created by Alex
 */

const loginPage = require('../javascript/pages/loginPage.po.js');
const analyzePage = require('../javascript/pages/analyzePage.po.js');
const users = require('../javascript/data/users.js');
const using = require('jasmine-data-provider');
const ec = protractor.ExpectedConditions;
const protractorConf = require('../../../../saw-web/conf/protractor.conf');

describe('Verify basic functionality on Analyze page: analyze.test.js', () => {

  //Prerequisites: two users should exist with user types: admin and user
  const userDataProvider = {
    'admin': {user: users.admin.loginId},
    'user': {user: users.userOne.loginId},
  };

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
  });

  beforeEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      expect(browser.getCurrentUrl()).toContain('/login');
      done();
    }, protractorConf.timeouts.pageResolveTimeout)
  });

  afterEach(function (done) {
    setTimeout(function () {
      browser.waitForAngular();
      analyzePage.main.doAccountAction('logout');
      done();
    }, protractorConf.timeouts.pageResolveTimeout)
  });

    using(userDataProvider, function (data, description) {
      it('should display list view by default by ' + description, function () {
        expect(browser.getCurrentUrl()).toContain('/login');
        loginPage.userLogin(data.user, users.anyUser.password);
        analyzePage.validateListView();
      });

    it(description + ' should land on analyze page', function () {
      loginPage.userLogin(data.user, users.anyUser.password);
      // the app should automatically navigate to the analyze page
      // and when its on there the current module link is disabled
      const alreadyOnAnalyzePage = ec.urlContains('/analyze');

      // wait for the app to automatically navigate to the default page
      browser
        .wait(() => alreadyOnAnalyzePage, protractorConf.timeouts.pageResolveTimeout)
        .then(() => expect(browser.getCurrentUrl()).toContain('/analyze'));
    });
  });
});
