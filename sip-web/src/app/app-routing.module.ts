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
    loadChildren: './modules/alerts/alerts.module#AlertsModule',
    data: { preload: true }
  },
  {
    path: 'workbench',
    loadChildren: './modules/workbench/workbench.module#WorkbenchModule',
    data: { preload: true }
  },
  {
    path: 'admin',
    loadChildren: './modules/admin/admin.module#AdminModule',
    data: { preload: true }
  },
  {
    path: 'observe',
    loadChildren: './modules/observe/observe.module#ObserveUpgradeModule',
    data: { preload: true }
  },
  {
    path: 'analyze',
    loadChildren: './modules/analyze/analyze.module#AnalyzeModule',
    data: { preload: true }
  },
  {
    path: 'login',
    loadChildren: './login/login.module#LoginModule',
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
