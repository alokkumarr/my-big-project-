const apiCall = require('../../javascript/helpers/apiCall');
const users = require('../../javascript/data/users');
const roles = require('../../javascript/data/roles');
const categories = require('../../javascript/data/categories');
const subCategories = require('../../javascript/data/subCategories');
const privileges = require('../../javascript/data/privileges');
const request = require('sync-request');
const globalVariables = require('../../javascript/helpers/globalVariables');
const dataSets = require('../../javascript/data/datasets');
const protractorConf = require('../../../../../saw-web/conf/protractor.conf');
const urlParser = require('url');
//const url = 'http://localhost/'; // API base url
const activeStatusInd = 1; // shared for all users
const custSysId = 1; // shared for all users
const customerId = 1; // shared for all users
const customerCode = 'SYNCHRONOSS'; // shared for all users
const productId = 1; // shared for all users
const moduleId = 1; // shared for all users
let url = '';

module.exports = {
  // Generates token for admin which has been created on docker start
  token: (baseUrl) => {
    const payload = {'masterLoginId': users.masterAdmin.loginId, 'password': users.masterAdmin.password};
    const q = urlParser.parse(baseUrl, true);
    url = 'http://'+q.host+'/'; // API base url
    return 'Bearer '.concat(JSON.parse(request('POST', url + 'security/doAuthenticate',
      {json: payload}).getBody()).aToken);
  },
  usersRolesPrivilegesCategories: (token) => {
    //console.log('e2e unique identifier: ' + globalVariables.e2eId);

    // Generate roles
    const rolesListWithAdmin = generateRole(roles.admin, token);
    const rolesListWithUser = generateRole(roles.userOne, token);
    roles.admin.roleId = getRoleIdByRoleName(rolesListWithAdmin, roles.admin.roleName);
    roles.userOne.roleId = getRoleIdByRoleName(rolesListWithUser, roles.userOne.roleName);

    // Generate users
    generateUser(users.admin, roles.admin.roleId, token);
    generateUser(users.userOne, roles.userOne.roleId, token);

    // Generate categories
    generateCategory(categories.privileges, token);
    let categoriesList = generateCategory(categories.analyses, token);

    // Generate sub-categories
    generateSubCategory(categories.privileges, subCategories.all, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.create, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.edit, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.fork, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.execute, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.publish, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.export, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.delete, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.multiple, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.noPrivileges, categoriesList, token);
    generateSubCategory(categories.privileges, subCategories.view, categoriesList, token);
    generateSubCategory(categories.analyses, subCategories.createAnalysis, categoriesList, token);

    // Generate privileges
    generatePrivilege(privileges.all, roles.admin, categories.privileges, subCategories.all, token);
    generatePrivilege(privileges.create, roles.admin, categories.privileges, subCategories.create, token);
    generatePrivilege(privileges.edit, roles.admin, categories.privileges, subCategories.edit, token);
    generatePrivilege(privileges.fork, roles.admin, categories.privileges, subCategories.fork, token);
    generatePrivilege(privileges.execute, roles.admin, categories.privileges, subCategories.execute, token);
    generatePrivilege(privileges.publish, roles.admin, categories.privileges, subCategories.publish, token);
    generatePrivilege(privileges.export, roles.admin, categories.privileges, subCategories.export, token);
    generatePrivilege(privileges.delete, roles.admin, categories.privileges, subCategories.delete, token);
    generatePrivilege(privileges.multiple, roles.admin, categories.privileges, subCategories.multiple, token);
    generatePrivilege(privileges.noAccess, roles.admin, categories.privileges, subCategories.noPrivileges, token);
    generatePrivilege(privileges.view, roles.admin, categories.privileges, subCategories.view, token);
    generatePrivilege(privileges.all, roles.admin, categories.analyses, subCategories.createAnalysis, token);


    generatePrivilege(privileges.all, roles.userOne, categories.privileges, subCategories.all, token);
    generatePrivilege(privileges.create, roles.userOne, categories.privileges, subCategories.create, token);
    generatePrivilege(privileges.edit, roles.userOne, categories.privileges, subCategories.edit, token);
    generatePrivilege(privileges.fork, roles.userOne, categories.privileges, subCategories.fork, token);
    generatePrivilege(privileges.execute, roles.userOne, categories.privileges, subCategories.execute, token);
    generatePrivilege(privileges.publish, roles.userOne, categories.privileges, subCategories.publish, token);
    generatePrivilege(privileges.export, roles.userOne, categories.privileges, subCategories.export, token);
    generatePrivilege(privileges.delete, roles.userOne, categories.privileges, subCategories.delete, token);
    generatePrivilege(privileges.multiple, roles.userOne, categories.privileges, subCategories.multiple, token);
    generatePrivilege(privileges.noAccess, roles.userOne, categories.privileges, subCategories.noPrivileges, token);
    generatePrivilege(privileges.view, roles.userOne, categories.privileges, subCategories.view, token);
    generatePrivilege(privileges.all, roles.userOne, categories.analyses, subCategories.createAnalysis, token);

    // Generate analyses
    let semanticId = getSemanticId(dataSets.pivotChart, token); // Get semanticId (dataset ID)


    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.all, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.create, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.edit, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.fork, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.execute, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.publish, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.export, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.delete, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.multiple, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.noPrivileges, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.view, token);
    generateChart(semanticId, dataSets.pivotChart, users.masterAdmin, subCategories.createAnalysis, token);
  }
};

// Returns roleID of created role if roleName is unique. If not unique then will return ID of last match
function generateRole(role, token) {
  const payload = {
    'activeStatusInd': activeStatusInd,
    'custSysId': custSysId,
    'customerCode': customerCode,
    'masterLoginId': users.anyUser.email, // all roles will share same email
    'myAnalysis': true,
    'roleType': role.roleType,
    'roleName': role.roleName,
    'roleDesc': role.roleDesc
  };

  return apiCall.post(url + 'security/auth/admin/cust/manage/roles/add', payload, token).roles; // return list of all
                                                                                                // roles
}

function getRoleIdByRoleName(rolesList, roleName) {
  return getValueFromListByKeyValue(rolesList, 'roleName', roleName, 'roleSysId');
}

function getSubCategoryIdBySubCategoryName(subCategoriesList, subCategoryName) {
  return getValueFromListByKeyValue(subCategoriesList, 'subCategoryName', subCategoryName, 'subCategoryId');
}

function generateUser(user, roleId, token) {
  const payload = {
    'activeStatusInd': activeStatusInd,
    'customerId': customerId,
    'masterLoginId': user.loginId,
    'firstName': user.firstName,
    'lastName': user.lastName,
    'myAnalysis': true, // Because of a bug it has to be true otherwise user appears with white screen
    'password': users.anyUser.password,
    'roleId': roleId, // user and role are connected by roleId
    'email': users.anyUser.email // all users will share same email
  };

  return apiCall.post(url + 'security/auth/admin/cust/manage/users/add', payload, token); // return list of all users
}

function generateCategory(category, token) {
  const payload = {
    'activeStatusInd': activeStatusInd,
    'productId': productId,
    'moduleId': moduleId,
    'categoryId': null,
    'categoryName': category.name,
    'categoryDesc': category.description,
    'customerId': customerId,
    'masterLoginId': users.anyUser.email // all users will share same email
  };

  // get list of all categories
  const categoriesList = apiCall.post(url + 'security/auth/admin/cust/manage/categories/add', payload, token).categories;

  category.id = getCategoryIdFromListByCategoryName(categoriesList, category);

  // return list of categories
  return categoriesList;
}

function generateSubCategory(parentCategory, subCategory, categoriesList, token) {
  parentCategory.id = getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryId');
  parentCategory.type = getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryType');
  parentCategory.code = getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryCode');

  const payload = {
    'activeStatusInd': activeStatusInd,
    'productId': productId,
    'moduleId': moduleId,
    'categoryId': parentCategory.id,
    'categoryName': subCategory.name,
    'categoryDesc': subCategory.description,
    'subCategoryInd': true,
    'categoryType': parentCategory.type,
    'categoryCode': parentCategory.code,
    'customerId': customerId,
    'masterLoginId': users.anyUser.email // all users will share same email
  };
  // get list of all categories
  const categoriesListWithSubCategory = apiCall.post(url + 'security/auth/admin/cust/manage/categories/add', payload, token).categories;
  // get list of all sub-categories
  let subCategoriesList = getSubCategoryListByCategoryName(categoriesListWithSubCategory, parentCategory.name);

  subCategory.id = getSubCategoryIdBySubCategoryName(subCategoriesList, subCategory.name);

  return subCategory.id;
}

// Returns privileges list
function generatePrivilege(privilege, role, parentCategory, subCategory, token) {
  const payload = {
    'productId': productId,
    'moduleId': moduleId,
    'roleId': role.roleId,
    'categoryCode': parentCategory.code,
    'categoryId': parentCategory.id,
    'subCategoriesPrivilege': [{
      'privilegeCode': privilege.privilegeCode,
      'privilegeDesc': privilege.privilegeDesc,
      'subCategoryId': subCategory.id,
      'privilegeId': 0
    }],
    'categoryType': parentCategory.type,
    'customerId': 1,
    'masterLoginId': users.anyUser.email
  };

  return apiCall.post(url + 'security/auth/admin/cust/manage/privileges/upsert', payload, token);
}

/* Returns object found in list by passed inputValue and inputKey.
 * For example if we have list like:
 * [{a: 1, b: 2},{a: 3, b :4}]
 * There are two items in list: {a: 1, b: 2} and {a: 3, b :4}
 * We want to find value(or object) in key 'b' by key-value `a: 1`
 * So we invoke function like getValueFromListByKeyValue(list, a, 1, b)
 *
 * Caution: inputValue should be unique because function takes returnValue from any matching inputKey-inputValue
 */
function getValueFromListByKeyValue(list, inputKey, inputValue, getValueOfKey) {
  let returnValue;

  for (let i = 0; i < list.length; i++) {
    const data = list[i];
    //console.log(JSON.stringify(list[i]));

    // Iterate each item in list
    // If inputValue matches, return value of getValueOfKey from this item in list
    Object.keys(data).forEach(function (key) {
      if (key === inputKey && data[key] === inputValue) {
        returnValue = data[getValueOfKey];
        //console.log('Found! ' + inputKey + ': \'' + data[key] + '\'');
      }
    });
  }
  if (returnValue == null) {
    throw new Error('There is no ' + inputKey + ' in list with value ' + inputValue);
  }
  return returnValue;
}

// Returns categoryID from list of available categories in system
// Caution: category names should be unique because function takes id from any matching category name
function getCategoryIdFromListByCategoryName(categoriesList, parentCategory) {
  return getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryId');
}

function getSubCategoryListByCategoryName(categoryList, categoryName) {
  return getValueFromListByKeyValue(categoryList, 'categoryName', categoryName, 'subCategories');
}

function generateChart(semanticId, dataSetName, user, subCategory, token) {
  // Create chart
  const createPayload = {
    'contents': {
      'keys': [{
        'customerCode': 'SYNCHRONOSS',
        'module': 'ANALYZE',
        'id': semanticId,
        'analysisType': 'chart'
      }], 'action': 'create'
    }
  };

  // Get chart ID
  const chartID = apiCall.post(url + 'services/analysis', createPayload, token).contents.analyze[0].id;

  // Update and save chart
  function getPayload(action) {
    return {
      'contents': {
        'keys': [{
          'customerCode': customerCode,
          'module': 'ANALYZE',
          'id': chartID,
          'type': 'chart'
        }],
        'action': action,
        'analyze': [{
          'id': chartID,
          'dataSecurityKey': '',
          'type': 'chart',
          'module': 'ANALYZE',
          'metric': 'sample-elasticsearch',
          'metricName': dataSetName,
          'customerCode': customerCode,
          'disabled': 'false',
          'checked': 'false',
          'esRepository': {'storageType': 'ES', 'indexName': 'sample', 'type': 'sample'},
          'artifacts': [{
            'artifactName': 'sample',
            'columns': [{
              'name': 'string.keyword',
              'type': 'string',
              'columnName': 'string.keyword',
              'displayName': 'String',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'g'
            }, {
              'name': 'long',
              'type': 'long',
              'columnName': 'long',
              'displayName': 'Long',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample'
            }, {
              'name': 'float',
              'type': 'float',
              'columnName': 'float',
              'displayName': 'Float',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample'
            }, {
              'name': 'date',
              'type': 'date',
              'columnName': 'date',
              'displayName': 'Date',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'x',
              'dateFormat': 'MMM d YYYY'
            }, {
              'name': 'integer',
              'type': 'integer',
              'columnName': 'integer',
              'displayName': 'Integer',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample'
            }, {
              'name': 'double',
              'type': 'double',
              'columnName': 'double',
              'displayName': 'Double',
              'aliasName': '',
              'table': 'sales',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'y',
              'aggregate': 'sum',
              'comboType': 'column'
            }]
          }],
          'repository': {'storageType': 'DL', 'objects': [], '_number_of_elements': 0},
          'semanticId': semanticId,
          'createdTimestamp': 1524821356471,
          'userId': user.userId,
          'userFullName': user.loginId,
          'chartType': 'column',
          'name': 'Column Chart ' + globalVariables.e2eId,
          'description': 'Description: Column Chart for e2e',
          'categoryId': subCategory.id,
          'scheduled': null,
          'sqlBuilder': {
            'booleanCriteria': 'AND',
            'filters': [],
            'dataFields': [{
              'name': 'double',
              'type': 'double',
              'columnName': 'double',
              'displayName': 'Double',
              'aliasName': '',
              'table': 'sales',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'y',
              'aggregate': 'sum',
              'comboType': 'column'
            }],
            'nodeFields': [{
              'name': 'string.keyword',
              'type': 'string',
              'columnName': 'string.keyword',
              'displayName': 'String',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'g'
            }, {
              'name': 'date',
              'type': 'date',
              'columnName': 'date',
              'displayName': 'Date',
              'aliasName': '',
              'table': 'sample',
              'joinEligible': false,
              'kpiEligible': true,
              'filterEligible': true,
              'tableName': 'sample',
              'checked': 'x',
              'dateFormat': 'MMM d YYYY'
            }],
            'sorts': [{'columnName': 'string.keyword', 'type': 'string', 'order': 'asc'}, {
              'columnName': 'date',
              'type': 'date',
              'order': 'asc'
            }]
          },
          'xAxis': {'title': null},
          'yAxis': {'title': null},
          'isInverted': false,
          'isStockChart': false,
          'legend': {'align': 'right', 'layout': 'vertical'},
          'saved': true
        }]
      }
    };
  }

  const updatePayload = getPayload('update');
  apiCall.post(url + 'services/analysis', updatePayload, token);

  const executePayload = getPayload('execute');
  return apiCall.post(url + 'services/analysis', executePayload, token);
}


// Returns semantic(dataset) ID by dataset name
function getSemanticId(dataSetName, token) {
  const payload = {
    'contents': {
      'keys': [{'customerCode': customerCode, 'module': 'ANALYZE'}],
      'action': 'search',
      'select': 'headers',
      'context': 'Semantic'
    }
  };
  let response = apiCall.post(url + 'services/md', payload, token);
  const semanticList = response.contents[0].ANALYZE;
  return getValueFromListByKeyValue(semanticList, 'metricName', dataSetName, 'id');
}
