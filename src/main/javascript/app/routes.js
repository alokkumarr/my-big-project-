export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';

  // $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'app',
      url: '/',
      component: 'root'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
