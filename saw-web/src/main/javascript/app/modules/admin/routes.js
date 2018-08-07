import {AdminMainViewComponent} from './main-view';
import {
  UsersTableHeader,
  RolesTableHeader,
  CategoriesTableHeader
} from './consts';

export function routesConfig($stateProvider) {
  'ngInject';

  const states = [
    {
      name: 'admin',
      url: '/admin',
      redirectTo: 'admin.user'
    }, {
      name: 'admin.user',
      url: '/user',
      component: AdminMainViewComponent,
      resolve: [{
        token: 'columns',
        resolveFn: () => UsersTableHeader
      }, {
        token: 'section',
        resolveFn: () => 'user'
      }]
    }, {
      name: 'admin.role',
      url: '/role',
      component: AdminMainViewComponent,
      resolve: [{
        token: 'columns',
        resolveFn: () => RolesTableHeader
      }, {
        token: 'section',
        resolveFn: () => 'role'
      }]
    }, {
      name: 'admin.categories',
      url: '/categories',
      component: AdminMainViewComponent,
      resolve: [{
        token: 'columns',
        resolveFn: () => CategoriesTableHeader
      }, {
        token: 'section',
        resolveFn: () => 'category'
      }]
    }, {
    //   name: 'admin.categories',
    //   url: '/categories',
    //   component: 'categoriesView'
    // }, {
      name: 'admin.privilege',
      url: '/privilege?role',
      component: 'privilegesView'
    }, {
      name: 'admin.export',
      url: '/export',
      component: 'exportComponent'
    }, {
      name: 'admin.import',
      url: '/import',
      component: 'importComponent'
    }
  ];

  states.forEach(state => {
    $stateProvider.state(state);
  });
}
