import {AdminMainViewComponent} from './main-view';
import {
  UsersTableHeader,
  RolesTableHeader,
  PrivilegesTableHeader,
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
      name: 'admin.privilege',
      url: '/privilege?role',
      component: AdminMainViewComponent,
      resolve: [{
        token: 'columns',
        resolveFn: () => PrivilegesTableHeader
      }, {
        token: 'section',
        resolveFn: () => 'privilege'
      }]
    // }, {
    //   name: 'admin.privilege',
    //   url: '/privilege',
    //   component: 'privilegesView'
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
