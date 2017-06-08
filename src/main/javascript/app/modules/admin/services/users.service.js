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
  function getActiveUsersList(customerId, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/fetch`, customerId).then(fpGet('data'));
  }
  function getRoles(customerId, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/dropdown/getRoles`, customerId).then(fpGet('data'));
  }
  function saveUser(user, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/add`, user).then(fpGet('data'));
  }
  function deleteUser(user, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/delete`, user).then(fpGet('data'));
  }
  function updateUser(user, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/edit`, user).then(fpGet('data'));
  }
}
