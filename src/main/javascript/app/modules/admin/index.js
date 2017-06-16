import angular from 'angular';

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

import {UsersManagementService} from './services/users.service';
import {RolesManagementService} from './services/roles.service';

export const AdminModule = 'AdminModule';

angular.module(AdminModule, [])
  .config(i18nConfig)
  .config(routesConfig)
  .factory('UsersManagementService', UsersManagementService)
  .factory('RolesManagementService', RolesManagementService)
  .component('usersView', UsersViewComponent)
  .component('usersListView', UsersListViewComponent)
  .component('userDialog', UserDialogComponent)
  .component('userNew', UserNewComponent)
  .component('userEdit', UserEditComponent)
  .component('rolesView', RolesViewComponent)
  .component('rolesListView', RolesListViewComponent)
  .component('roleDialog', RoleDialogComponent)
  .component('roleNew', RoleNewComponent)
  .component('roleEdit', RoleEditComponent);
