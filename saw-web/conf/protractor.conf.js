const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
const browserstack = require('browserstack-local');

exports.config = {
  //TODO Setup config depending on environment
  //seleniumAddress: 'http://hub-cloud.browserstack.com/wd/hub', // enable for loal browserstack testing
  seleniumAddress: 'http://localhost:4444/wd/hub', // enable for regular testing
  directConnect: true, // enable for regular testing
  framework: 'jasmine2',
  getPageTimeout: 5000000, // enable for regular testing
  allScriptsTimeout: 5000000, // enable for regular testing
  capabilities: {
    'browserstack.user': 'alexanderkrivoro4',
    'browserstack.key': 'rwCmGzyDLyVrjEkmXiUW',
    'browserstack.local': true,
    'browserstack.debug': true,
    'browserName': 'chrome',
    'browser_version': '63.0',
    'os': 'OS X',
    'os_version': 'High Sierra',
    'resolution': '1920x1080',

    // enable for regular testing
    chromeOptions: {
      args: [
        'disable-extensions',
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        "--headless",
        "--disable-gpu",
        "--window-size=2880,1800"
      ]
    }
  },
  jasmineNodeOpts: {
    isVerbose: true,
    showTiming: true,
    includeStackTrace: true,
    realtimeFailure: true,
    showColors: true
  },
  suites: {
    charts: [
      webpackHelper.root('src/test/e2e-tests/charts/applyFiltersToCharts.js'),
      webpackHelper.root('src/test/e2e-tests/charts/createAndDeleteCharts.test.js'),
      webpackHelper.root('src/test/e2e-tests/charts/previewForCharts.test.js')
    ],
    root: [
      webpackHelper.root('src/test/e2e-tests/analyze.test.js'),
      webpackHelper.root('src/test/e2e-tests/createPivot.test.js'),
      webpackHelper.root('src/test/e2e-tests/createReport.test.js'),
      //webpackHelper.root('src/test/e2e-tests/debug.test.js') // for testing purposes
      webpackHelper.root('src/test/e2e-tests/login.test.js'),
      webpackHelper.root('src/test/e2e-tests/priviliges.test.js'),
    ]
  },

  onPrepare() {
    //launchLocalBrowserStack(); // enable for loal browserstack testing

    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.DEFAULT_TIMEOUT_INTERVAL = 5000000;
    jasmine.getEnv().defaultTimeoutInterval = 5000000; //another option if above doesn't work

    browser.manage().timeouts().pageLoadTimeout(5000000);
    browser.manage().timeouts().implicitlyWait(10000);
    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.driver.get('http://localhost:3000');

    // enable for regular testing
    return browser.driver.wait(() => {
      return browser.driver.getCurrentUrl().then(url => {
        return /login/.test(url);
      });
    }, 30000);
  },

  onComplete() {
    //stopLocalBrowserStack(); // enable for loal browserstack testing
  }
};

// Start local browserstack before start of test
/*
To launch locally download binary from https://www.browserstack.com/local-testing
and launch it from folder where binary was downloaded: ./BrowserStackLocal --key your-key-here --force-local
 */
function launchLocalBrowserStack() {
  console.log("Connecting local BrowserStack");
  return new Promise(function (resolve, reject) {
    exports.bs_local = new browserstack.Local();
    exports.bs_local.start({'key': exports.config.capabilities['browserstack.key']}, function (error) {
      if (error) return reject(error);
      console.log('Connected. Now testing...');

      resolve();
      console.log("resolved");
    });
  });
}

// Stop local browserstack after end of test
function stopLocalBrowserStack() {
  return new Promise(function (resolve, reject) {
    exports.bs_local.stop(resolve);
    console.log("Local BrowserStack stopped");
  });
}
