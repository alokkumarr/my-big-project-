export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  // $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise(() => {
    const config = localStorage.getItem('sipConfig');
    if (!config) {
      console.log('config do not exist');
      return '/analyze';
    }

    try {
      const pref = angular.fromJson(config);
      if (!angular.isArray(pref.preferences)) {
        console.log('prefereces do not exist');
        return '/analyze';
      }

      const defaultDashboard = pref.preferences.filter(p => p.preferenceName === 'defaultDashboard')[0];
      const defaultDashboardCat = pref.preferences.filter(p => p.preferenceName === 'defaultDashboardCategory')[0];

      if (!defaultDashboard || !defaultDashboardCat) {
        console.log('default do not exist');
        return '/analyze';
      }

      return `/observe/${defaultDashboardCat.preferenceValue}?dashboard=${defaultDashboard.preferenceValue}`;
    } catch (err) {
      console.log('error happened');
      return '/analyze';
    }
  });

  const states = [{
    name: 'root',
    url: '/'
  }, {
    // Dummy route to serve as auth endpoint
    name: 'authenticate',
    url: '/authenticate'
  }];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
