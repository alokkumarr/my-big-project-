import fpGet from 'lodash/fp/get';

export function RolesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveRolesList,
    getRoleTypes,
    saveRole,
    deleteRole,
    updateRole
  };
  function getActiveRolesList(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/fetch`, customerId).then(fpGet('data'));
  }
  function getRoleTypes() {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/types/list`).then(fpGet('data'));
  }
  function saveRole(role) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/add`, role).then(fpGet('data'));
  }
  function deleteRole(role) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/delete`, role).then(fpGet('data'));
  }
  function updateRole(role) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/edit`, role).then(fpGet('data'));
  }
}
