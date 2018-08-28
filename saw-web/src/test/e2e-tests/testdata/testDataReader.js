'use strict';

var appRoot = require('app-root-path');
var fs = require('fs');
const protractorConf = require('../../../../conf/protractor.conf');

module.exports = {
  testData:protractorConf.config.testData
}
