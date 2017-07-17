import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';

export function UsersManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveUsersList,
    getRoles,
    saveUser,
    deleteUser,
    updateUser,
    searchUsers
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
  function searchUsers(users, searchTerm = '', header) {
    if (!searchTerm) {
      return users;
    }
    const term = searchTerm.toUpperCase();
    const matchIn = item => {
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };
    return filter(users, item => {
      switch (header) {
        default: {
          return matchIn(item.masterLoginId) ||
            matchIn(item.roleName) ||
            matchIn(item.firstName) ||
            matchIn(item.lastName) ||
            matchIn(item.email) ||
            matchIn(item.activeStatusInd);
        }
        case 'LOGIN ID': {
          return matchIn(item.masterLoginId);
        }
        case 'ROLE': {
          return matchIn(item.roleName);
        }
        case 'FIRST NAME': {
          return matchIn(item.firstName);
        }
        case 'LAST NAME': {
          return matchIn(item.lastName);
        }
        case 'EMAIL': {
          return matchIn(item.email);
        }
        case 'STATUS': {
          return matchIn(item.activeStatusInd);
        }
      }
    });
  }
}
