export function routesConfig($stateProvider, $urlRouterProvider) {
  'ngInject';
  $urlRouterProvider.otherwise('/');

  const states = [{
    name: 'login',
    url: '/',
    component: 'loginComponent'
  }, {
    name: 'changepwd',
    url: '/changePwd',
    component: 'changeComponent'
  }, {
    name: 'resetpwd',
    url: '/resetPwd'
  }];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
