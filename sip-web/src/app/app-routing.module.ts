import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {
  IsUserLoggedInGuard,
  DefaultModuleGuard,
  SSOAuthGuard
} from './common/guards';
import { SSOAuthComponent } from './common/components/sso-auth/sso-auth.component';
import { MainPageComponent } from './layout';
import { SelectivePreloading } from './preloading-strategy.component';

const routes: Routes = [
  {
    // name: 'root',
    path: '',
    canActivate: [IsUserLoggedInGuard, DefaultModuleGuard],
    canActivateChild: [IsUserLoggedInGuard],
    // redirectTo: 'analyze',
    component: MainPageComponent,
    pathMatch: 'full'
  },
  {
    path: 'alerts',
    loadChildren: () => import('./modules/alerts/alerts.module').then(m => m.AlertsModule),
    data: { preload: true }
  },
  {
    path: 'workbench',
    loadChildren: () => import('./modules/workbench/workbench.module').then(m => m.WorkbenchModule),
    data: { preload: true }
  },
  {
    path: 'admin',
    loadChildren: () => import('./modules/admin/admin.module').then(m => m.AdminModule),
    data: { preload: true }
  },
  {
    path: 'observe',
    loadChildren: () => import('./modules/observe/observe.module').then(m => m.ObserveUpgradeModule),
    data: { preload: true }
  },
  {
    path: 'analyze',
    loadChildren: () => import('./modules/analyze/analyze.module').then(m => m.AnalyzeModule),
    data: { preload: true }
  },
  {
    path: 'login',
    loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
    data: { preload: true }
  },

  {
    path: 'authenticate',
    canActivate: [SSOAuthGuard],
    component: SSOAuthComponent
  },

  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      useHash: true,
      onSameUrlNavigation: 'reload',
      enableTracing: false,
      preloadingStrategy: SelectivePreloading
    })
  ],
  providers: [SelectivePreloading],
  exports: [RouterModule]
})
export class AppRoutingModule {}
