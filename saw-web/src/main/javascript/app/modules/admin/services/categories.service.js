import * as fpGet from 'lodash/fp/get';

export function CategoriesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveCategoriesList,
    getProducts,
    getModules,
    getParentCategories,
    saveCategory,
    deleteSubCategories,
    deleteCategories,
    updateCategory
  };
  function getActiveCategoriesList(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/fetch`, customerId).then(fpGet('data'));
  }
  function getProducts(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/products/list`, customerId).then(fpGet('data'));
  }
  function getModules(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/modules/list`, inputObject).then(fpGet('data'));
  }
  function getParentCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/parent/list`, inputObject).then(fpGet('data'));
  }
  function saveCategory(category) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/add`, category).then(fpGet('data'));
  }
  function deleteSubCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/subcategories/delete`, inputObject).then(fpGet('data'));
  }
  function deleteCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/delete`, inputObject).then(fpGet('data'));
  }
  function updateCategory(category) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/edit`, category).then(fpGet('data'));
  }
}
