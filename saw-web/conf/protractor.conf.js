var appRoot = require('app-root-path');
const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
const generate = require('../src/test/javascript/data/generateTestData');
var retry = require('protractor-retry').retry;
var JSONReporter = require('jasmine-bamboo-reporter');
var fs = require('fs');
var HtmlReporter = require('protractor-beautiful-reporter');

/**
 * Note about intervals:
 * Defined to be dependent on environment where tests are executed. Running against distribution package in CI requires
 * extended timeouts
 */


/**
 * Sets the amount of time to wait for a page load to complete before returning an error.  If the timeout is negative,
 * page loads may be indefinite.
 */
const pageLoadTimeout = webpackHelper.distRun() ? 1200000 : 600000;

/**
 * Specifies the amount of time the driver should wait when searching for an element if it is not immediately present.
 */

const implicitlyWait = webpackHelper.distRun() ? 600000 : 20000;
const extendedImplicitlyWait = webpackHelper.distRun() ? 1200000 : 30000;//30000 // = 30 sec; Sometimes element will not
                                                                          // appear so fast

/**
 * Defines the maximum amount of time to wait for a condition
 */
const fluentWait = webpackHelper.distRun() ? 600000 : 20000;//20000

/**
 * Default time to wait in ms before a test fails
 * Fixes error: jasmine default timeout interval
 */
const defaultTimeoutInterval = webpackHelper.distRun() ? 600000 : 600000;//20000
// = 30 | 5 min. Sometimes test can execute for a long time
const extendedDefaultTimeoutInterval = webpackHelper.distRun() ? 3600000 : 3600000;

/**
 * Fixes error: Timed out waiting for asynchronous Angular tasks to finish after n seconds;
 * If fluentWait is happening more than this timeout it will throw an error like "element is not clickable"
 */
const allScriptsTimeout = webpackHelper.distRun() ? 10800000 : 10800000;
/**
 * number of failed retry, 3 times in bamboo and 2 times in local
 */
const maxRetryForFailedTests = webpackHelper.distRun() ? 3 : 3;

/**
 * Waits ms after page is loaded
 */
const pageResolveTimeout = 1000;

/**
 * Note: Prefix with "../saw-web" because end-to-end tests are invoked from "dist" when run against the
 * distribution package. The same path also works when run directly out of "saw-web".
 */
const testDir = '../saw-web/src/test';

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
  tempts: tempts
};

exports.config = {
  framework: 'jasmine2',
  //seleniumAddress: 'http://localhost:4444/wd/hub',
  getPageTimeout: pageLoadTimeout,
  allScriptsTimeout: allScriptsTimeout,
  customerCode:customerCode,
  useAllAngular2AppRoots: true,
  directConnect: true,
  baseUrl: 'http://localhost:3000',
  capabilities: {
    browserName: 'chrome',
    shardTestFiles: true,
    maxInstances: 4,
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
  suites: webpackHelper.distRun() ? {
    /**
     * Suites for test run invoked from Maven which is used in Bamboo continuous integration.
     * Note: In the long term there should just be a single set of suites used everywhere (for both continuous
     * integration and local front-end development). However, for now use a separate suite that allows enabling known
     * working tests (working reliably without flakiness) incrementally one by one in continuous integration, while
     * working on fixing the rest.
     */
    // root: [
    //   appRoot + '/src/test/e2e-tests/priviliges.test.js',
    //   appRoot + '/src/test/e2e-tests/analyze.test.js',
    //   appRoot + '/src/test/e2e-tests/createReport.test.js'
    // ],
    // charts: [
    //   appRoot + '/src/test/e2e-tests/charts/applyFiltersToCharts.js',
    //   appRoot + '/src/test/e2e-tests/charts/createAndDeleteCharts.test.js',
    //   appRoot + '/src/test/e2e-tests/charts/previewForCharts.test.js'
    // ],
    // chartEditFork: [
    //   appRoot + '/src/test/e2e-tests/charts/editAndDeleteCharts.test.js',
    //   appRoot + '/src/test/e2e-tests/charts/forkAndEditAndDeleteCharts.test.js'
    // ],
    // filters: [
    //   appRoot + '/src/test/e2e-tests/promptFilters.test.js'
    // ],
    // pivots: [
    //   appRoot + '/src/test/e2e-tests/pivots/pivotFilters.test.js'
    // ],
    authentication: [
      appRoot + '/src/test/e2e-tests/login.test.js' // TCs linked
    ],
    debug: [
      //webpackHelper.root(testDir + '/e2e-tests/debug.test.js')
    ]
  } : {
    /**
     * Suites for test run invoked from Protractor directly on local saw-web front-end development server
     */
    root: [
      appRoot + '/src/test/e2e-tests/priviliges.test.js',
      appRoot + '/src/test/e2e-tests/analyze.test.js',
      appRoot + '/src/test/e2e-tests/createReport.test.js'
    ],
    charts: [
      appRoot + '/src/test/e2e-tests/charts/applyFiltersToCharts.js',
      appRoot + '/src/test/e2e-tests/charts/createAndDeleteCharts.test.js',
      appRoot + '/src/test/e2e-tests/charts/previewForCharts.test.js'
    ],
    chartEditFork: [
      appRoot + '/src/test/e2e-tests/charts/editAndDeleteCharts.test.js',
      appRoot + '/src/test/e2e-tests/charts/forkAndEditAndDeleteCharts.test.js'
    ],
    filters: [
      appRoot + '/src/test/e2e-tests/promptFilters.test.js'
    ],
    pivots: [
      appRoot + '/src/test/e2e-tests/pivots/pivotFilters.test.js'
    ],
    authentication: [
      appRoot + '/src/test/e2e-tests/login.test.js' // TCs linked
    ],
    debug: [
      //webpackHelper.root(testDir + '/e2e-tests/debug.test.js')
    ]
  },
  onCleanUp: function (results) {
    retry.onCleanUp(results);
    //console.log("Finished at : "+ new Date())
  },
  onPrepare() {
    //console.log("Started at : "+ new Date())
    retry.onPrepare();
    // Gerenate test data
    token = generate.token(browser.baseUrl);
    //console.log("aToken: " + token);
    generate.usersRolesPrivilegesCategories(token);

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
    //clean up any residual/leftover from a previous run. Ensure we have clean
    //files for both locking and merging.
    if (fs.existsSync('target/jasmine-results.json.lock')) {
      fs.unlinkSync('target/jasmine-results.json.lock');
    }
    if (fs.existsSync('target/jasmine-results.json')) {
      fs.unlink('target/jasmine-results.json');
    }
  },
  afterLaunch: function() {
    return retry.afterLaunch(maxRetryForFailedTests);
  }
};
