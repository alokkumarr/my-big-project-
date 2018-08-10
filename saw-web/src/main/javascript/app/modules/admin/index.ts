
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModuleTs } from '../../common';
import { AdminListViewComponent } from './list-view';
import { AdminMainViewComponent } from './main-view';
import { AdminService } from './main-view/admin.service';
import { RoleService } from './role/role.service';
import { PrivilegeService } from './privilege/privilege.service';
import {
  UserEditDialogComponent,
  UserService
} from './user';
import {
  RoleEditDialogComponent
} from './role';
import {JwtService} from '../../../login/services/jwt.service';
import {dxDataGridService} from '../../common/services/dxDataGrid.service';
import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from '../../common/interceptor';
import { SidenavMenuService } from '../../common/components/sidenav';
import { ToastService } from '../../common/services/toastMessage.service';
import { LocalSearchService } from '../../common/services/local-search.service';

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
import {AnalysisExportComponent} from './components/export/export.component';
import {AnalysisImportComponent} from './components/import/import.component';
import {ExportListViewComponent} from './components/export/export-list/export-list.component';
import {ImportListViewComponent} from './components/import/import-list/import-list.component';
import {ImportFileListViewComponent} from './components/import/import-file-list/import-file-list.component';

import {ExportService} from './services/export.service';
import {ImportService} from './services/import.service';
import {UsersManagementService} from './services/users.service';
import {RolesManagementService} from './services/roles.service';
import {PrivilegesManagementService} from './services/privileges.service';
import {CategoriesManagementService} from './services/categories.service';
import {CommonModule} from '../../common';

export const OldAdminModule = 'AdminModule';

angular.module(OldAdminModule, [
  CommonModule
])
  .config(i18nConfig)
  .config(routesConfig)
  .factory('UsersManagementService', UsersManagementService)
  .factory('RolesManagementService', RolesManagementService)
  .factory('PrivilegesManagementService', PrivilegesManagementService)
  .factory('CategoriesManagementService', CategoriesManagementService)
  .service('ExportService', ExportService)
  .service('ImportService', ImportService)
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
  .component('categoryDelete', CategoryDeleteComponent)
  .component('exportComponent', AnalysisExportComponent)
  .component('importComponent', AnalysisImportComponent)
  .component('exportListView', ExportListViewComponent)
  .component('importListView', ImportListViewComponent)
  .component('importFileListView', ImportFileListViewComponent);


const COMPONENTS = [
  AdminMainViewComponent,
  AdminListViewComponent,
  UserEditDialogComponent,
  RoleEditDialogComponent
];
@NgModule({
  imports: [
    CommonModuleTs
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AddTokenInterceptor, multi: true },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HandleErrorInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RefreshTokenInterceptor,
      multi: true
    },
    SidenavMenuService,
    AdminService,
    UserService,
    JwtService,
    dxDataGridService,
    LocalSearchService,
    ToastService,
    RoleService,
    PrivilegeService
  ],
  exports: [
    AdminMainViewComponent
  ]
})
export class AdminModule {}
