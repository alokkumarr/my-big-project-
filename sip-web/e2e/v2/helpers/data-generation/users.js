const globalVariables = require('./globalVariables');
const roles = require('./roles');

const users = {
  // initial admin user on docker instance
  masterAdmin: {
    loginId: 'sawadmin@synchronoss.com', // local docker implementation
    roleName: 'ADMIN',
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'admin',
    password: 'Sawsyncnewuser1!',
    userId: 1
  },
  admin: {
    loginId: 'at.admin.' + globalVariables.e2eId,
    roleName: roles.admin.roleName,
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'admin'
  },
  userOne: {
    loginId: 'at.userOne.' + globalVariables.e2eId,
    roleName: roles.userOne.roleName,
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'user'
  },
  adminReset: {
    loginId: 'at.adminr.' + globalVariables.e2eId,
    roleName: roles.admin.roleName,
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'adminreset'
  },
  userOneReset: {
    loginId: 'at.userOner.' + globalVariables.e2eId,
    roleName: roles.userOne.roleName,
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'userreset'
  },
  userTwoReset: {
    loginId: 'at.userTwor.' + globalVariables.e2eId,
    roleName: roles.userOne.roleName,
    status: 'ACTIVE',
    firstName: 'at',
    lastName: 'userreset'
  },
  // Password and email is common to all users, recorded under anyUser
  anyUser: {
    password: 'Password1!',
    email: 'e2e@email.com'
  }
};

module.exports = users;
