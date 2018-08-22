import { AdminMainViewComponent } from './main-view';
import { AdminExportViewComponent } from './export';
import {
  UsersTableHeader,
  RolesTableHeader
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
      name: 'admin.privilege',
      url: '/privilege?role',
      component: 'privilegesView'
    }, {
      name: 'admin.categories',
      url: '/categories',
      component: 'categoriesView'
    }, {
      name: 'admin.export',
      url: '/export',
      // component: 'exportComponent'
      component: AdminExportViewComponent
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
