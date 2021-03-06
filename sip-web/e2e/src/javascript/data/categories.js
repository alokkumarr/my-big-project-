const globalVariables = require('../helpers/globalVariables');

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
  }
};

module.exports = categories;
