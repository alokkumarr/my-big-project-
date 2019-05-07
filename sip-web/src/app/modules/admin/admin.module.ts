import { NgModule } from '@angular/core';
import { NgxsModule } from '@ngxs/store';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TreeModule } from 'angular-tree-component';
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

import { AdminState } from './state/admin.state';

import {
  AdminExportViewComponent,
  AdminExportListComponent,
  AdminExportTreeComponent,
  AdminExportContentComponent,
  ExportPageState
} from './export';
import { CategoryService } from './category/category.service';
import { UserEditDialogComponent, UserService } from './user';
import {
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent
} from './category';
import { RoleEditDialogComponent } from './role';
import {
  AdminImportViewComponent,
  AdminImportListComponent,
  AdminImportFileListComponent,
  AdminImportCategorySelectComponent,
  AdminImportPageState
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
import { JwtService } from '../../common/services';
import {
  AddTokenInterceptor,
  HandleErrorInterceptor,
  RefreshTokenInterceptor
} from '../../common/interceptor';
import { SidenavMenuService } from '../../common/components/sidenav';
import {
  DxDataGridService,
  ToastService,
  LocalSearchService
} from '../../common/services';
import { IsAdminGuard, GoToDefaultAdminPageGuard } from './guards';

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
  AdminExportTreeComponent,
  AdminExportContentComponent,
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent,
  AdminImportViewComponent,
  AdminImportListComponent,
  AdminImportFileListComponent,
  AdminImportCategorySelectComponent
];

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
const GUARDS = [IsAdminGuard, GoToDefaultAdminPageGuard];

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
    NgxsModule.forFeature([AdminState, ExportPageState, AdminImportPageState]),
    CommonModuleTs,
    FormsModule,
    RouterModule.forChild(routes),
    TreeModule
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [...INTERCEPTORS, ...SERVICES, ...GUARDS],
  exports: [AdminPageComponent]
})
export class AdminModule {}
