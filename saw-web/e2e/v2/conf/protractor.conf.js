var appRoot = require('app-root-path');
var retry = require('protractor-retry').retry;
var fs = require('fs');
var argv = require('yargs').argv;
const SuiteHelper = require('../helpers/SuiteSetup');

/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 */
const pageLoadTimeout = 30000;

const implicitlyWait = 1000; //should not be more than 5 seconds, this will increase over all execution time

/**
 * Explicit wait for element
 * 30 seconds in docker and 25 seconds
 */
const fluentWait = new SuiteHelper().distRun() ? 30000 : 25000;

/**
 * Before performing any action, Protractor waits until there are no pending asynchronous tasks in your Angular
 * application. This means that all timeouts and http requests are finished.
 * https://github.com/angular/protractor/blob/master/docs/timeouts.md
 * https://github.com/angular/protractor/blob/master/lib/config.ts
 * 5 min in docker and 3 min in local -- All http/https calls should be completed
 */
const allScriptsTimeout = new SuiteHelper().distRun() ? 300000 : 180000;

/**
 *  https://github.com/angular/protractor/blob/master/docs/timeouts.md
 * https://github.com/angular/protractor/blob/master/lib/config.ts
 * Max total time before throwing NO ACTIVE SESSION_ID
 * 60 min
 */
const timeoutInterval = 3600000

/**
 * number of retries in case of failure, executes all failed tests
 */
let maxRetryForFailedTests = new SuiteHelper().distRun() ? 3 : 2;

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
  framework: 'jasmine2', allScriptsTimeout: allScriptsTimeout, customerCode: customerCode, useAllAngular2AppRoots: true,
  testData: new SuiteHelper().getTestData(),
  baseUrl: 'http://localhost:3000',
  capabilities: {
    browserName: 'chrome',
    shardTestFiles: true,
    maxInstances: 10,
    chromeOptions: {
      args: ['disable-extensions', 'disable-web-security', '--start-fullscreen', // enable for Mac OS
        '--headless', // start on background
        '--disable-gpu', '--window-size=2880,1800']
    }, 'moz:firefoxOptions': {
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
  }, suites: {
    /**
     * This suite will be run as part of main bamboo build plan.
     */
    smoke: [testBaseDir + 'login.test.js'], /**
     * This suite will be triggered from QA Test bamboo plan frequently for sanity check
     */
    sanity: [testBaseDir + 'login.test.js', testBaseDir + 'createReport.test.js', testBaseDir + 'charts/createAndDeleteCharts.test.js'],
    /**
     * This suite will be triggered from QA Test bamboo plan frequently for full regression as daily basis
     */
    critical: [// login logout tests
      testBaseDir + 'login.test.js', testBaseDir + 'priviliges.test.js', testBaseDir + 'analyze.test.js', testBaseDir + 'createReport.test.js', // charts tests
      testBaseDir + 'charts/applyFiltersToCharts.js', testBaseDir + 'charts/createAndDeleteCharts.test.js', testBaseDir + 'charts/previewForCharts.test.js', // chartEditFork tests
      testBaseDir + 'charts/editAndDeleteCharts.test.js', testBaseDir + 'charts/forkAndEditAndDeleteCharts.test.js', // filters tests
      testBaseDir + 'promptFilter/chartPromptFilters.test.js', testBaseDir + 'promptFilter/esReportPromptFilters.test.js', testBaseDir + 'promptFilter/pivotPromptFilters.test.js', testBaseDir + 'promptFilter/reportPromptFilters.test.js', // pivots tests
      testBaseDir + 'pivots/pivotFilters.test.js', // Observe module test cases
      testBaseDir + 'observe/createAndDeleteDashboardWithCharts.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithESReport.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithPivot.test.js', testBaseDir + 'observe/dashboardGlobalFilter.test.js', testBaseDir + 'observe/dashboardGlobalFilterWithPivot.test.js', testBaseDir + 'observe/dashboardGlobalFilterWithESReport.test.js'],
    regression: [// login logout tests
      testBaseDir + 'login.test.js', testBaseDir + 'priviliges.test.js', testBaseDir + 'analyze.test.js', testBaseDir + 'createReport.test.js', // charts tests
      testBaseDir + 'charts/applyFiltersToCharts.js', testBaseDir + 'charts/createAndDeleteCharts.test.js', testBaseDir + 'charts/previewForCharts.test.js', // chartEditFork tests
      testBaseDir + 'charts/editAndDeleteCharts.test.js', testBaseDir + 'charts/forkAndEditAndDeleteCharts.test.js', // filters tests
      testBaseDir + 'promptFilter/chartPromptFilters.test.js', testBaseDir + 'promptFilter/esReportPromptFilters.test.js', testBaseDir + 'promptFilter/pivotPromptFilters.test.js', testBaseDir + 'promptFilter/reportPromptFilters.test.js', // pivots tests
      testBaseDir + 'pivots/pivotFilters.test.js', // Observe module test cases
      testBaseDir + 'observe/createAndDeleteDashboardWithCharts.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithESReport.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js', testBaseDir + 'observe/createAndDeleteDashboardWithPivot.test.js', testBaseDir + 'observe/dashboardGlobalFilter.test.js', testBaseDir + 'observe/dashboardGlobalFilterWithPivot.test.js', testBaseDir + 'observe/dashboardGlobalFilterWithESReport.test.js'],
    /**
     * This suite is for development environment and always all dev tests will be executed.
     */
    development: [testBaseDir + 'dev1.js', testBaseDir + 'dev2.js']
  },
  onCleanUp: function (results) {
    retry.onCleanUp(results);
  },
  onPrepare() {
    retry.onPrepare();

    browser.manage().timeouts().pageLoadTimeout(pageLoadTimeout);
    browser.manage().timeouts().implicitlyWait(implicitlyWait);

    let jasmineReporters = require('jasmine-reporters');
    let junitReporter = new jasmineReporters.JUnitXmlReporter({
      savePath: protractorPath, consolidateAll: true
    });
    jasmine.getEnv().addReporter(junitReporter);

    let AllureReporter = require('jasmine-allure-reporter');
    jasmine.getEnv().addReporter(new AllureReporter({
      resultsDir: 'target/allure-results'
    }));
    jasmine.getEnv().afterEach(function (done) {
      browser.takeScreenshot().then(function (png) {
        allure.createAttachment('Screenshot', function () {
          return new Buffer(png, 'base64');
        }, 'image/png')();
        done();
      });
    });

    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.get(browser.baseUrl);
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, pageResolveTimeout);
  },
  beforeLaunch: function () {
    //console.log('beforeLaunch....generating the testdata...')
    // Generate test data
    if (new SuiteHelper().getSawWebUrl()) {
      const generate = require('../helpers/data-generation/generateTestData');
      token = generate.token(new SuiteHelper().getSawWebUrl());
      generate.usersRolesPrivilegesCategories(token);
    } else {
      process.exit(1);
    }
  },
  afterLaunch: function () {
    //console.log('afterLaunch....')
    if (fs.existsSync('target/e2eId.json')) {
      // delete and create new always
      //console.log('deleting e2e id json file....')
      fs.unlinkSync('target/e2eId.json');
    }

    var retryCounter = 1;
    if (argv.retry) {
      retryCounter = ++argv.retry;
    }
    if (retryCounter <= maxRetryForFailedTests) {
      // console.log('Generating failed tests supporting data if there are any failed tests then those will be retried again.....');
      new SuiteHelper().generateFailedTests('target/allure-results');
    }

    return retry.afterLaunch(maxRetryForFailedTests);
  }
};
