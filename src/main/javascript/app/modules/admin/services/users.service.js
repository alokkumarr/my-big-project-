import fpGet from 'lodash/fp/get';

export function UsersManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveUsersList,
    getRoles,
    saveUser,
    deleteUser,
    updateUser
  };
  function getActiveUsersList(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/fetch`, customerId).then(fpGet('data'));
  }
  function getRoles(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/roles/list`, customerId).then(fpGet('data'));
  }
  function saveUser(user) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/add`, user).then(fpGet('data'));
  }
  function deleteUser(user) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/delete`, user).then(fpGet('data'));
  }
  function updateUser(user) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/edit`, user).then(fpGet('data'));
  }
}
