import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import {
  IsUserLoggedInGuard,
  DefaultModuleGuard,
  SSOAuthGuard
} from './common/guards';
import { SSOAuthComponent } from './common/components/sso-auth/sso-auth.component';
import { MainPageComponent } from './layout';

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
    loadChildren: './modules/alerts/alerts.module#AlertsModule'
  },
  {
    path: 'workbench',
    loadChildren: './modules/workbench/workbench.module#WorkbenchModule'
  },
  {
    path: 'admin',
    loadChildren: './modules/admin/admin.module#AdminModule'
  },
  {
    path: 'observe',
    loadChildren: './modules/observe/observe.module#ObserveUpgradeModule'
  },
  {
    path: 'analyze',
    loadChildren: './modules/analyze/analyze.module#AnalyzeModule'
  },
  {
    path: 'login',
    loadChildren: './login/login.module#LoginModule'
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
      preloadingStrategy: PreloadAllModules
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
