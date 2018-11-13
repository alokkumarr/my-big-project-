var testDataReader = require('./testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../protractor.conf');
var appRoot = require('app-root-path');
var fs = require('fs');
describe('dev test1 dev1.js', () => {

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

  using(testDataReader.testData['DEV1']['dp'], function (data, description) {
    it('Dev1Test1 first IT_Block ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DEV1', dp:'dp'}), function () {
      console.log(JSON.stringify(data));
      expect(true).toBe(true);

    });
  });

  using(testDataReader.testData['DEV2']['dp'], function (data, description) {
    it('DEV2Test1 first IT_Block ' + description +' testDataMetaInfo: '+ JSON.stringify({test:description,feature:'DEV2', dp:'dp'}), function () {
      console.log(JSON.stringify(data));
      expect(true).toBe(false);

    });
  });
});
