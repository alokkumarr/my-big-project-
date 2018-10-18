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
import { routes } from './routes';
import { AdminExportViewComponent, AdminExportListComponent } from './export';
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
  AdminImportFileListComponent
} from './import';
import {
  PrivilegeEditDialogComponent,
  PrivilegeEditorComponent,
  PrivilegeRowComponent
} from './privilege';
import { SidenavMenuService } from '../../common/components/sidenav';

import { isAdminGuard, GoToDefaultAdminPageGuard } from './guards';

const COMPONENTS = [
  AdminPageComponent,
  AdminMainViewComponent,
  AdminListViewComponent,
  UserEditDialogComponent,
  RoleEditDialogComponent,
  PrivilegeEditDialogComponent,
  PrivilegeEditorComponent,
  PrivilegeRowComponent,
  AdminExportViewComponent,
  AdminExportListComponent,
  CategoryEditDialogComponent,
  CategoryDeleteDialogComponent,
  AdminImportViewComponent,
  AdminImportListComponent,
  AdminImportFileListComponent
];

const GUARDS = [isAdminGuard, GoToDefaultAdminPageGuard];

const SERVICES = [
  SidenavMenuService,
  AdminService,
  UserService,
  RoleService,
  PrivilegeService,
  ExportService,
  ImportService,
  CategoryService
];
@NgModule({
  imports: [CommonModuleTs, RouterModule.forChild(routes)],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [...SERVICES, ...GUARDS],
  exports: [AdminPageComponent]
})
export class AdminModule {}
