export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'admin',
      url: '/admin',
      component: 'usersView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
