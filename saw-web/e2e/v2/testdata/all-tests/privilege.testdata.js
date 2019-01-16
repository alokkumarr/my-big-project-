const Constants = require('../../helpers/Constants')
export const privilegeTestData = {
  PRIVILEGES: {
    positiveTests: {
      PRIVILEGES001: {
        user: 'userOne',
        subCategory: 'all',
        cardOptions: true,
        viewOptions: true,
        create: true,
        edit: true,
        fork: true,
        publish: true,
        schedule: true,
        execute: true,
        export: true,
        delete: true,
        description: 'User should have all the permission',
        suites: [Constants.SMOKE, Constants.SANITY, Constants.CRITICAL, Constants.REGRESSION]
      },
      PRIVILEGES002: {
        user: 'userOne',
        subCategory: 'create',
        cardOptions: false,
        viewOptions: true,
        create: true,
        edit: false,
        fork: false,
        publish: false,
        schedule: false,
        execute: false,
        export: false,
        delete: false,
        description: 'User should have only Create permission',
        suites: [Constants.SMOKE, Constants.SANITY, Constants.CRITICAL, Constants.REGRESSION]
      }
    }
  }
};
