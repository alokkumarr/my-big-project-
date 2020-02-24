const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
var appRoot = require('app-root-path');
var retry = require('protractor-retry').retry;
var JSONReporter = require('jasmine-bamboo-reporter');
var fs = require('fs');
var argv = require('yargs').argv;
const webpackHelper = require('./webpack.helper');
const logger = require('./v2/conf/logger')(__filename);
const SuiteSetup = require('./v2/helpers/SuiteSetup');
const Constants = require('./v2/helpers/Constants');
/**
 * Note about intervals:
 * Defined to be dependent on environment where tests are executed. Running against distribution package in CI requires
 * extended timeouts
 */

/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 */
const pageLoadTimeout = webpackHelper.distRun() ? 300000 : 150000;

/**
 * Specifies the amount of time the driver should wait when searching for an element if it is not immediately present.
 */

const implicitlyWait = webpackHelper.distRun() ? 40000 : 30000;
const extendedImplicitlyWait = webpackHelper.distRun() ? 40000 : 30000; //30000 // = 30 sec; Sometimes element will not
// appear so fast

/**
 * Defines the maximum amount of time to wait for a condition
 */
const fluentWait = webpackHelper.distRun() ? 30000 : 20000;
const extendedFluentWait = webpackHelper.distRun() ? 60000 : 40000;

/**
 * Default time to wait in ms before a test fails
 * Fixes error: jasmine default timeout interval
 */
const defaultTimeoutInterval = webpackHelper.distRun() ? 600000 : 300000;
// = 30 | 5 min. Sometimes test can execute for a long time
const extendedDefaultTimeoutInterval = webpackHelper.distRun()
  ? 12600000
  : 10800000;

/**
 * Fixes error: Timed out waiting for asynchronous Angular tasks to finish after n seconds;
 * If fluentWait is happening more than this timeout it will throw an error like "element is not clickable"
 */
const allScriptsTimeout = webpackHelper.distRun() ? 12600000 : 10800000;
/**
 * number of failed retry
 */
let maxRetryForFailedTests = webpackHelper.distRun() ? 3 : 2;

/**
 * Waits ms after page is loaded
 */
const pageResolveTimeout = 1000;

/**
 * Note: Prefix with "../saw-web" because end-to-end tests are invoked from "dist" when run against the
 * distribution package. The same path also works when run directly out of "saw-web".
 */
const testBaseDir = appRoot + '/e2e/src/';

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
let run = SuiteSetup.islocalRun();
let token;

exports.timeouts = {
  fluentWait: fluentWait,
  extendedDefaultTimeoutInterval: extendedDefaultTimeoutInterval,
  extendedImplicitlyWait: extendedImplicitlyWait,
  pageResolveTimeout: pageResolveTimeout,
  extendedFluentWait: extendedFluentWait,
  tempts: tempts
};

exports.config = {
  framework: 'jasmine2',
  getPageTimeout: pageLoadTimeout,
  allScriptsTimeout: allScriptsTimeout,
  customerCode: customerCode,
  useAllAngular2AppRoots: true,
  testData: webpackHelper.getTestData(),
  directConnect: true, // this runs selenium server on the fly and it has faster execution + parallel execution efficiently
  //and tests are more stable with local server started instead of directConnection.
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
    defaultTimeoutInterval: extendedDefaultTimeoutInterval,
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
     */
    sanity: [
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
      testBaseDir +
      'observe/createAndDeleteDashboardWithActualVsTargetKpi.test.js',
      testBaseDir + 'observe/createAndDeleteDashboardWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilter.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithPivot.test.js',
      testBaseDir + 'observe/dashboardGlobalFilterWithESReport.test.js',
      testBaseDir + 'workbench/bis/createAndDeleteChannel.test.js',
      testBaseDir + 'workbench/bis/updateAndDeleteChannel.test.js',
      testBaseDir + 'workbench/bis/activateDeActivateChannel.test.js',
      testBaseDir + 'workbench/bis/createAndDeleteRoute.test.js',
      testBaseDir + 'workbench/bis/activateAndDeActivateRoute.test.js',
      testBaseDir + 'workbench/bis/scheduleRoute.test.js'
    ],
    /**
     * This suite is for development environment and always all dev tests will be executed.
     */
    development: [
      testBaseDir + 'dummyDevelopmentTests1.js',
      testBaseDir + 'dummyDevelopmentTests2.js'
    ]
  },
  onCleanUp: function(results) {
    retry.onCleanUp(results);
  },
  onPrepare() {
    retry.onPrepare();

    //console.log('Running instance at '+ new Date());
    jasmine.getEnv().addReporter(
      new SpecReporter({
        displayStacktrace: true,
        displaySpecDuration: true,
        displaySuiteNumber: true
      })
    );

    browser
      .manage()
      .timeouts()
      .pageLoadTimeout(pageLoadTimeout);
    browser
      .manage()
      .timeouts()
      .implicitlyWait(2000);

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

    jasmine.getEnv().addReporter(
      new JSONReporter({
        file: 'target/jasmine-results.json', // by default it writes to jasmine.json
        beautify: true,
        indentationLevel: 4 // used if beautify === true
      })
    );

    jasmine.getEnv().addReporter(junitReporter);

    var AllureReporter = require('jasmine-allure-reporter');
    jasmine.getEnv().addReporter(
      new AllureReporter({
        resultsDir: 'target/allure-results'
      })
    );
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

    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.baseUrl = JSON.parse(
      fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/url.json', 'utf8')
    ).baseUrl;

    browser.get(browser.baseUrl);
    return browser.wait(() => {
      return browser.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, pageResolveTimeout);
  },
  beforeLaunch: function() {
    process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0;
    //clean up any residual/leftover from a previous run. Ensure we have clean
    //files for both locking and merging.
    if (fs.existsSync('jasmine-results.json.lock')) {
      fs.unlinkSync('jasmine-results.json.lock');
    }
    if (fs.existsSync('jasmine-results.json')) {
      fs.unlink('jasmine-results.json');
    }
    if (!run.localRun || (run.localRun && run.firstRun)) {
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

        let APICommonHelpers = require('./v2/helpers/api/APICommonHelpers');

        let apiBaseUrl = APICommonHelpers.getApiUrl(appUrl);
        console.log('api data -----' + apiBaseUrl);
        let token = APICommonHelpers.generateToken(apiBaseUrl);

        if (!token) {
          logger.error(
            'cleanup and setup stage : Token generation failed hence marking test suite failure, Please refer the logs for more information.'
          );
          process.exit(1);
        }
        let TestDataGenerator = require('./v2/helpers/data-generation/TestDataGenerator');
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
    }
  },
  afterLaunch: function() {
    process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 1;
    if (!run.localRun) {
      if (fs.existsSync('target/e2e/e2eId.json')) {
        // delete and create new always
        //console.log('deleting e2e id json file....')
        fs.unlinkSync('target/e2e/e2eId.json');
      }

      let retryCounter = 1;
      if (argv.retry) {
        retryCounter = ++argv.retry;
      }

      webpackHelper.generateFailedTests('target/allure-results');

      let retryStatus = retry.afterLaunch(maxRetryForFailedTests);
      if (retryStatus === 1) {
        // retryStatus 1 means there are some failures & there are no retry left, hence mark test suite failure
        let failedTests;
        if (fs.existsSync('target/e2e/testData/failed/finalFail.json')) {
          failedTests = JSON.parse(
            fs.readFileSync('target/e2e/testData/failed/finalFail.json', 'utf8')
          );
        }
        logger.error(
          'There are some failures hence marking test suite failed. Failed tests are: ' +
          JSON.stringify(failedTests)
        );
        process.exit(1);
      } else if (retryStatus === 0) {
        // retryStatus 0 means there are no failures but to be double sure check the finalfail json file
        if (fs.existsSync('target/e2e/testData/failed/finalFail.json')) {
          let failedTests = JSON.parse(
            fs.readFileSync('target/e2e/testData/failed/finalFail.json', 'utf8')
          );
          logger.error(
            'There are some failures hence marking test suite failed. Failed tests are: ' +
            JSON.stringify(failedTests)
          );
          process.exit(1);
        }
      }

      return retryStatus;
    }
  }
};
