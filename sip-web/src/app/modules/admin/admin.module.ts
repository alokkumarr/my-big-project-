import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgxsModule } from '@ngxs/store';
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
import { DataSecurityService } from './datasecurity/datasecurity.service';
import { AdminBrandingComponent } from './branding/branding.component';
import { BrandingService } from './branding/branding.service';
import { routes } from './routes';
import { FormsModule } from '@angular/forms';
import { AnalyzeService } from '../analyze/services/analyze.service';
import { ColorPickerModule } from 'ngx-color-picker';

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
import { SidenavMenuService } from '../../common/components/sidenav';
import {
  DxDataGridService,
  ToastService,
  LocalSearchService
} from '../../common/services';
import { IsAdminGuard, GoToDefaultAdminPageGuard } from './guards';
import { DskFilterGroupComponent } from './datasecurity/dsk-filter-group/dsk-filter-group.component';
import { DskFilterDialogComponent } from './datasecurity/dsk-filter-dialog/dsk-filter-dialog.component';
import { DskFilterGroupViewComponent } from './datasecurity/dsk-filter-group-view/dsk-filter-group-view.component';

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
  DskFilterDialogComponent,
  DskFilterGroupComponent,
  DskFilterGroupViewComponent,
  AdminBrandingComponent,
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
  DataSecurityService,
  BrandingService,
  CategoryService,
  AnalyzeService
];
@NgModule({
  imports: [
    RouterModule.forChild(routes),
    CommonModuleTs,
    FormsModule,
    TreeModule,
    ColorPickerModule,
    NgxsModule.forFeature([AdminState, ExportPageState, AdminImportPageState])
  ],
  declarations: COMPONENTS,
  entryComponents: COMPONENTS,
  providers: [...SERVICES, ...GUARDS],
  exports: [AdminPageComponent],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class AdminModule {}
