export function routesConfig($stateProvider, $urlRouterProvider, $locationProvider) {
  'ngInject';

  $locationProvider.html5Mode(true);
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'login',
      url: '/login',
      component: 'loginComponent',
      data: {
        title: 'Login'
      }
    },
    {
      name: 'changePassword',
      url: '/changePwd',
      component: 'passwordChangeComponent',
      data: {
        title: 'Change Password'
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
