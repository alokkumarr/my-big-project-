import { Routes }  from '@angular/router';
import {AdminMainViewComponent} from './main-view';
import {AdminExportViewComponent} from './export';
import {AdminImportViewComponent} from './import';
import {
  UsersTableHeader,
  RolesTableHeader,
  PrivilegesTableHeader,
  CategoriesTableHeader
} from './consts';

export const routes: Routes = [{
  // name: 'admin',
  path: 'admin',
  redirectTo: 'admin.user'
}, {
  // name: 'admin.user',
  path: 'admin/user',
  component: AdminMainViewComponent,
  resolve: [{
    token: 'columns',
    resolveFn: () => UsersTableHeader
  }, {
    token: 'section',
    resolveFn: () => 'user'
  }]
}, {
  // name: 'admin.role',
  path: 'admin/role',
  component: AdminMainViewComponent,
  resolve: [{
    token: 'columns',
    resolveFn: () => RolesTableHeader
  }, {
    token: 'section',
    resolveFn: () => 'role'
  }]
}, {
  // name: 'admin.categories',
  path: 'admin/categories',
  component: AdminMainViewComponent,
  resolve: [{
    token: 'columns',
    resolveFn: () => CategoriesTableHeader
  }, {
    token: 'section',
    resolveFn: () => 'category'
  }]
}, {
  // name: 'admin.privilege',
  path: 'admin/privilege?role',
  component: AdminMainViewComponent,
  resolve: [{
    token: 'columns',
    resolveFn: () => PrivilegesTableHeader
  }, {
    token: 'section',
    resolveFn: () => 'privilege'
  }]
}, {
  // name: 'admin.export',
  path: 'admin/export',
  component: AdminExportViewComponent
}, {
  // name: 'admin.import',
  path: 'admin/import',
  component: AdminImportViewComponent
}];
