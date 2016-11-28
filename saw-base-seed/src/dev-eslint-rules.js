var defaultsDeep = require('lodash/defaultsDeep');
var baseRules = require('./prod-eslint-rules');

module.exports = defaultsDeep({
  rules: {
    'no-console': 0,
    'angular/log': 0,
    'no-unused-vars': 0
  }
}, baseRules);
