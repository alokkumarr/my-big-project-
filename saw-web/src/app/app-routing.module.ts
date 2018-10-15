import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IsUserLoggedInGuard, DefaultModuleGuard } from './common/guards';
import { MainPageComponent } from './layout';
import { AdminModule } from './modules/admin';
import { routes as AdminRoutes } from './modules/admin/routes';

const routes: Routes = [
  {
    // name: 'root',
    path: '',
    canActivate: [IsUserLoggedInGuard, DefaultModuleGuard],
    canActivateChild: [IsUserLoggedInGuard],
    // redirectTo: 'analyze',
    component: MainPageComponent,
    pathMatch: 'full',
    children: [...AdminRoutes]
  },
  {
    path: 'workbench',
    loadChildren: './modules/workbench/workbench.module#WorkbenchModule'
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
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [
    AdminModule,
    RouterModule.forRoot(routes, {
      useHash: true,
      onSameUrlNavigation: 'reload'
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
