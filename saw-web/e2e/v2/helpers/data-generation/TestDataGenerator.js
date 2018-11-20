'use strict';
const users = require('./users');
const roles = require('./roles');
const categories = require('./categories');
const { subCategories, createSubCategories } = require('./subCategories');
const privileges = require('./privileges');
const globalVariables = require('./globalVariables');
const dataSets = require('./datasets');
const activeStatusInd = 1; // shared for all users
const custSysId = 1; // shared for all users
const customerId = 1; // shared for all users
const customerCode = 'SYNCHRONOSS'; // shared for all users
const productId = 1; // shared for all users
let AdminHelper = require('../api/AdminHelper');
let APICommonHelpers = require('../api/APICommonHelpers');
let AnalysisHelper = require('../api/AnalysisHelper');
let Constants = require('../Constants');

class TestDataGenerator {
  generateUsersRolesPrivilegesCategories(url, token) {
    let analysisHelper = new AnalysisHelper();
    let adminHelper = new AdminHelper();
    let apiUrl = APICommonHelpers.getApiUrl(url);

    // Generate roles
    const rolesListWithAdmin = adminHelper.generateRole(
      apiUrl,
      roles.admin,
      token,
      custSysId,
      activeStatusInd,
      customerCode,
      users.anyUser.email
    );
    const rolesListWithUser = adminHelper.generateRole(
      apiUrl,
      roles.userOne,
      token,
      custSysId,
      activeStatusInd,
      customerCode,
      users.anyUser.email
    );
    roles.admin.roleId = adminHelper.getRoleIdByRoleName(
      rolesListWithAdmin,
      roles.admin.roleName
    );
    roles.userOne.roleId = adminHelper.getRoleIdByRoleName(
      rolesListWithUser,
      roles.userOne.roleName
    );

    // Generate users
    adminHelper.generateUser(
      apiUrl,
      users.admin,
      roles.admin.roleId,
      token,
      activeStatusInd,
      customerId,
      users.anyUser.email,
      users.anyUser.password
    );
    adminHelper.generateUser(
      apiUrl,
      users.userOne,
      roles.userOne.roleId,
      token,
      activeStatusInd,
      customerId,
      users.anyUser.email,
      users.anyUser.password
    );

    // Generate categories
    adminHelper.generateCategory(
      apiUrl,
      categories.privileges,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    );
    let categoriesList = adminHelper.generateCategory(
      apiUrl,
      categories.analyses,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    );

    // Generate sub-categories for all privileges categories
    for (let [name, subCategory] of Object.entries(subCategories)) {
      adminHelper.generateSubCategory(
        apiUrl,
        categories.privileges,
        subCategory,
        categoriesList,
        token,
        activeStatusInd,
        productId,
        customerId,
        users.anyUser.email
      );
    }
    // Generate sub-category for create category
    adminHelper.generateSubCategory(
      apiUrl,
      categories.analyses,
      createSubCategories.createAnalysis,
      categoriesList,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    );

    //Generate privileges for Admin
    for (let [name, privilege] of Object.entries(privileges)) {
      adminHelper.generatePrivilege(
        apiUrl,
        privilege,
        roles.admin,
        categories.privileges,
        subCategories[name],
        token,
        productId,
        users.anyUser.email
      );
    }
    //Generate privileges for Admin to create analysis category
    adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.admin,
      categories.analyses,
      createSubCategories.createAnalysis,
      token,
      productId,
      users.anyUser.email
    );

    //Generate privileges for User
    for (let [name, privilege] of Object.entries(privileges)) {
      adminHelper.generatePrivilege(
        apiUrl,
        privilege,
        roles.userOne,
        categories.privileges,
        subCategories[name],
        token,
        productId,
        users.anyUser.email
      );
    }
    //Generate privileges for Admin to create analysis category
    adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.userOne,
      categories.analyses,
      createSubCategories.createAnalysis,
      token,
      productId,
      users.anyUser.email
    );

    // Generate analyses
    let semanticId = analysisHelper.getSemanticId(
      apiUrl,
      dataSets.pivotChart,
      token
    ); // Get semanticId (dataset ID)

    let name = `Column Chart ${globalVariables.e2eId}`;
    let description = `Column Chart created by e2e under sub category: ${new Date()}`;
    let analysisType = Constants.CHART;
    let subType = 'column';

    // Create charts for different sub categories
    for (let [name, subCategory] of Object.entries(subCategories)) {
      analysisHelper.createAnalysis(
        apiUrl,
        token,
        name,
        description,
        analysisType,
        subType,
        dataSets.pivotChart,
        null,
        subCategory,
        semanticId
      );
    }

    // Generate chart for create analysis sub category
    analysisHelper.createAnalysis(
      apiUrl,
      token,
      name,
      description,
      analysisType,
      subType,
      dataSets.pivotChart,
      null,
      createSubCategories.createAnalysis,
      semanticId
    );

    // Generate categories for observe module
    let observeCategoriesList = adminHelper.generateCategory(
      apiUrl,
      categories.observe,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email,
      2
    );

    adminHelper.generateSubCategory(
      apiUrl,
      categories.observe,
      createSubCategories.observeSubCategory,
      observeCategoriesList,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email,
      2
    );

    adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.admin,
      categories.observe,
      createSubCategories.observeSubCategory,
      token,
      productId,
      users.anyUser.email,
      2
    );

    adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.userOne,
      categories.observe,
      createSubCategories.observeSubCategory,
      token,
      productId,
      users.anyUser.email,
      2
    );
  }
}

module.exports = TestDataGenerator;
