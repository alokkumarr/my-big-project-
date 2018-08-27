'use strict';

var appRoot = require('app-root-path');
var fs = require('fs');
const protractorConf = require('../../../../conf/protractor.conf');
//
// fs.readFile(appRoot+'/src/test/e2e-tests/testdata/data.json','utf8', (err, data) => {
//   console.log(JSON.stringify(JSON.parse(data)))
//   return JSON.parse(data);
// });


module.exports = {
  testData:protractorConf.config.testData,
  testData2:protractorConf.config.testData
}
