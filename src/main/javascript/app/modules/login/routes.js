export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'login',
      url: '/login',
      component: 'loginComponent'
    },
    {
      name: 'changePassword',
      url: '/changePwd',
      component: 'passwordChangeComponent'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
