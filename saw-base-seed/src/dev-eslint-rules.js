const defaultsDeep = require('lodash/defaultsDeep');
const baseRules = require('./prod-eslint-rules');

// these rules will be merged with the production rules
module.exports = defaultsDeep({
  rules: {
    'no-console': 0,
    'angular/log': 0,
    'no-unused-vars': 0,
    'no-debugger': 0
  }
}, baseRules);
