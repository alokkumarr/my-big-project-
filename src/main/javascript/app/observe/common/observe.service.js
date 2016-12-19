import get from 'lodash/fp/get';

export function ObserveService($http) {
  'ngInject';

  return {
    getMenu
  };

  function getMenu() {
    return $http.get('/api/menu/observe').then(get('data'));
  }
}
