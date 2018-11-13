
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { CommonModuleTs } from '../../common';
import { AdminListViewComponent } from './list-view';
import { AdminMainViewComponent } from './main-view';
import { AdminPageComponent } from './page';
import { AdminService } from './main-view/admin.service';
import { RoleService } from './role/role.service';
import { PrivilegeService } from './privilege/privilege.service';
import { ExportService } from './export/export.service';
import { ImportService } from './import/import.service';
import { UserAssignmentService } from './datasecurity/userassignment.service';
import { routes } from './routes';
import { FormsModule } from '@angular/forms';

import {
  AdminExportViewComponent,
  AdminExportListComponent
} from './export';
import { CategoryService } from './category/category.service';
import {
  UserEditDialogComponent,
  UserService
} from './user';
import {
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent
} from './category';
import {
  RoleEditDialogComponent
} from './role';
import {
  AdminImportViewComponent,
  AdminImportListComponent,
  AdminImportFileListComponent
} from './import';
import {
  PrivilegeEditDialogComponent,
  PrivilegeEditorComponent,
  PrivilegeRowComponent
} from './privilege';
import {
  SecurityGroupComponent,
  AddSecurityDialogComponent,
  AddAttributeDialogComponent,
  FieldAttributeViewComponent,
  DeleteDialogComponent
} from './datasecurity';
import {JwtService} from '../../common/services';
import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from '../../common/interceptor';
import { SidenavMenuService } from '../../common/components/sidenav';
import { DxDataGridService, ToastService, LocalSearchService } from '../../common/services';

<<<<<<< HEAD
import { isAdminGuard, GoToDefaultAdminPageGuard} from './guards';
=======
import { IsAdminGuard, GoToDefaultAdminPageGuard } from './guards';
>>>>>>> 78c559796236f7d401bae56e29b5744980ab3736

const COMPONENTS = [
  AdminPageComponent,
  AdminMainViewComponent,
  AdminListViewComponent,
  UserEditDialogComponent,
  RoleEditDialogComponent,
  PrivilegeEditDialogComponent,
  PrivilegeEditorComponent,
  PrivilegeRowComponent,
  SecurityGroupComponent,
  AddSecurityDialogComponent,
  DeleteDialogComponent,
  AddAttributeDialogComponent,
  FieldAttributeViewComponent,
  AdminExportViewComponent,
  AdminExportListComponent,
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent,
  AdminImportViewComponent,
  AdminImportListComponent,
  AdminImportFileListComponent
];

<<<<<<< HEAD
const INTERCEPTORS = [
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
  }
];

const GUARDS = [isAdminGuard, GoToDefaultAdminPageGuard];
=======
const GUARDS = [IsAdminGuard, GoToDefaultAdminPageGuard];
>>>>>>> 78c559796236f7d401bae56e29b5744980ab3736

const SERVICES = [
  SidenavMenuService,
  AdminService,
  UserService,
  JwtService,
  DxDataGridService,
  LocalSearchService,
  ToastService,
  RoleService,
  PrivilegeService,
  ExportService,
  ImportService,
  UserAssignmentService,
  CategoryService
];
@NgModule({
  imports: [
    CommonModuleTs,
    FormsModule,
    RouterModule.forChild(routes)
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [
    ...INTERCEPTORS,
    ...SERVICES,
    ...GUARDS
  ],
  exports: [
    AdminPageComponent
  ]
})
export class AdminModule {}
