
import { NgModule } from '@angular/core';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { UIRouterModule } from '@uirouter/angular';
import { CommonModuleTs } from '../../common';
import { AdminListViewComponent } from './list-view';
import { AdminMainViewComponent } from './main-view';
import { AdminService } from './main-view/admin.service';
import { RoleService } from './role/role.service';
import { PrivilegeService } from './privilege/privilege.service';
import { ExportService } from './export/export.service';
import { ImportService } from './import/import.service';
import { routes } from './routes';
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

const COMPONENTS = [
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
@NgModule({
  imports: [
    CommonModuleTs,
    UIRouterModule.forChild({states: routes})
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
    PrivilegeService,
    ExportService,
    ImportService,
    CategoryService
  ],
  exports: [
    AdminMainViewComponent
  ]
})
export class AdminModule {}
