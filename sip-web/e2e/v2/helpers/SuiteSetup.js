'use strict';

const path = require('path');
var fs = require('fs');
var convert = require('xml-js');
const globalVariables = require('../helpers/data-generation/globalVariables');
const logger = require('../conf/logger')(__filename);
const Constants = require('../helpers/Constants');
const Utils = require('./Utils');
class SuiteSetup {
  /* Return true if end-to-end tests are run against distribution
   * package built with Maven and deployed to a local Docker container
   * (as happens for example on the Bamboo continuous integration
   * server), as opposed to a local saw-web front-end development
   * server
   * */
  static distRun() {
    if (!process.env.PWD) {
      process.env.PWD = process.cwd();
    }
    return process.env.PWD.endsWith('/dist');
  }

  root(...args) {
    return path.join(process.cwd(), ...args);
  }

  failedTestData(testInfo) {
    logger.debug('writing failed test cases to failure object');
    let failedTestsData = {};
    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/retry')) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR + '/retry', {
        recursive: true
      });
    }

    if (
      fs.existsSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      )
    ) {
      let existingFailures = JSON.parse(
        fs.readFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json',
          'utf8'
        )
      );
      // There are already failed tests so add to existing list
      logger.debug('existingFailures---' + JSON.stringify(existingFailures));
      // add new failures to existing
      this.writeToJsonFile(
        existingFailures,
        testInfo,
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      );
    } else {
      logger.debug('first failure... ');
      // Write new failed test list json file;
      this.writeToJsonFile(
        failedTestsData,
        testInfo,
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      );
    }
  }

  passTestData(testInfo) {
    logger.debug('writing pass test cases to success object');
    let passTestsData = {};

    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result')) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR + '/result', {
        recursive: true
      });
    }

    if (
      fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result/passTestData.json')
    ) {
      let existingPassTests = JSON.parse(
        fs.readFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/result/passTestData.json',
          'utf8'
        )
      );
      // There are already failed tests so add to existing list
      logger.debug(
        'existingSuccess tests---' + JSON.stringify(existingPassTests)
      );
      // add new failures to existing
      this.writeToJsonFile(
        existingPassTests,
        testInfo,
        Constants.E2E_OUTPUT_BASE_DIR + '/result/passTestData.json'
      );
    } else {
      logger.debug('first success test... ');
      // Write new pass test list json file;
      this.writeToJsonFile(
        passTestsData,
        testInfo,
        Constants.E2E_OUTPUT_BASE_DIR + '/result/passTestData.json'
      );
    }
  }

  writeToJsonFile(testDataObject, testInfo, fileLocation) {
    if (!testDataObject[testInfo.feature]) {
      testDataObject[testInfo.feature] = {};
    }
    if (!testDataObject[[testInfo.feature]][[testInfo.dataProvider]]) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]] = {};
    }
    if (
      !testDataObject[[testInfo.feature]][[testInfo.dataProvider]][
        [testInfo.testId]
      ]
    ) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]][
        [testInfo.testId]
      ] = testInfo.data;
      fs.writeFileSync(fileLocation, JSON.stringify(testDataObject), {
        encoding: 'utf8'
      });
    }
  }

  addToExecutedTests(currentResult) {
    let testResultStatus = {};

    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result')) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR + '/result', {
        recursive: true
      });
    }
    if (
      fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json')
    ) {
      testResultStatus = JSON.parse(
        fs.readFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json',
          'utf8'
        )
      );
      // There are already some test exits then add new one or replace the existing one with latest result
      logger.silly('existingResult---' + JSON.stringify(testResultStatus));
    } else {
      logger.silly('first test result... ');
    }
    testResultStatus[currentResult.testInfo.testId] = currentResult;
    fs.writeFileSync(
      Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json',
      JSON.stringify(testResultStatus),
      { encoding: 'utf8' }
    );
  }

  static readAllData(dir = null, currentSuite = null) {
    let completeTestData = {};
    let location;
    if (dir) {
      location = dir;
    } else {
      location = '../sip-web/e2e/v2/testdata/all-tests';
    }
    const dirCont = fs.readdirSync(location);
    const files = dirCont.filter(elm => /.*\.(json)/gi.test(elm));
    files.forEach(function(file) {
      logger.info('Reading file for executing test suite: ' + file);
      let data = JSON.parse(fs.readFileSync(location + '/' + file, 'utf8'));
      completeTestData = Object.assign(data, completeTestData);
    });
    // Filter the data based on suite
    if (currentSuite) {
      completeTestData = this.filterDataBySuite(completeTestData, currentSuite);
    }
    logger.warn(
      'completeTestData after filter---' + JSON.stringify(completeTestData)
    );
    return completeTestData;
  }

  // Filter the data and return only matching records
  // If suites property doesn't exist then it means this test applicable for all suite
  static filterDataBySuite(data, currentsuite) {
    Object.keys(data).forEach(feature => {
      Object.keys(data[feature]).forEach(dp => {
        Object.keys(data[feature][dp]).forEach(testcase => {
          if (data[feature][dp][testcase].hasOwnProperty('suites')) {
            if (!data[feature][dp][testcase].suites.includes(currentsuite)) {
              delete data[feature][dp][testcase];
            }
          }
        });
      });
    });

    return data;
  }

  static failedTestDataForRetry() {
    logger.info('Generating failed test data set for next retry from failures');

    if (
      fs.existsSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestDataForRetry.json'
      )
    ) {
      fs.unlinkSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestDataForRetry.json'
      );
    }
    if (
      fs.existsSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      )
    ) {
      fs.renameSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json',
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestDataForRetry.json'
      );
      logger.debug(
        'Old failures json file is deleted and converted to failure data set ' +
          'i.e. ' +
          Constants.E2E_OUTPUT_BASE_DIR +
          '/retry/failedTestData.json converted to ' +
          Constants.E2E_OUTPUT_BASE_DIR +
          '/retry/failedTestDataForRetry.json'
      );
    } else if (
      !fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json')
    ) {
      // if result is not created then it means something is wrong.
      logger.error('oopps....!!! There are unexpected failures!');
    } else {
      logger.info('Yahooo....!!! There are no failures!');
    }
    // Delete the old file so that it can be again re-rewritten and used by another set of failures in next retry
    if (
      fs.existsSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      )
    ) {
      fs.unlinkSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestData.json'
      );
      logger.debug(
        'Old failures json file is deleted and converted to failure data set'
      );
    }
  }

  static getSawWebUrl() {
    let url;

    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }
    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
    }

    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/url.json')) {
      url = JSON.parse(
        fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/url.json', 'utf8')
      ).baseUrl;
    } else {
      process.argv.forEach(function(val) {
        if (val.includes('--baseUrl')) {
          url = val.split('=')[1];
          let urlObject = {
            baseUrl: url
          };
          fs.writeFileSync(
            Constants.E2E_OUTPUT_BASE_DIR + '/url.json',
            JSON.stringify(urlObject),
            {
              encoding: 'utf8'
            }
          );
          return;
        }
      });
    }
    logger.info('Application url used for running e2e: ' + url);
    return url;
  }

  static islocalRun() {
    let localRunDetails = {
      localRun: false,
      firstRun: false
    };

    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }
    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
    }

    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/localRun.json')) {
      localRunDetails = {
        localRun: true,
        firstRun: false
      };

      fs.writeFileSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/localRun.json',
        JSON.stringify(localRunDetails),
        {
          encoding: 'utf8'
        }
      );
    } else {
      process.argv.forEach(function(val) {
        if (val.includes('--localRun')) {
          localRunDetails = {
            localRun: true,
            firstRun: true
          };
          fs.writeFileSync(
            Constants.E2E_OUTPUT_BASE_DIR + '/localRun.json',
            JSON.stringify(localRunDetails),
            {
              encoding: 'utf8'
            }
          );
          return;
        }
      });
    }
    if (localRunDetails.localRun) {
      if (!localRunDetails.firstRun) {
        logger.warn(
          'End to end tests are running locallly but this is not first run hence disabling data generation & retry'
        );
      } else {
        logger.warn(
          'End to end tests are running locallly but this is first run hence enabling data generation'
        );
      }
    } else {
      logger.warn(
        'End to end tests are running with retry and data generation enabled'
      );
    }
    return localRunDetails;
  }

  static getTestData() {
    if (
      fs.existsSync(
        Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestDataForRetry.json'
      )
    ) {
      let data = JSON.parse(
        fs.readFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/retry/failedTestDataForRetry.json',
          'utf8'
        )
      );
      logger.warn(
        'This is retry execution!! Executing with failed test data set, because failedTestDataForRetry found in  ' +
          Constants.E2E_OUTPUT_BASE_DIR +
          '/retry/failedTestDataForRetry.json: data--->' +
          JSON.stringify(data)
      );
      return data;
    } else {
      let suiteName;

      if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
        fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR, {
          recursive: true
        });
      }

      if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json')) {
        suiteName = JSON.parse(
          fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json', 'utf8')
        ).suiteName;
      } else {
        process.argv.forEach(function(val) {
          if (val.includes('--suite')) {
            suiteName = val.split('=')[1];
            let suiteObject = {
              suiteName: suiteName
            };
            fs.writeFileSync(
              Constants.E2E_OUTPUT_BASE_DIR + '/suite.json',
              JSON.stringify(suiteObject),
              {
                encoding: 'utf8'
              }
            );
            return;
          }
        });
      }
      if (suiteName) {
        const suite = suiteName.trim().toLowerCase();
        logger.warn(`Executing with ${suite} suite test data set.....`);
        return SuiteSetup.readAllData(null, suite);
      } else {
        logger.warn('Executing with critical suite test data set....');
        return SuiteSetup.readAllData(null, 'critical');
      }
    }
  }

  static convertJsonToJunitXml() {
    if (
      fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json')
    ) {
      let testResultStatus = JSON.parse(
        fs.readFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/result/testResult.json',
          'utf8'
        )
      );

      let totalTests = Object.keys(testResultStatus).length;
      let failedCount = 0;
      let failedTests = '';
      let passedTests = '';

      for (let key in testResultStatus) {
        if (testResultStatus.hasOwnProperty(key)) {
          if (testResultStatus[key].status.toLowerCase() === 'failed') {
            failedCount++;
            failedTests += `<testcase classname="${Utils.replaceSpecialCharsNotAllowedInXml(
              testResultStatus[key].fullName
            )}" name="${Utils.replaceSpecialCharsNotAllowedInXml(
              testResultStatus[key].description
            )}">
                                    <failure type="exception" message="${Utils.replaceSpecialCharsNotAllowedInXml(
                                      testResultStatus[key]
                                        .failedExpectations[0].message
                                    )}">
                                     <![CDATA[${
                                       testResultStatus[key]
                                         .failedExpectations[0].stack
                                     }]]>
                                    </failure>
                                </testcase>`;
          } else if (testResultStatus[key].status.toLowerCase() === 'passed') {
            passedTests += `<testcase classname="${Utils.replaceSpecialCharsNotAllowedInXml(
              testResultStatus[key].fullName
            )}" name="${Utils.replaceSpecialCharsNotAllowedInXml(
              testResultStatus[key].description
            )}" />`;
          }
        }
      }
      if (totalTests > 0) {
        let suiteName = JSON.parse(
          fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json', 'utf8')
        ).suiteName;
        let testSuiteStart = `	<testsuite name="executed suite: ${suiteName}" timestamp="${new Date()}" failures="${failedCount}" tests="${totalTests}">`;
        let testSuiteEnd = `</testsuite>`;
        let xmlDocument = `<?xml version="1.0" encoding="UTF-8" ?>`;
        xmlDocument += testSuiteStart;
        xmlDocument += failedTests;
        xmlDocument += passedTests;

        xmlDocument += testSuiteEnd;
        logger.debug('xmlDocument e2e tests: ' + xmlDocument);

        fs.writeFileSync(
          Constants.E2E_OUTPUT_BASE_DIR + '/myjunit.xml',
          xmlDocument,
          { encoding: 'utf8' }
        );
      }
    }
  }
}

module.exports = SuiteSetup;
