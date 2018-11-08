import { AdminPageComponent } from './page';
import { Routes } from '@angular/router';
import { AdminMainViewComponent } from './main-view';
import { AdminExportViewComponent } from './export';
import { AdminImportViewComponent } from './import';
import { IsAdminGuard } from './guards';
import {
  UsersTableHeader,
  RolesTableHeader,
  PrivilegesTableHeader,
  CategoriesTableHeader
} from './consts';

export const routes: Routes = [
  {
    // name: 'admin',
    path: '',
    component: AdminPageComponent,
    canActivate: [IsAdminGuard],
    canActivateChild: [IsAdminGuard],
    runGuardsAndResolvers: 'paramsOrQueryParamsChange',
    children: [
      {
        // name: 'admin.user',
        path: 'user',
        component: AdminMainViewComponent,
        data: {
          columns: UsersTableHeader,
          section: 'user'
        }
      },
      {
        // name: 'admin.role',
        path: 'role',
        component: AdminMainViewComponent,
        data: {
          columns: RolesTableHeader,
          section: 'role'
        }
      },
      {
        // name: 'admin.categories',
        path: 'categories',
        component: AdminMainViewComponent,
        data: {
          columns: CategoriesTableHeader,
          section: 'category'
        }
      },
      {
        // name: 'admin.privilege',
        path: 'privilege',
        component: AdminMainViewComponent,
        data: {
          columns: PrivilegesTableHeader,
          section: 'privilege'
        }
      },
      {
        // name: 'admin.export',
        path: 'export',
        component: AdminExportViewComponent
      },
      {
        // name: 'admin.import',
        path: 'import',
        component: AdminImportViewComponent
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'user'
      }
    ]
  }
];
