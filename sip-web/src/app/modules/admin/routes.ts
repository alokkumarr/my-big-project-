import { AdminPageComponent } from './page';
import { Routes } from '@angular/router';
import { AdminMainViewComponent } from './main-view';
import { AdminExportViewComponent } from './export';
import { AdminImportViewComponent } from './import';
import { SecurityGroupComponent } from './datasecurity/security-group/security-group.component';
import { AdminBrandingComponent } from './branding/branding.component';
import { IsAdminGuard } from './guards';
import {
  UsersTableHeader,
  RolesTableHeader,
  PrivilegesTableHeader,
  CategoriesTableHeader,
  UserAssignmentsTableHeader
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
        // name: 'admin.import',
        path: 'securitygroups',
        component: SecurityGroupComponent
      },
      {
        path: 'userassignments',
        component: AdminMainViewComponent,
        data: {
          columns: UserAssignmentsTableHeader,
          section: 'user assignments'
        }
      },
      {
        path: 'branding',
        component: AdminBrandingComponent,
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'user'
      }
    ]
  }
];
