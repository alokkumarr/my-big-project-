import { Routes } from '@angular/router';

import { IsUserLoggedInGuard } from '../../common/guards';
import { IsAdminGuard } from '../admin/guards';

import { AlertsPageComponent } from './components/alerts-page/alerts-page.component';
import { AlertsViewComponent } from './components/alerts-view/alerts-view.component';
import {
  AlertsConfigurationComponent,
  AddAlertComponent
} from './components/configure/index';

export const routes: Routes = [
  {
    path: '',
    canActivate: [IsUserLoggedInGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: AlertsPageComponent,
    runGuardsAndResolvers: 'paramsOrQueryParamsChange',
    children: [
      {
        path: 'view',
        component: AlertsViewComponent
      },
      {
        path: 'configure',
        component: AlertsConfigurationComponent,
        canActivate: [IsAdminGuard]
      },
      {
        path: 'add',
        component: AddAlertComponent,
        canActivate: [IsAdminGuard]
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'configure'
      }
    ]
  }
];
