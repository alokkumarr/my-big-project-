const webpackHelper = require('../../../../conf/webpack.helper');

const users = {
  admin: {
    loginId: webpackHelper.distRun() ? "sawadmin@synchronoss.com" : "at.admin",
    role: "AT Role Admin DO NOT TOUCH",
    status: "ACTIVE",
    firstname: "at",
    lastName: "admin"
  },
  userOne: {
    loginId: webpackHelper.distRun() ? "sawadmin@synchronoss.com" : "at.userOne",
    role: "AT Role User One DO NOT TOUCH"
  },
  // Password and email is common to all users, recorded under anyUser
  anyUser: {
    password: webpackHelper.distRun() ? "Sawsyncnewuser1!" : "Password1!",
    email: "alexander.krivorotko@moduscreate.com"
  }
};

module.exports = users;
