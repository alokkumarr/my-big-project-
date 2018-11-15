'use strict';

const path = require('path');
var fs = require('fs');
var convert = require('xml-js');
const globalVariables = require('../helpers/data-generation/globalVariables');

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

    let failedTestsData = {};
    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }

    if (fs.existsSync('target/failedTestData.json')) {
      let existingFailures = JSON.parse(fs.readFileSync('target/failedTestData.json', 'utf8'));
      // There are already failed tests so add to existing list
      console.log('existingFailures---'+JSON.stringify(existingFailures));
      // add new failures to existing
      this.writeToJsonFile(existingFailures, testInfo);

    } else {
      // Write new failed test list json file
      console.log('first failure---'+JSON.stringify(existingFailures));
      this.writeToJsonFile(failedTestsData, testInfo);
    }
  }

  writeToJsonFile(testDataObject, testInfo){

    if (!testDataObject[testInfo.feature]) {
      testDataObject[testInfo.feature] = {};
    }
    if (!testDataObject[[testInfo.feature]][[testInfo.dataProvider]]) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]] = {};
    }
    if (!testDataObject[[testInfo.feature]][[testInfo.dataProvider]][[testInfo.testId]]) {
      testDataObject[[testInfo.feature]][[testInfo.dataProvider]][[testInfo.testId]] = testInfo.data;
      fs.writeFileSync('target/failedTestData.json', JSON.stringify(testDataObject), {encoding: 'utf8'});
    }
  }

  static failedTestDataForRetry() {
    if(fs.existsSync('target/failedTestData.json')){
      console.log("'target/failedTestData.json'")
    }
    fs.renameSync('target/failedTestData.json','target/failedTestDataForRetry.json');
    // Delete the old file so that it can be again re-rewritten and used by another set of failures in next retry
    if (fs.existsSync('target/failedTestData.json')) {
      fs.unlinkSync('target/failedTestData.json');
    }
  }

  static getSawWebUrl() {
    let url;

    if (!fs.existsSync('target')) {
      fs.mkdirSync('target');
    }

    if (fs.existsSync('target/url.json')) {
      url = JSON.parse(fs.readFileSync('target/url.json', 'utf8')).baseUrl;
    } else {
      process.argv.forEach(function (val) {
        if (val.includes('--baseUrl')) {
          url = val.split('=')[1];
          let urlObject = {
            baseUrl: url, e2eId: globalVariables.generateE2eId
          };
          fs.writeFileSync('target/url.json', JSON.stringify(urlObject), {
            encoding: 'utf8'
          });
          return;
        }
      });
    }
    return url;
  }

  static getTestData() {
    if (fs.existsSync('target/failedTestData.json')) {
      let data = JSON.parse(fs.readFileSync('target/failedTestData.json', 'utf8'));
      console.log('useing Failed test data---'+JSON.stringify(data));
      return data;
    } else {
      let suiteName;
      if (!fs.existsSync('target')) {
        fs.mkdirSync('target');
      }

      if (fs.existsSync('target/suite.json')) {
        suiteName = JSON.parse(fs.readFileSync('target/suite.json', 'utf8')).suiteName;
      } else {
        process.argv.forEach(function (val) {
          if (val.includes('--suite')) {
            suiteName = val.split('=')[1];
            let suiteObject = {
              suiteName: suiteName
            };
            fs.writeFileSync('target/suite.json', JSON.stringify(suiteObject), {
              encoding: 'utf8'
            });
            return;
          }
        });
      }
      if (suiteName !== undefined && suiteName === 'critical') {
        console.log('Executing critical suite.....');
        let data = JSON.parse(fs.readFileSync('../saw-web/e2e/v2/testdata/data.critical.json', 'utf8'));
        return data;
      } else {
        console.log('Executing full suite.....');
        let data = JSON.parse(fs.readFileSync('../saw-web/e2e/v2/testdata/data.json', 'utf8'));
        return data;
      }
    }
  }
}

module.exports = SuiteSetup;
