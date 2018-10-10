var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../protractor.conf');
var appRoot = require('app-root-path');
var fs = require('fs');
describe('dev dev3.js', () => {

  //Prerequisites: two users should exist with user types: admin and user

  beforeAll(function () {
    jasmine.DEFAULT_TIMEOUT_INTERVAL = protractorConf.timeouts.extendedDefaultTimeoutInterval;
    // fs.readFile(appRoot+'/src/test../testdata/data.json','utf8', (err, data) => {
    //   let testData = JSON.parse(data);
    //   console.log(testData.DEV1.dataProviderName1)
    //   userDataProvider = testData.DEV1.dataProviderName1;
    // });
  });
  afterEach(function(){

  });

  using(testDataReader.testData['DEV3']['dataProviderName1'], function (data, description) {
    it('Dev3Test2 first IT_Block ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DEV3', dp:'dataProviderName1'}), function () {
      console.log(JSON.stringify(data));
        expect(true).toBe(false);

    });
  });

  // using(testDataReader.testData2.DEV2.dataProviderName1, function (data, description) {
  //   it('Dev1Test2 second IT_Block ' + description + 'testData:'+JSON.stringify(testDataReader.testData.DEV1.dataProviderName1[description]), function () {
  //     console.log(JSON.stringify(data));
  //     expect(true).toBe(false);
  //
  //   });
  // });
});
