import { Routes } from '@angular/router';

import { IsUserLoggedInGuard } from '../../common/guards';
import { IsAdminGuard } from '../admin/guards';

import { AlertsPageComponent } from './components/alerts-page/alerts-page.component';
import { AlertsViewComponent } from './components/alerts-view/alerts-view.component';
import { AlertsConfigurationComponent } from './components/configure/alerts-configuration/alerts-configuration.component';

export const routes: Routes = [
  {
    path: '',
    canActivate: [IsUserLoggedInGuard],
    canActivateChild: [IsUserLoggedInGuard],
    component: AlertsPageComponent,
    runGuardsAndResolvers: 'paramsOrQueryParamsChange',
    children: [
      {
        path: 'alerts/view',
        component: AlertsViewComponent
      },
      {
        path: 'alerts/configure',
        component: AlertsConfigurationComponent,
        canActivate: [IsAdminGuard]
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'alerts/configure'
      }
    ]
  }
];
