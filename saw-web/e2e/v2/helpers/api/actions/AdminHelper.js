'use strict';
let RestClient = require('../RestClient');;
const Constants = require('../../Constants')
let Utils = require('../../Utils');
const moduleId = 1; // shared for all users
class AdminHelper {

  generateRole(url, role, token, custSysId, activeStatusInd, customerCode, masterLoginId) {
    const payload = {
      activeStatusInd: activeStatusInd, custSysId: custSysId, customerCode: customerCode,
      masterLoginId: masterLoginId, // all roles will share same email
      myAnalysis: true,
      roleType: role.roleType,
      roleName: role.roleName,
      roleDesc: role.roleDesc
    };
    return new RestClient().post(url + Constants.API_ROUTES.ROLES, payload, token).roles;
  }

  generateUser(url, user, roleId, token, activeStatusInd, customerId, email, password) {
    const payload = {
      activeStatusInd: activeStatusInd,
      customerId: customerId,
      masterLoginId: user.loginId,
      firstName: user.firstName,
      lastName: user.lastName,
      myAnalysis: true,
      password: password,
      roleId: roleId, // user and role are connected by roleId
      email: email
    };

    return new RestClient().post(url + Constants.API_ROUTES.USERS, payload, token); // return list of all users
  }

  generateCategory(url, category, token, activeStatusInd, productId, customerId, email, module = null) {
    const payload = {
      activeStatusInd: activeStatusInd,
      productId: productId,
      moduleId: module ? module : moduleId,
      categoryId: null,
      categoryName: category.name,
      categoryDesc: category.description,
      customerId: customerId,
      masterLoginId: email // all users will share same email
    };
    // get list of all categories
    const categoriesList = new RestClient().post(url + Constants.API_ROUTES.ADD_CATEGORIES, payload,token).categories;

    category.id = this.getCategoryIdFromListByCategoryName(categoriesList, category);

    // return list of categories
    return categoriesList;
  }

  generateSubCategory(url, parentCategory, subCategory, categoriesList, token, activeStatusInd, productId, customerId, email, module = null) {
    parentCategory.id = new Utils().getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryId');
    parentCategory.type = new Utils().getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryType');
    parentCategory.code = new Utils().getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryCode');

    const payload = {
      activeStatusInd: activeStatusInd,
      productId: productId,
      moduleId: module ? module : moduleId,
      categoryId: parentCategory.id,
      categoryName: subCategory.name,
      categoryDesc: subCategory.description,
      subCategoryInd: true,
      categoryType: parentCategory.type,
      categoryCode: parentCategory.code,
      customerId: customerId,
      masterLoginId: email
    };
    // get list of all categories
    const categoriesListWithSubCategory = new RestClient().post(url + Constants.API_ROUTES.ADD_CATEGORIES, payload, token).categories;
    // get list of all sub-categories
    let subCategoriesList = this.getSubCategoryListByCategoryName(categoriesListWithSubCategory, parentCategory.name);

    subCategory.id = this.getSubCategoryIdBySubCategoryName(subCategoriesList, subCategory.name);

    return subCategory.id;
  }

  // Returns privileges list
  generatePrivilege(url, privilege, role, parentCategory, subCategory, token, productId, email, module = null) {

    const payload = {
      productId: productId,
      moduleId: module ? module : moduleId,
      roleId: role.roleId,
      categoryCode: parentCategory.code,
      categoryId: parentCategory.id,
      subCategoriesPrivilege: [{
        privilegeCode: privilege.privilegeCode,
        privilegeDesc: privilege.privilegeDesc,
        subCategoryId: subCategory.id,
        privilegeId: 0
      }],
      categoryType: parentCategory.type,
      customerId: 1,
      masterLoginId: email
    };

    return new RestClient().post(url + Constants.API_ROUTES.PRIVILEGES, payload, token);
  }

  getRoleIdByRoleName(rolesList, roleName) {
    return new Utils().getValueFromListByKeyValue(rolesList, 'roleName', roleName, 'roleSysId');
  }

  // Returns categoryID from list of available categories in system
  // Caution: category names should be unique because function takes id from any matching category name
  getCategoryIdFromListByCategoryName(categoriesList, parentCategory) {
    return new Utils().getValueFromListByKeyValue(categoriesList, 'categoryName', parentCategory.name, 'categoryId');
  }
  getSubCategoryListByCategoryName(categoryList, categoryName) {
    return new Utils().getValueFromListByKeyValue(categoryList, 'categoryName', categoryName, 'subCategories');
  }
  getSubCategoryIdBySubCategoryName(subCategoriesList, subCategoryName) {
    return new Utils().getValueFromListByKeyValue(subCategoriesList, 'subCategoryName', subCategoryName, 'subCategoryId');
  }


  getSubCategoriesByCategoryName(url, token, categoryName) {
    let requestPayLoad = 1;
    let response = new RestClient().post(url + Constants.API_ROUTES.CATEGORIES_FETCH, requestPayLoad, token);
    if (response) {
      for (let category of response.categories) {
        if (category.categoryName.trim().toLowerCase() === categoryName.trim().toLowerCase()) {
          return category.subCategories;
        }
      }
    }
    return null;
  }

}

module.exports = AdminHelper;
