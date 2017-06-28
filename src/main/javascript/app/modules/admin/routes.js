export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'admin',
      url: '/admin',
      component: 'usersView'
    }, {
      name: 'role',
      url: '/role',
      component: 'rolesView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
