How to add new test data in json file. for e2e we need to follow below json schema

**********
Schema:
**********
{
  "featureName": {
    "dataProvideName": {
      "testCaseId": {
        "user": "userOne",
        "description": "test by user TC01"
      }
    }
  }
}


**********
Details about the attribute
**********
featureName:
      # it should be unique across test suite else while merging all file you may get unexpected result and failures.
      # You should use this name in test to filter your particular test
dataProvideName:
      # This is dataprovider name and this can be anything, use name name in test to filter out your test
      # e.g. you have login feature but different type of tests i.e. invalid login tests, valid login tests etc....
testCaseId:
      # this is Testcase id ideally this should be TestLinkID but for now add any unique id and update this file with last TestCase id
      # This can hold any object but same will be used as data in test data


Example test case format.


    var testDataReader = require('../testdata/testDataReader.js');
    const using = require('jasmine-data-provider');
    const protractorConf = require('../conf/protractor.conf');
    const logger = require('../conf/logger')(__filename);

      describe('DEV1 test from dummyDevelopmentTests1.js', () => {
        using(testDataReader.testData['featureName']['dataProvideName'] ? testDataReader.testData['featureName']['dataProvideName'] :{}, function(data, id) {

         ## Note:
                data will be value of `testCaseId` key
                id: will be `testCaseId`


        it(`${id}:${data.description}`, function() {
          console.log('I am in test');
          console.log(JSON.stringify(id));
          console.log(JSON.stringify(data));
          expect(true).toBe(true);
          logger.warn(id+'Hello this is from logger');
        }).result.testInfo = {testId: id, data: data, feature:'featureName', dataProvider:'dataProvideName'};
      });
    });
