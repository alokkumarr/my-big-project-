var testDataReader = require('../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../conf/protractor.conf');
const logger = require('../conf/logger')(__filename);

  describe('DEV1 test from dev1.js', () => {
    logger.info('Hello this is from logger')
    using(testDataReader.testData['DEV1']['dp'] ? testDataReader.testData['DEV1']['dp'] :{}, function(data, id) {

    it(`${id}:${data.description}`, function() {
      console.log('I am in test');
      console.log(JSON.stringify(id));
      console.log(JSON.stringify(data));
      expect(true).toBe(true);
      logger.warn(id+'Hello this is from logger');
    }).result.testInfo = {testId: id, data: data, feature:'DEV1', dataProvider:'dp'};
  });
});
