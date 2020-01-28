const globalVariables = require('./globalVariables');

const description = 'Category created for e2e testing'; // same for all categories
const nullValue = 'NULL';

const categories = {
  privileges: {
    name: 'AT Privileges Category ' + globalVariables.e2eId,
    description: description,
    id: nullValue,
    type: nullValue,
    code: nullValue
  },
  analyses: {
    name: 'AT Analysis Category ' + globalVariables.e2eId,
    description: description,
    id: nullValue,
    type: nullValue,
    code: nullValue
  },
  observe: {
    name: 'AT observe Category ' + globalVariables.e2eId,
    description: description,
    id: nullValue,
    type: nullValue,
    code: nullValue
  },
  workbench: {
    name: 'AT work Category ' + globalVariables.e2eId,
    description: description,
    id: nullValue,
    type: nullValue,
    code: nullValue
  },
  alertDefault: {
    name: 'Alerts',
    description: description,
    id: 57,
    type: 'PARENT_SYNCHRONOSS1ALERT12',
    code: 'SYNCHRONOSS1ALERT12'
  }
};

module.exports = categories;
