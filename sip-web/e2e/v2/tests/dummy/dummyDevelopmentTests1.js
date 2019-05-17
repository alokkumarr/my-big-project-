var testDataReader = require('../../testdata/testDataReader.js');
const using = require('jasmine-data-provider');
const protractorConf = require('../../conf/protractor.conf');
const logger = require('../../conf/logger')(__filename);
const ObserveHelper = require('../../helpers/api/ObserveHelper');

  describe('DEV1 test from dummyDevelopmentTests1.js', () => {
    logger.info('Hello this is from logger')
    using(testDataReader.testData['DEV1']['dp'] ? testDataReader.testData['DEV1']['dp'] :{}, function(data, id) {

    it(`${id}:${data.description}`, function() {

      try {
        let oh  = new ObserveHelper();
        oh.deleteDashboard()
        console.log('I am in test');
        console.log(JSON.stringify(id));
        console.log(JSON.stringify(data));
        expect(true).toBe(true);
        logger.warn(id+'Hello this is from logger');
      }catch (e) {

      }
    }).result.testInfo = {testId: id, data: data, feature:'DEV1', dataProvider:'dp'};
  });
});
