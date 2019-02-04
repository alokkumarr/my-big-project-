'use strict';
var appRoot = require('app-root-path');
var retry = require('protractor-retry').retry;
var fs = require('fs');
var argv = require('yargs').argv;
const SuiteSetup = require('../helpers/SuiteSetup');
const logger = require('./logger')(__filename);
const testSuites = require('./testSuites');
/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 * maximum time to wait for a page load i.e. when user refresh page/ first time open browser and type saw app url
 */
const pageLoadTimeout = 30000; //-- !!!DON'T CHANGE because it will increase overall test execution time

const implicitlyWait = 1000; //should not be more than 5 seconds, this will impact over all execution time

/**
 * This is maximum time to wait for an element visible
 * if element is not visible within this time then protractor will throw timeout error & mark test as failure
 * and continue to execute other test.
 * 30 seconds in docker and 20 seconds in local
 */
const fluentWait = SuiteSetup.distRun() ? 30000 : 20000; //-- !!!DON'T CHANGE because it will impact overall test execution time

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
let maxRetriesForFailedTests = SuiteSetup.distRun() ? 3 : 2;

/**
 * Waits ms after page is loaded
 */
const pageResolveTimeout = 1000;

/**
 * All tests are running for customer
 */
const customerCode = 'SYNCHRONOSS';

exports.timeouts = {
  fluentWait: fluentWait,
  pageResolveTimeout: pageResolveTimeout,
  timeoutInterval: timeoutInterval
};

exports.config = {
  framework: 'jasmine2',
  allScriptsTimeout: allScriptsTimeout,
  customerCode: customerCode,
  useAllAngular2AppRoots: true,
  baseUrl: 'http://localhost:3000',
  capabilities: {
    browserName: 'chrome',
    shardTestFiles: true,
    maxInstances: 10,
    chromeOptions: {
      args: [
        'disable-extensions',
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        //'--headless', // start on background
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
    smoke: testSuites.SMOKE,
    sanity: testSuites.SANITY,
    /**
     * This is default suite contains basic functionality aorund analyze + observe module
     * Gets triggered on each push
     */
    critical: testSuites.CRITICAL,
    /**
     * This suite is full regression and is triggered by e2e bamboo plan i.e
     * https://bamboo.synchronoss.net:8443/browse/BDA-TSA
     */
    regression: testSuites.REGRESSION,
    /**
     * This suite is for developing new tests and quickly debug if something breaking
     */
    development: testSuites.DEVELOPMENT
  },
  onCleanUp: results => {
    retry.onCleanUp(results);
  },
  onPrepare: () => {
    retry.onPrepare();

    browser
      .manage()
      .timeouts()
      .pageLoadTimeout(pageLoadTimeout);

    browser
      .manage()
      .timeouts()
      .implicitlyWait(implicitlyWait);

    // Allure reporter start
    let AllureReporter = require('jasmine-allure-reporter');
    jasmine.getEnv().addReporter(
      new AllureReporter({
        resultsDir: 'target/allure-results'
      })
    );
    // Add screenshot to allure report. screenshot are taken after each test
    jasmine.getEnv().afterEach(function(done) {
      browser.takeScreenshot().then(function(png) {
        allure.createAttachment(
          'Screenshot',
          function() {
            return new Buffer(png, 'base64');
          },
          'image/png'
        )();
        done();
      });
    });
    // Allure reporter done

    // Get failed test data
    jasmine.getEnv().addReporter(
      new function() {
        this.specDone = function(result) {
          if (result.status !== 'passed') {
            logger.debug('Test is failed: ' + JSON.stringify(result.testInfo));
            new SuiteSetup().failedTestData(result.testInfo);
          }
          // Add executed test status in a result.json file which contains pass and fail tests
          // This fill be used for converting to junit xml so that e2e results can display under test status in bamboo
          new SuiteSetup().addToExecutedTests(result);
        };
      }()
    );

    browser.get(browser.baseUrl);
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, pageResolveTimeout);
  },
  beforeLaunch: () => {
    logger.info('Doing cleanup and setting up test data for e2e tests....');
    // Generate test data
    let appUrl = SuiteSetup.getSawWebUrl();

    if (!appUrl) {
      logger.error(
        'appUrl can not be null or undefined hence exiting the e2e suite...appUrl:' +
          appUrl +
          ', hence exiting test suite and failing it...'
      );
      process.exit(1);
    }

    try {
      logger.info('Generating test for this run...');

      let APICommonHelpers = require('../helpers/api/APICommonHelpers');

      let apiBaseUrl = APICommonHelpers.getApiUrl(appUrl);
      let token = APICommonHelpers.generateToken(apiBaseUrl);

      if (!token) {
        logger.error(
          'cleanup and setup stage : Token generation failed hence marking test suite failure, Please refer the logs for more information.'
        );
        process.exit(1);
      }
      let TestDataGenerator = require('../helpers/data-generation/TestDataGenerator');
      new TestDataGenerator().generateUsersRolesPrivilegesCategories(
        apiBaseUrl,
        token
      );
    } catch (e) {
      logger.error(
        'There is some error during cleanup and setting up test data for e2e tests, ' +
          'hence exiting test suite and failing it....' +
          e
      );
      process.exit(1);
    }
  },
  afterLaunch: () => {
    // Delete old e2e unique id.
    if (fs.existsSync('target/e2e/e2eId.json')) {
      // delete and create new always
      fs.unlinkSync('target/e2e/e2eId.json');
    }
    SuiteSetup.failedTestDataForRetry();

    let retryStatus = retry.afterLaunch(maxRetriesForFailedTests);
    if (retryStatus === 1) {
      // retryStatus 1 means there are some failures & there are no retry left, hence mark test suite failure
      logger.error('There are some failures hence marking test suite failed');
      // TODO: Convert testResult json to junit.xml file
      SuiteSetup.convertJsonToJunitXml();
      process.exit(1); // this will mark build failure as well
    } else if (retryStatus === 0) {
      // retryStatus 0 means there are no failures
      // TODO: Convert testResult json to junit.xml file
      SuiteSetup.convertJsonToJunitXml();
    }
    return retryStatus;
  }
};
