var appRoot = require('app-root-path');
const globalVariables = require(appRoot + '/src/test/javascript/helpers/globalVariables');

const roles = {
  admin: {
    roleName: 'EndToEnd Admin Role ' + globalVariables.e2eId,
    roleType: 'ADMIN',
    roleDesc: 'Admin role created for e2e testing',
    roleId: 'NULL'
  },
  userOne: {
    roleName: 'EndToEnd User Role ' + globalVariables.e2eId,
    roleType: 'USER',
    roleDesc: 'User role created for e2e testing',
    roleId: 'NULL'
  }
};

module.exports = roles;
