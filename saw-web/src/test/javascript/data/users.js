const users = {
  admin: {
    loginId: "at.admin",
    role: "AT Role Admin DO NOT TOUCH",
    status: "ACTIVE",
    firstname: "at",
    lastName: "admin"
  },
  userOne: {
    loginId: "at.userOne",
    role: "AT Role User One DO NOT TOUCH"
  },
  // Password and email is common to all users, recorded under anyUser
  anyUser: {
    password: "Password1!",
    email: "alexander.krivorotko@moduscreate.com"
  }
};

module.exports = users;
