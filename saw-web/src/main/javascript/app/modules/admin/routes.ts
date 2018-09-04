import {AdminMainViewComponent} from './main-view';
import {AdminExportViewComponent} from './export';
import {AdminImportViewComponent} from './import';
import {
  UsersTableHeader,
  RolesTableHeader,
  PrivilegesTableHeader,
  CategoriesTableHeader
} from './consts';

export const routes = [{
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
}, {
  name: 'admin.export',
  url: '/export',
  component: AdminExportViewComponent
}, {
  name: 'admin.import',
  url: '/import',
  component: AdminImportViewComponent
}];
