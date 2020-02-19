import { Routes } from '@angular/router';

import { IsUserLoggedInGuard } from '../../common/guards';
import { IsAdminGuard } from '../admin/guards';
import { AlertRedirectGuard, AlertPrivilegeGuard } from './guards';

import { AlertsPageComponent } from './components/alerts-page/alerts-page.component';
import { AlertsViewComponent } from './components/alerts-view/alerts-view.component';
import {
  AlertsConfigurationComponent,
  AddAlertComponent
} from './components/configure/index';
import { AlertUnsubscribe } from './components/alert-unsubscribe/alert-unsubscribe.component';

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
        component: AlertsViewComponent,
        canActivate: [AlertPrivilegeGuard]
      },
      {
        path: 'configure',
        component: AlertsConfigurationComponent,
        canActivate: [AlertPrivilegeGuard]
      },
      {
        path: 'add',
        component: AddAlertComponent,
        canActivate: [IsAdminGuard]
      },
      {
        path: '',
        pathMatch: 'full',
        canActivate: [AlertRedirectGuard]
      }
    ]
  }, {
    path: '',
    component: AlertsPageComponent,
    children: [
      {
        path: 'unsubscribe',
        component: AlertUnsubscribe
      }
    ]
  }
];
