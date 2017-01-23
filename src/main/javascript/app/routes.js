export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  // $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise('/');

  const states = [{
    name: 'root',
    url: '/'
  }];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
