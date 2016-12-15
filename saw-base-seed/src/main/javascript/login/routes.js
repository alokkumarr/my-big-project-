export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';
  $urlRouterProvider.otherwise('/');

  const states = [
    {
      name: 'changepwd',
      url: '/changePwd',
      component: 'changeComponent',
      onEnter: ($state, $window) => {
        'ngInject';
        // this hack redirecting is only for the moment
        // this should be done on the server
        $window.location = `/change.html`;
      },
      data: {
        authorization: true,
        redirectTo: 'login'
      }
    }, {
      name: 'resetpwd',
      url: '/resetPwd',
      data: {
        authorization: true,
        redirectTo: 'login'
      }
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
