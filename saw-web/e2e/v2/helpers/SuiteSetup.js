'use strict';

const path = require('path');
var fs = require('fs');
var convert = require('xml-js');
const globalVariables = require('../helpers/data-generation/globalVariables');
const logger = require('../conf/logger')(__filename);
const Constants = require('../helpers/Constants');
class SuiteSetup {
  /* Return true if end-to-end tests are run against distribution
 * package built with Maven and deployed to a local Docker container
 * (as happens for example on the Bamboo continuous integration
 * server), as opposed to a local saw-web front-end development
 * server
 * */
  static distRun() {
    return process.env.PWD.endsWith('/dist');
  }

  root(...args) {
    return path.join(process.cwd(), ...args);
  }

  failedTestData(testInfo) {

    logger.debug('writing failed test cases to failure object');
    let failedTestsData = {};
    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }
    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
    }

    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json')) {
      let existingFailures = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json', 'utf8'));
      // There are already failed tests so add to existing list
      logger.debug('existingFailures---' + JSON.stringify(existingFailures));
      // add new failures to existing
      this.writeToJsonFile(existingFailures, testInfo, Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json');

    } else {
      logger.debug('first failure... ');
      // Write new failed test list json file;
      this.writeToJsonFile(failedTestsData, testInfo, Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json');
    }
  }

  passTestData(testInfo) {

    logger.debug('writing pass test cases to success object');
    let passTestsData = {};
    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }
    if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
      fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
    }

    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/passTestData.json')) {
      let existingPassTests = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/passTestData.json', 'utf8'));
      // There are already failed tests so add to existing list
      logger.debug('existingSuccess tests---' + JSON.stringify(existingPassTests));
      // add new failures to existing
      this.writeToJsonFile(existingPassTests, testInfo, Constants.E2E_OUTPUT_BASE_DIR + '/passTestData.json');

    } else {
      logger.debug('first success test... ');
      // Write new pass test list json file;
      this.writeToJsonFile(passTestsData, testInfo, Constants.E2E_OUTPUT_BASE_DIR + '/passTestData.json');
    }
  }

  writeToJsonFile(testDataObject, testInfo, fileLocation) {

    if (!testDataObject[testInfo.feature]) {
      testDataObject[testInfo.feature] = {};
    }
    if (!testDataObject[[testInfo.feature]][[testInfo.dataProvider]]) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]] = {};
    }
    if (!testDataObject[[testInfo.feature]][[testInfo.dataProvider]][[testInfo.testId]]) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]][[testInfo.testId]] = testInfo.data;
      fs.writeFileSync(fileLocation, JSON.stringify(testDataObject), { encoding: 'utf8' });
    }
  }

  static readAllData(dir = null) {
    let completeTestData = {};
    let location;
    if (dir) {
      location = dir;
    } else {
      location = '../saw-web/e2e/v2/testdata/all-tests';
    }
    const dirCont = fs.readdirSync(location);
    const files = dirCont.filter((elm) => /.*\.(json)/gi.test(elm));
    files.forEach(function (file) {
      logger.info('Reading file for executing test suite: ' + file);
      let data = JSON.parse(fs.readFileSync(location + '/' + file, 'utf8'));
      completeTestData = Object.assign(data, completeTestData)
    });
    return completeTestData;
  }

  static failedTestDataForRetry() {
    logger.info('Generating failed test data set for next retry from failures');

    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json')) {
      fs.unlinkSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json');
    }
    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json')) {
      fs.renameSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json', Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json');
      logger.debug('Old failures json file is deleted and converted to failure data set ' +
        'i.e. ' + Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json converted to ' + Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.jsons');
    } else {
      logger.info('Yahooo....!!! There are no failures!');
    }
    // Delete the old file so that it can be again re-rewritten and used by another set of failures in next retry
    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json')) {
      fs.unlinkSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestData.json');
      logger.debug('Old failures json file is deleted and converted to failure data set');
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
      url = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/url.json', 'utf8')).baseUrl;
    } else {
      process.argv.forEach(function (val) {
        if (val.includes('--baseUrl')) {
          url = val.split('=')[1];
          let urlObject = {
            baseUrl: url, e2eId: globalVariables.generateE2eId
          };
          fs.writeFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/url.json', JSON.stringify(urlObject), {
            encoding: 'utf8'
          });
          return;
        }
      });
    }
    logger.info('Application url used for running e2e: ' + url);
    return url;
  }

  static getTestData() {
    if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json')) {
      let data = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json', 'utf8'));
      logger.warn('This is retry execution!! Executing with failed test data set, because failedTestDataForRetry found in  ' + Constants.E2E_OUTPUT_BASE_DIR + '/failedTestDataForRetry.json: data--->' + JSON.stringify(data));
      return data;
    } else {
      let suiteName;
      if (!fs.existsSync('target')) {
        fs.mkdirSync('target');
      }
      if (!fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR)) {
        fs.mkdirSync(Constants.E2E_OUTPUT_BASE_DIR);
      }

      if (fs.existsSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json')) {
        suiteName = JSON.parse(fs.readFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json', 'utf8')).suiteName;
      } else {
        process.argv.forEach(function (val) {
          if (val.includes('--suite')) {
            suiteName = val.split('=')[1];
            let suiteObject = {
              suiteName: suiteName
            };
            fs.writeFileSync(Constants.E2E_OUTPUT_BASE_DIR + '/suite.json', JSON.stringify(suiteObject), {
              encoding: 'utf8'
            });
            return;
          }
        });
      }
      if (suiteName !== undefined && suiteName === 'critical') {
        logger.warn('Executing with critical suite test data set.....');
        let data = JSON.parse(fs.readFileSync('../saw-web/e2e/v2/testdata/data.critical.json', 'utf8'));
        return data;
      } else {
        logger.warn('Executing with full suite test data set....');
        //let data = JSON.parse(fs.readFileSync('../saw-web/e2e/v2/testdata/data.json', 'utf8'));
        let data = SuiteSetup.readAllData();
        return data;
      }
    }
  }
}

module.exports = SuiteSetup;
