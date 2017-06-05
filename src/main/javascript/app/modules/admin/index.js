import angular from 'angular';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

import {UsersViewComponent} from './components/users-view/users-view.component';
import {UsersListViewComponent} from './components/users-view/list/users-list-view.component';

import {UsersManagementService} from './services/users.service';

export const AdminModule = 'AdminModule';

angular.module(AdminModule, [])
  .config(i18nConfig)
  .config(routesConfig)
  .factory('UsersManagementService', UsersManagementService)
  .component('usersView', UsersViewComponent)
  .component('usersListView', UsersListViewComponent);
