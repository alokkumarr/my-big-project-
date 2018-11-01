var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../conf/protractor.conf');
var appRoot = require('app-root-path');
var fs = require('fs');

using(testDataReader.testData['DEV1']['dp'], function(data, description) {
  describe('dev test1 dev1.js', () => {
    beforeEach(() => {
      console.log('I am in before each');
      console.log(JSON.stringify(data));
      console.log(JSON.stringify(description));
    });

    afterEach(function() {
      console.log('I am in after test');
      console.log(JSON.stringify(data));
      console.log(JSON.stringify(description));
    });

    it('Test1', function() {
      console.log('I am in test');
      console.log(JSON.stringify(data));
      console.log(JSON.stringify(description));
      expect(true).toBe(true);
    });
  });
});
