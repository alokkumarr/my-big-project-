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
let Utils = require('../Utils');
const logger = require('../../conf/logger')(__filename);

class TestDataGenerator {
  generateUsersRolesPrivilegesCategories(url, token) {
    let analysisHelper = new AnalysisHelper();
    let adminHelper = new AdminHelper();
    let apiUrl = APICommonHelpers.getApiUrl(url);
    let utils = new Utils();
    // Generate roles
    const rolesListWithAdmin = utils.validApiCall(adminHelper.generateRole(
      apiUrl,
      roles.admin,
      token,
      custSysId,
      activeStatusInd,
      customerCode,
      users.anyUser.email
    ),'generate roles for admin');

    const rolesListWithUser =  utils.validApiCall(adminHelper.generateRole(
      apiUrl,
      roles.userOne,
      token,
      custSysId,
      activeStatusInd,
      customerCode,
      users.anyUser.email
    ), 'generate roles for user');

    roles.admin.roleId = adminHelper.getRoleIdByRoleName(
      rolesListWithAdmin,
      roles.admin.roleName
    );
    roles.userOne.roleId = adminHelper.getRoleIdByRoleName(
      rolesListWithUser,
      roles.userOne.roleName
    );

    // Generate users
    utils.validApiCall(adminHelper.generateUser(
      apiUrl,
      users.admin,
      roles.admin.roleId,
      token,
      activeStatusInd,
      customerId,
      users.anyUser.email,
      users.anyUser.password
    ),'generate user for admin');

    utils.validApiCall(adminHelper.generateUser(
      apiUrl,
      users.userOne,
      roles.userOne.roleId,
      token,
      activeStatusInd,
      customerId,
      users.anyUser.email,
      users.anyUser.password
    ), 'generate user for userOne');

    // Generate categories
    utils.validApiCall(adminHelper.generateCategory(
      apiUrl,
      categories.privileges,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    ), 'generate privileges for admin');

    let categoriesList = utils.validApiCall(adminHelper.generateCategory(
      apiUrl,
      categories.analyses,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    ),'generateCategory for analysis');

    // Generate sub-categories for all privileges categories
    for (let [name, subCategory] of Object.entries(subCategories)) {
      utils.validApiCall(adminHelper.generateSubCategory(
        apiUrl,
        categories.privileges,
        subCategory,
        categoriesList,
        token,
        activeStatusInd,
        productId,
        customerId,
        users.anyUser.email
      ),'generateSubCategory privileges:'+subCategory);
    }
    // Generate sub-category for create category
    utils.validApiCall(adminHelper.generateSubCategory(
      apiUrl,
      categories.analyses,
      createSubCategories.createAnalysis,
      categoriesList,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email
    ),'generateSubCategory for createAnalysis');

    //Generate privileges for Admin
    for (let [name, privilege] of Object.entries(privileges)) {
      utils.validApiCall(adminHelper.generatePrivilege(
        apiUrl,
        privilege,
        roles.admin,
        categories.privileges,
        subCategories[name],
        token,
        productId,
        users.anyUser.email
      ),'generatePrivilege for '+JSON.stringify(subCategories[name]));
    }
    //Generate privileges for Admin to create analysis category
    utils.validApiCall(adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.admin,
      categories.analyses,
      createSubCategories.createAnalysis,
      token,
      productId,
      users.anyUser.email
    ),'generatePrivilege for '+JSON.stringify(createSubCategories.createAnalysis));

    //Generate privileges for User
    for (let [name, privilege] of Object.entries(privileges)) {
      utils.validApiCall(adminHelper.generatePrivilege(
        apiUrl,
        privilege,
        roles.userOne,
        categories.privileges,
        subCategories[name],
        token,
        productId,
        users.anyUser.email
      ),'generatePrivilege userOne with '+JSON.stringify(subCategories[name]));
    }
    //Generate privileges for Admin to create analysis category
    utils.validApiCall(adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.userOne,
      categories.analyses,
      createSubCategories.createAnalysis,
      token,
      productId,
      users.anyUser.email
    ),'generatePrivilege userOne with '+JSON.stringify(createSubCategories.createAnalysis));

    // Generate analyses
    // Get semanticId (dataset ID)
    let semanticId = utils.validApiCall(analysisHelper.getSemanticId(
      apiUrl,
      dataSets.pivotChart,
      token
    ),'getSemanticId');

    let name = `Column Chart ${globalVariables.e2eId}`;
    let description = `Column Chart created by e2e under sub category: ${new Date()}`;
    let analysisType = Constants.CHART;
    let subType = 'column';

    // Create charts for different sub categories
    for (let [name, subCategory] of Object.entries(subCategories)) {
      utils.validApiCall(analysisHelper.createAnalysis(
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
      ),'createAnalysis: for '+subCategory);
    }

    // Generate chart for create analysis sub category
    utils.validApiCall(analysisHelper.createAnalysis(
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
    ),'createAnalysis for '+createSubCategories.createAnalysis);

    // Generate categories for observe module
    let observeCategoriesList = utils.validApiCall(adminHelper.generateCategory(
      apiUrl,
      categories.observe,
      token,
      activeStatusInd,
      productId,
      customerId,
      users.anyUser.email,
      2
    ),'generateCategory for '+categories.observe);

    utils.validApiCall(adminHelper.generateSubCategory(
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
    ),'generateSubCategory for '+createSubCategories.observeSubCategory);

    utils.validApiCall(adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.admin,
      categories.observe,
      createSubCategories.observeSubCategory,
      token,
      productId,
      users.anyUser.email,
      2
    ),'generatePrivilege for admin with '+ createSubCategories.observeSubCategory);

    utils.validApiCall(adminHelper.generatePrivilege(
      apiUrl,
      privileges.all,
      roles.userOne,
      categories.observe,
      createSubCategories.observeSubCategory,
      token,
      productId,
      users.anyUser.email,
      2
    ),'generatePrivilege for user with '+createSubCategories.observeSubCategory);
  }
}

module.exports = TestDataGenerator;
