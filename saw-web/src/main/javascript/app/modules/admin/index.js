import * as angular from 'angular';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

import {UsersViewComponent} from './components/users-view/users-view.component';
import {UsersListViewComponent} from './components/users-view/list/users-list-view.component';
import {UserNewComponent} from './components/user-new/user-new.component';
import {UserDialogComponent} from './components/user-dialog/user-dialog.component';
import {UserEditComponent} from './components/user-edit/user-edit.component';
import {RolesViewComponent} from './components/roles-view/roles-view.component';
import {RolesListViewComponent} from './components/roles-view/list/roles-list-view.component';
import {RoleNewComponent} from './components/role-new/role-new.component';
import {RoleDialogComponent} from './components/role-dialog/role-dialog.component';
import {RoleEditComponent} from './components/role-edit/role-edit.component';
import {PrivilegesViewComponent} from './components/privileges-view/privileges-view.component';
import {PrivilegesListViewComponent} from './components/privileges-view/list/privileges-list-view.component';
import {PrivilegeDialogComponent} from './components/privilege-dialog/privilege-dialog.component';
import {PrivilegeNewComponent} from './components/privilege-new/privilege-new.component';
import {PrivilegeEditComponent} from './components/privilege-edit/privilege-edit.component';
import {CategoriesViewComponent} from './components/categories-view/categories-view.component';
import {CategoriesListViewComponent} from './components/categories-view/list/categories-list-view.component';
import {CategoryDialogComponent} from './components/category-dialog/category-dialog.component';
import {CategoryNewComponent} from './components/category-new/category-new.component';
import {CategoryEditComponent} from './components/category-edit/category-edit.component';
import {CategoryDeleteComponent} from './components/category-delete/category-delete.component';

import {UsersManagementService} from './services/users.service';
import {RolesManagementService} from './services/roles.service';
import {PrivilegesManagementService} from './services/privileges.service';
import {CategoriesManagementService} from './services/categories.service';
import {CommonModule} from '../../common';
export const AdminModule = 'AdminModule';

angular.module(AdminModule, [
  CommonModule
])
  .config(i18nConfig)
  .config(routesConfig)
  .factory('UsersManagementService', UsersManagementService)
  .factory('RolesManagementService', RolesManagementService)
  .factory('PrivilegesManagementService', PrivilegesManagementService)
  .factory('CategoriesManagementService', CategoriesManagementService)
  .component('usersView', UsersViewComponent)
  .component('usersListView', UsersListViewComponent)
  .component('userDialog', UserDialogComponent)
  .component('userNew', UserNewComponent)
  .component('userEdit', UserEditComponent)
  .component('rolesView', RolesViewComponent)
  .component('rolesListView', RolesListViewComponent)
  .component('roleDialog', RoleDialogComponent)
  .component('roleNew', RoleNewComponent)
  .component('roleEdit', RoleEditComponent)
  .component('privilegesView', PrivilegesViewComponent)
  .component('privilegesListView', PrivilegesListViewComponent)
  .component('privilegeDialog', PrivilegeDialogComponent)
  .component('privilegeNew', PrivilegeNewComponent)
  .component('privilegeEdit', PrivilegeEditComponent)
  .component('categoriesView', CategoriesViewComponent)
  .component('categoriesListView', CategoriesListViewComponent)
  .component('categoryDialog', CategoryDialogComponent)
  .component('categoryNew', CategoryNewComponent)
  .component('categoryEdit', CategoryEditComponent)
  .component('categoryDelete', CategoryDeleteComponent);
