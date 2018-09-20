var appRoot = require('app-root-path');
const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
const generate = require('../src/test/javascript/data/generateTestData');
var retry = require('protractor-retry').retry;
var JSONReporter = require('jasmine-bamboo-reporter');
var fs = require('fs');
var HtmlReporter = require('protractor-beautiful-reporter');
var argv = require('yargs').argv;

/**
 * Note about intervals:
 * Defined to be dependent on environment where tests are executed. Running against distribution package in CI requires
 * extended timeouts
 */


/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 */
const pageLoadTimeout = webpackHelper.distRun() ? 600000 : 500000;

/**
 * Specifies the amount of time the driver should wait when searching for an element if it is not immediately present.
 */

const implicitlyWait = webpackHelper.distRun() ? 40000 : 30000;
const extendedImplicitlyWait = webpackHelper.distRun() ? 40000 : 30000;//30000 // = 30 sec; Sometimes element will not
                                                                          // appear so fast

/**
 * Defines the maximum amount of time to wait for a condition
 */
const fluentWait = webpackHelper.distRun() ? 40000 : 30000;
const extendedFluentWait = webpackHelper.distRun() ? 60000 : 40000;

/**
 * Default time to wait in ms before a test fails
 * Fixes error: jasmine default timeout interval
 */
const defaultTimeoutInterval = webpackHelper.distRun() ? 600000 : 300000;
// = 30 | 5 min. Sometimes test can execute for a long time
const extendedDefaultTimeoutInterval = webpackHelper.distRun() ? 5400000 : 3600000;

/**
 * Fixes error: Timed out waiting for asynchronous Angular tasks to finish after n seconds;
 * If fluentWait is happening more than this timeout it will throw an error like "element is not clickable"
 */
const allScriptsTimeout = webpackHelper.distRun() ? 12600000 : 10800000;
/**
 * number of failed retry
 */
let maxRetryForFailedTests = webpackHelper.distRun() ? 4 : 3;

/**
 * Waits ms after page is loaded
 */
const pageResolveTimeout = 1000;

/**
 * Note: Prefix with "../saw-web" because end-to-end tests are invoked from "dist" when run against the
 * distribution package. The same path also works when run directly out of "saw-web".
 */
const testBaseDir = appRoot + '/src/test/e2e-tests/';

/**
 * Output path for the junit reports. Folder should be created in advance
 */
const protractorPath = 'target/protractor-reports';

/**
 * Amount of attempts to retry doing action on element
 */
const tempts = 10;

/**
 * All tests are running for customer
 */
const customerCode = 'SYNCHRONOSS';

let token;

exports.timeouts = {
  fluentWait: fluentWait,
  extendedDefaultTimeoutInterval: extendedDefaultTimeoutInterval,
  extendedImplicitlyWait: extendedImplicitlyWait,
  pageResolveTimeout: pageResolveTimeout,
  extendedFluentWait:extendedFluentWait,
  tempts: tempts
};

exports.config = {
  framework: 'jasmine2',
  getPageTimeout: pageLoadTimeout,
  allScriptsTimeout: allScriptsTimeout,
  customerCode:customerCode,
  useAllAngular2AppRoots: true,
  testData:webpackHelper.getTestData(),
  //directConnect: true, // this runs selenium server on the fly and it has faster execution + parallel execution efficiently
  //and tests are more stable with local server started instead of directConnection.
  baseUrl: 'http://localhost:3000',
  capabilities: {
    browserName: 'chrome',
    shardTestFiles: true,
    maxInstances: 15,
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
    defaultTimeoutInterval: defaultTimeoutInterval,
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
    smoke: [
      testBaseDir + 'login.test.js'
    ],
    /**
     * This suite will be triggered from QA Test bamboo plan frequently for sanity check
     */
    sanity: [
      testBaseDir + 'login.test.js',
      testBaseDir + 'createReport.test.js',
      testBaseDir + 'charts/createAndDeleteCharts.test.js'
    ],
    /**
     * This suite will be triggered from QA Test bamboo plan frequently for full regression as daily basis
     */
    regression: [
      // login logout tests
      testBaseDir + 'login.test.js',
      testBaseDir + 'priviliges.test.js',
      testBaseDir + 'analyze.test.js',
      testBaseDir + 'createReport.test.js',
      // charts tests
      testBaseDir + 'charts/applyFiltersToCharts.js',
      testBaseDir + 'charts/createAndDeleteCharts.test.js',
      testBaseDir + 'charts/previewForCharts.test.js',
      // chartEditFork tests
      testBaseDir + 'charts/editAndDeleteCharts.test.js',
      testBaseDir + 'charts/forkAndEditAndDeleteCharts.test.js',
      // filters tests
      testBaseDir + 'promptFilter/chartPromptFilters.test.js',
      testBaseDir + 'promptFilter/esReportPromptFilters.test.js',
      testBaseDir + 'promptFilter/pivotPromptFilters.test.js',
      testBaseDir + 'promptFilter/reportPromptFilters.test.js',
      // pivots tests
      testBaseDir + 'pivots/pivotFilters.test.js',
      // Observe module test cases
      testBaseDir + 'observe/createAndDeleteDashboardWithCharts.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithESReport.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithSnapshotKPI.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js',
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
      testBaseDir + 'dev2.js'
    ]
  },
  onCleanUp: function (results) {
    retry.onCleanUp(results);
  },
  onPrepare() {
    retry.onPrepare();

    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.getEnv().addReporter(new HtmlReporter({
      baseDirectory: 'target/screenshots',
      preserveDirectory: false
    }).getJasmine2Reporter());

    browser.manage().timeouts().pageLoadTimeout(pageLoadTimeout);
    browser.manage().timeouts().implicitlyWait(implicitlyWait);

    let jasmineReporters = require('jasmine-reporters');
    let junitReporter = new jasmineReporters.JUnitXmlReporter({
      savePath: protractorPath,

      // conslidate all true:
      //   output/junitresults.xml
      //
      // conslidate all set to false:
      //   output/junitresults-example1.xml
      //   output/junitresults-example2.xml
      consolidateAll: true
    });

    jasmine.getEnv().addReporter(new JSONReporter({
      file: 'target/jasmine-results.json', // by default it writes to jasmine.json
      beautify: true,
      indentationLevel: 4 // used if beautify === true
    }));

    jasmine.getEnv().addReporter(junitReporter);

    var AllureReporter = require('jasmine-allure-reporter');
    jasmine.getEnv().addReporter(new AllureReporter({
      resultsDir: 'target/allure-results'
    }));
    jasmine.getEnv().afterEach(function(done){
      browser.takeScreenshot().then(function (png) {
        allure.createAttachment('Screenshot', function () {
          return new Buffer(png, 'base64')
        }, 'image/png')();
        done();
      })
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
    // Generate test data
    token = generate.token(webpackHelper.getSawWebUrl());
    generate.usersRolesPrivilegesCategories(token);
  },
  afterLaunch: function() {

    var retryCounter = 1;
    if (argv.retry) {
      retryCounter = ++argv.retry;
    }
    if (retryCounter <= maxRetryForFailedTests){
     // console.log('Generating failed tests supporting data if there are any failed tests then those will be retried again.....');
      webpackHelper.generateFailedTests('target/allure-results');
    }

    return retry.afterLaunch(maxRetryForFailedTests);
  }
};
