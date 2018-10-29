var testDataReader = require('./testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../protractor.conf');
var appRoot = require('app-root-path');
var fs = require('fs');

using(testDataReader.testData['DEV1']['dp'], function(data, description) {
  describe('dev test1 dev1.js', () => {
    beforeEach(() => {
      console.log(JSON.stringify(data));
      console.log(JSON.stringify(description));
    });

    afterEach(function() {});

    it('Test1', function() {
      expect(true).toBe(true);
    });
  });
});
