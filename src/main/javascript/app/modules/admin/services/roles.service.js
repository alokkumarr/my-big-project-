import fpGet from 'lodash/fp/get';

export function RolesManagementService($http) {
  'ngInject';
  return {
    getActiveRolesList,
    getRoleTypes,
    saveRole,
    deleteRole,
    updateRole
  };
  function getActiveRolesList(customerId) {
    return $http.post(`http://10.16.53.124:9000/saw-security/auth/admin/cust/manage/roles/fetch`, customerId).then(fpGet('data'));
  }
  function getRoleTypes() {
    return $http.post(`http://10.16.53.124:9000/saw-security/auth/admin/cust/manage/dropdown/getRoleTypes`).then(fpGet('data'));
  }
  function saveRole(role) {
    return $http.post(`http://10.16.53.124:9000/saw-security/auth/admin/cust/manage/roles/add`, role).then(fpGet('data'));
  }
  function deleteRole(role) {
    return $http.post(`http://10.16.53.124:9000/saw-security/auth/admin/cust/manage/roles/delete`, role).then(fpGet('data'));
  }
  function updateRole(role) {
    return $http.post(`http://10.16.53.124:9000/saw-security/auth/admin/cust/manage/roles/edit`, role).then(fpGet('data'));
  }
}
