import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';

export function PrivilegesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActivePrivilegesList,
    getRoles,
    getProducts,
    getModules,
    getCategories,
    savePrivilege,
    deletePrivilege,
    updatePrivilege,
    searchPrivileges
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
  function savePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/add`, privilege).then(fpGet('data'));
  }
  function deletePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/delete`, privilege).then(fpGet('data'));
  }
  function updatePrivilege(privilege) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/privileges/edit`, privilege).then(fpGet('data'));
  }
  function searchPrivileges(privileges, searchTerm = '', header) {
    if (!searchTerm) {
      return privileges;
    }
    const term = searchTerm.toUpperCase();
    const matchIn = item => {
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };
    return filter(privileges, item => {
      switch (header) {
        default: {
          return matchIn(item.productName) ||
            matchIn(item.moduleName) ||
            matchIn(item.categoryName) ||
            matchIn(item.roleName) ||
            matchIn(item.privilegeDesc);
        }
        case 'PRODUCT': {
          return matchIn(item.productName);
        }
        case 'MODULE': {
          return matchIn(item.moduleName);
        }
        case 'CATEGORY': {
          return matchIn(item.categoryName);
        }
        case 'ROLE': {
          return matchIn(item.roleName);
        }
        case 'PRIVILEGE DESC': {
          return matchIn(item.privilegeDesc);
        }
      }
    });
  }
}
