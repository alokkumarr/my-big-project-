var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const logger = require('../conf/logger')(__filename);

  describe('DEV2 test from dev2.js', () => {

    using(testDataReader.testData['DEV3']['dp'], function(data, id) {

    it(`${id}:${data.description}`, function() {
      console.log('I am in test');
      console.log(JSON.stringify(id));
      console.log(JSON.stringify(data));
      expect(true).toBe(false);

    }).result.testInfo = {testId: id, data: data, feature:'DEV3', dataProvider:'dp'};
  });
});

using(testDataReader.testData['DEV4']['dp'], function(data, id) {
  describe('DEV4 test from dev2.js', () => {


    it(`${id}:${data.description}`, function() {
      console.log('I am in test');
      console.log(JSON.stringify(id));
      console.log(JSON.stringify(data));
      if(data.user == 'admin') {
        expect(true).toBe(true);
      } else {
        expect(true).toBe(false);
      }

    }).result.testInfo = {testId: id, data: data, feature:'DEV4', dataProvider:'dp'};
  });
});
