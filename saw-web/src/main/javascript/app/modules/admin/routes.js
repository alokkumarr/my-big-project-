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
    }, {
      name: 'export',
      url: '/export',
      component: 'exportComponent'
    }, {
      name: 'import',
      url: '/import',
      component: 'importComponent'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
