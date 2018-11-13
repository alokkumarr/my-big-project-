var appRoot = require('app-root-path');
var retry = require('protractor-retry').retry;
var fs = require('fs');
var argv = require('yargs').argv;
const SuiteSetup = require('../helpers/SuiteSetup');

/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 */
const pageLoadTimeout = 30000;

const implicitlyWait = 1000; //should not be more than 5 seconds, this will increase over all execution time

/**
 * Explicit wait for element
 * 30 seconds in docker and 20 seconds in local
 */
const fluentWait = SuiteSetup.distRun() ? 30000 : 20000;

/**
 * Before performing any action, Protractor waits until there are no pending asynchronous tasks in your Angular
 * application. This means that all timeouts and http requests are finished.
 * https://github.com/angular/protractor/blob/master/docs/timeouts.md
 * https://github.com/angular/protractor/blob/master/lib/config.ts
 * 5 min in docker and 3 min in local -- All http/https calls should be completed
 */
const allScriptsTimeout = SuiteSetup.distRun() ? 300000 : 180000;

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
let maxRetryForFailedTests = SuiteSetup.distRun() ? 3 : 2;

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
  testData: SuiteSetup.getTestData(),
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
    smoke: [testBaseDir + 'login.test.js'],
    /**
     * This suite will be triggered from QA Test bamboo plan frequently for sanity check
     */ sanity: [
      testBaseDir + 'login.test.js',
      testBaseDir + 'createReport.test.js',
      testBaseDir + 'charts/createAndDeleteCharts.test.js'
    ],
    /**
     * This suite will be triggered from QA Test bamboo plan frequently for full regression as daily basis
     */
    critical: [
      // login logout tests
      testBaseDir + 'login.test.js',
      testBaseDir + 'priviliges.test.js',
      testBaseDir + 'analyze.test.js',
      testBaseDir + 'createReport.test.js', // charts tests
      testBaseDir + 'charts/applyFiltersToCharts.js',
      testBaseDir + 'charts/createAndDeleteCharts.test.js',
      testBaseDir + 'charts/previewForCharts.test.js', // chartEditFork tests
      testBaseDir + 'charts/editAndDeleteCharts.test.js',
      testBaseDir + 'charts/forkAndEditAndDeleteCharts.test.js', // filters tests
      testBaseDir + 'promptFilter/chartPromptFilters.test.js',
      testBaseDir + 'promptFilter/esReportPromptFilters.test.js',
      testBaseDir + 'promptFilter/pivotPromptFilters.test.js',
      testBaseDir + 'promptFilter/reportPromptFilters.test.js', // pivots tests
      testBaseDir + 'pivots/pivotFilters.test.js', // Observe module test cases
      testBaseDir + 'observe/createAndDeleteDashboardWithCharts.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithESReport.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js',
      testBaseDir +
        'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilter.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithESReport.test.js'
    ],
    regression: [
      // login logout tests
      testBaseDir + 'login.test.js',
      testBaseDir + 'priviliges.test.js',
      testBaseDir + 'analyze.test.js',
      testBaseDir + 'createReport.test.js', // charts tests
      testBaseDir + 'charts/applyFiltersToCharts.js',
      testBaseDir + 'charts/createAndDeleteCharts.test.js',
      testBaseDir + 'charts/previewForCharts.test.js', // chartEditFork tests
      testBaseDir + 'charts/editAndDeleteCharts.test.js',
      testBaseDir + 'charts/forkAndEditAndDeleteCharts.test.js', // filters tests
      testBaseDir + 'promptFilter/chartPromptFilters.test.js',
      testBaseDir + 'promptFilter/esReportPromptFilters.test.js',
      testBaseDir + 'promptFilter/pivotPromptFilters.test.js',
      testBaseDir + 'promptFilter/reportPromptFilters.test.js', // pivots tests
      testBaseDir + 'pivots/pivotFilters.test.js', // Observe module test cases
      testBaseDir + 'observe/createAndDeleteDashboardWithCharts.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithESReport.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js',
      testBaseDir +
        'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilter.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithESReport.test.js'
    ],
    /**
     * This suite is for development environment and always all dev tests will be executed.
     */
    development: [
      testBaseDir + 'dev1.js',
      testBaseDir + 'dev2.js']
  },
  onCleanUp: function(results) {
    //retry.onCleanUp(results);
  },
  onPrepare() {
    //retry.onPrepare();

    browser
      .manage()
      .timeouts()
      .pageLoadTimeout(pageLoadTimeout);

    browser
      .manage()
      .timeouts()
      .implicitlyWait(implicitlyWait);

    let jasmineReporters = require('jasmine-reporters');
    let junitReporter = new jasmineReporters.JUnitXmlReporter({
      savePath: protractorPath,
      consolidateAll: true
    });

    jasmine.getEnv().addReporter(junitReporter);

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

    jasmine.getEnv().addReporter(new function() {
      this.specDone = function(result) {
        if (result.status !== 'passed') {
          //console.log(`Failed test...${JSON.stringify(result)}`)
          // write failed test to json file.
          new SuiteSetup().failedTestData(result.testInfo)
        }
      };
    });

    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.get('http://juliemr.github.io/protractor-demo/');
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return true;
      });
    }, pageResolveTimeout);
  },
  beforeLaunch: function() {
    // Delete old e2e unique id.
    // if (fs.existsSync('target/e2eId.json')) {
    //   fs.unlinkSync('target/e2eId.json');
    // }
    // Generate test data
    let appUrl = SuiteSetup.getSawWebUrl();
    // if (appUrl) {
    //   console.log('Generating test data');
    //   let APICommonHelpers = require('../helpers/api/APICommonHelpers');
    //   let apiBaseUrl = APICommonHelpers.getApiUrl(appUrl);
    //   let token = APICommonHelpers.generateToken(apiBaseUrl);
    //   let TestDataGenerator = require('../helpers/data-generation/TestDataGenerator');
    //   new TestDataGenerator().generateUsersRolesPrivilegesCategories(
    //     apiBaseUrl,
    //     token
    //   );
    // } else {
    //   process.exit(1);
    // }
  },
  afterLaunch: function() {
    var retryCounter = 1;
    if (argv.retry) {
      retryCounter = ++argv.retry;
    }
    /**
     * get a copy of old
     */
    // if (retryCounter <= maxRetryForFailedTests) {
    //   // console.log('Generating failed tests supporting data if there are any failed tests then those will be retried again.....');
    //   SuiteSetup.generateFailedTests('target/allure-results');
    // }

    //return retry.afterLaunch(maxRetryForFailedTests);
  }
};
