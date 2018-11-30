'use strict';
var appRoot = require('app-root-path');
var retry = require('protractor-retry').retry;
var fs = require('fs');
var argv = require('yargs').argv;
const SuiteSetup = require('../helpers/SuiteSetup');
const logger = require('./logger')(__filename);
const testSuite = require('./testSuites');
/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 * maximum time to wait for a page load i.e. when user refresh page/ first time open browser and type saw app url
 */
const pageLoadTimeout = 30000;  //-- !!!DON'T CHANGE because it will increase overall test execution time

const implicitlyWait = 1000; //should not be more than 5 seconds, this will impact over all execution time

/**
 * This is maximum time to wait for an element visible
 * if element is not visible within this time then protractor will throw timeout error & mark test as failure
 * and continue to execute other test.
 * 30 seconds in docker and 20 seconds in local
 */
const fluentWait = SuiteSetup.distRun() ? 30000 : 20000;  //-- !!!DON'T CHANGE because it will impact overall test execution time

/**
 * Before performing any action, Protractor waits until there are no pending asynchronous tasks in your Angular
 * application. This means that all timeouts and http requests are finished.
 * https://github.com/angular/protractor/blob/master/docs/timeouts.md
 * https://github.com/angular/protractor/blob/master/lib/config.ts
 * 3 min -- All http/https calls should be completed -- !!!DON'T CHANGE because it will impact overall test execution time
 */
const allScriptsTimeout = 180000;

/**
 *  https://github.com/angular/protractor/blob/master/docs/timeouts.md
 * https://github.com/angular/protractor/blob/master/lib/config.ts
 * Max total time before throwing NO ACTIVE SESSION_ID
 * 60 min
 */
const timeoutInterval = 3600000;

/**
 * number of retries in case of failure, executes all failed tests
 */
let maxRetryForFailedTests = SuiteSetup.distRun() ? 1 : 1;

/**
 * Waits ms after page is loaded
 */
const pageResolveTimeout = 1000;

/**
 * Note: Prefix with "../saw-web" because end-to-end tests are invoked from "dist" when run against the
 * distribution package. The same path also works when run directly out of "saw-web".
 */
const testBaseDir = appRoot + '/e2e/v2/tests/';

/**
 * Output path for the junit reports. Folder should be created in advance
 */
const protractorPath = 'target/protractor-reports';
/**
 * All tests are running for customer
 */
const customerCode = 'SYNCHRONOSS';

let token;

exports.timeouts = {
  fluentWait: fluentWait,
  pageResolveTimeout: pageResolveTimeout
};

exports.config = {
  framework: 'jasmine2',
  allScriptsTimeout: allScriptsTimeout,
  customerCode: customerCode,
  useAllAngular2AppRoots: true,
  baseUrl: 'http://localhost:3000',
  logger: logger,
  capabilities: {
    browserName: 'chrome',
    shardTestFiles: true,
    maxInstances: 10,
    chromeOptions: {
      args: [
        'disable-extensions',
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        '--headless', // start on background
        '--disable-gpu',
        '--window-size=2880,1800'
      ]
    },
    'moz:firefoxOptions': {
      args: ['--headless']
    }
  },
  jasmineNodeOpts: {
    defaultTimeoutInterval: timeoutInterval,
    isVerbose: true,
    showTiming: true,
    includeStackTrace: true,
    realtimeFailure: true,
    showColors: true
  },
  suites: {
    /**
     * This suite will be run as part of main bamboo build plan.
     */
    smoke: testSuite.SMOKE, /**
     * This suite will be triggered from main bamboo plan frequently for sanity check
     */
    sanity: testSuite.SANITY, /**
     * This suite will be triggered from main bamboo plan and runs on every commit
     * Contains all feature with minimal data set
     */
    critical: testSuite.CRITICAL, /**
     * This suite will be triggered from e2e bamboo plan and dev team has to run this plan manually to verify that their
     * changes are not causing any issues to app.
     * Bamboo plan url: https://bamboo.synchronoss.net:8443/browse/BDA-TSA
     * Contains all feature with minimal data set
     */
    regression: testSuite.REGRESSION, /**
     * This suite is for developing new tests and quickly debug if something breaking
     */
    development: testSuite.DEVELOPMENT
  },
  onCleanUp: function (results) {
    retry.onCleanUp(results);
  },
  onPrepare() {
    retry.onPrepare();

    browser.manage().timeouts().pageLoadTimeout(pageLoadTimeout);

    browser.manage().timeouts().implicitlyWait(implicitlyWait);
    // Allure reporter start
    let AllureReporter = require('jasmine-allure-reporter');
    jasmine.getEnv().addReporter(
      new AllureReporter({
        resultsDir: 'target/allure-results'
      })
    );
    // Add screenshot to allure report. screenshot are taken after each test
    jasmine.getEnv().afterEach(function (done) {
      browser.takeScreenshot().then(function (png) {
        allure.createAttachment(
          'Screenshot',
          function () {
            return new Buffer(png, 'base64');
          },
          'image/png'
        )();
        done();
      });
    });
    // Allure reporter done

    // Get failed test data
    jasmine.getEnv().addReporter(new function () {
      this.specDone = function (result) {
        if (result.status !== 'passed') {
          new SuiteSetup().failedTestData(result.testInfo)
        }
      };
    });
    browser.get(browser.baseUrl);
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, pageResolveTimeout);
  },
  beforeLaunch: function () {
    //Delete old e2e unique id.
    logger.info('Doing cleanup and setting up test data for e2e tests....');
    if (fs.existsSync('target/e2eId.json')) {
      fs.unlinkSync('target/e2eId.json');
    }
    // Generate test data
    let appUrl = SuiteSetup.getSawWebUrl();

    if (!appUrl) {
      logger.error('appUrl can not be null or undefined hence exiting the e2e suite...appUrl:' + appUrl
        + ', hence exiting test suite and failing it...');
      process.exit(1);
    }

    try {
      logger.info('Generating test for this run...');

      let APICommonHelpers = require('../helpers/api/APICommonHelpers');

      let apiBaseUrl = APICommonHelpers.getApiUrl(appUrl);
      let token = APICommonHelpers.generateToken(apiBaseUrl);

      if (!token) {
        logger.error('cleanup and setup stage : Token generation failed hence marking test suite failure, Please refer the logs for more information.');
        process.exit(1);
      }
      let TestDataGenerator = require('../helpers/data-generation/TestDataGenerator');
      new TestDataGenerator().generateUsersRolesPrivilegesCategories(apiBaseUrl, token);

    } catch (e) {
      logger.error('There is some error during cleanup and setting up test data for e2e tests, ' +
        'hence exiting test suite and failing it....' + e);
      process.exit(1);
    }
  },
  afterLaunch: function () {
    var retryCounter = 1;
    if (argv.retry) {
      retryCounter = ++argv.retry;
    }
    // Rename failedFailedTests.json to
    if (retryCounter <= maxRetryForFailedTests) {
      // console.log('Generating failed tests supporting data if there are any failed tests then those will be retried again.....');
      SuiteSetup.failedTestDataForRetry();

    }
    return retry.afterLaunch(maxRetryForFailedTests);
  }
};
