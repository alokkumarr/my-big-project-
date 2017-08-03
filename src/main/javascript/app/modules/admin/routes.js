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
    }, {
      name: 'privilege',
      url: '/privilege?role',
      component: 'privilegesView'
    }, {
      name: 'categories',
      url: '/categories',
      component: 'categoriesView'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
