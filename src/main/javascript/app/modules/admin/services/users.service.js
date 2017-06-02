import fpGet from 'lodash/fp/get';

export function UsersManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveUsersList
  };
  function getActiveUsersList(customerId, token) {
    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
    return $http.post(`${loginUrl}/auth/admin/cust/manage/users/fetch`, customerId).then(fpGet('data'));
  }
}
