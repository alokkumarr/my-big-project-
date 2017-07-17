import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';

export function RolesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveRolesList,
    getRoleTypes,
    saveRole,
    deleteRole,
    updateRole,
    searchRoles
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
  function searchRoles(roles, searchTerm = '', header) {
    if (!searchTerm) {
      return roles;
    }
    const term = searchTerm.toUpperCase();
    const matchIn = item => {
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };
    return filter(roles, item => {
      switch (header) {
        default: {
          return matchIn(item.roleName) ||
            matchIn(item.roleType) ||
            matchIn(item.roleDesc) ||
            matchIn(item.activeStatusInd);
        }
        case 'ROLE NAME': {
          return matchIn(item.roleName);
        }
        case 'ROLE TYPE': {
          return matchIn(item.roleType);
        }
        case 'ROLE DESCRIPTION': {
          return matchIn(item.roleDesc);
        }
        case 'STATUS': {
          return matchIn(item.activeStatusInd);
        }
      }
    });
  }
}
