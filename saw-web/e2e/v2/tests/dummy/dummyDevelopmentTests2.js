var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const logger = require('../../conf/logger')(__filename);

  describe('DEV2 test from dummyDevelopmentTests2.js', () => {

    using(testDataReader.testData['DEV2']['dp'] ? testDataReader.testData['DEV2']['dp']:{}, function(data, id) {
    it(`${id}:${data.description}`, function() {
      console.log('I am in test');
      console.log(JSON.stringify(id));
      console.log(JSON.stringify(data));
      expect(true).toBe(true);

    }).result.testInfo = {testId: id, data: data, feature:'DEV2', dataProvider:'dp'};
  });

    using(testDataReader.testData['DEV2']['dp1'] ? testDataReader.testData['DEV2']['dp1']:{}, function(data, id) {
      it(`${id}:${data.description}`, function() {
        console.log('I am in test');
        console.log(JSON.stringify(id));
        console.log(JSON.stringify(data));
        if(data.user==='admin'){
          expect(true).toBe(true);
        } else {
          expect(true).toBe(true);
        }

      }).result.testInfo = {testId: id, data: data, feature:'DEV2', dataProvider:'dp1'};
    });
});
