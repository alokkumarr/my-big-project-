import * as fpGet from 'lodash/fp/get';

export function PrivilegesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActivePrivilegesList,
    getRoles,
    getProducts,
    getModules,
    getCategories,
    getParentCategories,
    savePrivilege,
    deletePrivilege,
    updatePrivilege,
    getSubCategories
  };
  function getActivePrivilegesList(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/fetch`, customerId).then(fpGet('data'));
  }
  function getRoles(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/list`, customerId).then(fpGet('data'));
  }
  function getProducts(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/products/list`, customerId).then(fpGet('data'));
  }
  function getModules(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/modules/list`, inputObject).then(fpGet('data'));
  }
  function getCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/list`, inputObject).then(fpGet('data'));
  }
  function getParentCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/parent/list`, inputObject).then(fpGet('data'));
  }
  function savePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/upsert`, privilege).then(fpGet('data'));
  }
  function deletePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/delete`, privilege).then(fpGet('data'));
  }
  function updatePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/upsert`, privilege).then(fpGet('data'));
  }
  function getSubCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/subCategoriesWithPrivilege/list`, inputObject).then(fpGet('data'));
  }
}
