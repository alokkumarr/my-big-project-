const webpackHelper = require('./webpack.helper');
const SpecReporter = require('jasmine-spec-reporter').SpecReporter;
const browserstack = require('browserstack-local');

exports.config = {
  seleniumAddress: 'http://hub-cloud.browserstack.com/wd/hub',
  framework: 'jasmine2',
  capabilities: {
    'browserstack.user': 'alexanderkrivoro3',
    'browserstack.key': 'nBTyXwJY7goDDaB15nNt',
    'browserstack.local': true,
    'browserName': 'chrome',
    'browser_version': '63.0',
    'os': 'OS X',
    'os_version': 'High Sierra',
    'resolution': '1920x1080',
    /*chromeOptions: {
      args: [
        //'incognito',
        'disable-extensions',
        'disable-web-security',
        '--start-fullscreen', // enable for Mac OS
        /!*"--headless",
        "--disable-gpu",
        "--window-size=2880,1800"
      ]
    }*/
  },
  /*jasmineNodeOpts: {
    isVerbose: true,
    defaultTimeoutInterval: 120000,
    showTiming: true,
    includeStackTrace: true,
    realtimeFailure: true,
    showColors: true
  },*/
  suites: {
    test: [
      webpackHelper.root('src/test/e2e-tests/login.test.js')
    ],
    /*authentication: [
      webpackHelper.root('src/test/e2e-tests/login.test.js')
    ],
    analyses: [
      webpackHelper.root('src/test/e2e-tests/priviliges.test.js'),
      webpackHelper.root('src/test/e2e-tests/goToAnalyze.test.js'),
      webpackHelper.root('src/test/e2e-tests/createChart.test.js'),
      webpackHelper.root('src/test/e2e-tests/createPivot.test.js'),
      webpackHelper.root('src/test/e2e-tests/createReport.test.js'),
      webpackHelper.root('src/test/e2e-tests/charts/createAndDeleteCharts.test.js')
      //webpackHelper.root('src/test/javascript/e2e/spec/analyses.test.js'), // obsolete
      //webpackHelper.root('src/test/e2e-tests/debug.test.js') // for testing purposes
    ]*/
  },

  onPrepare() {
    launchLocalBrowserStack();

    jasmine.getEnv().addReporter(new SpecReporter({
      displayStacktrace: true,
      displaySpecDuration: true,
      displaySuiteNumber: true
    }));

    jasmine.getEnv().defaultTimeoutInterval = 600000; //another option if above doesn't work

    browser.manage().timeouts().pageLoadTimeout(30000);
    browser.manage().timeouts().implicitlyWait(10000);
    //browser.driver.manage().window().maximize(); // disable for Mac OS
    browser.driver.get('http://localhost:3000');
  },

  onComplete() {
    stopLocalBrowserStack();
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
