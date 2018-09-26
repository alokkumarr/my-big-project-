import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { IsUserLoggedInGuard, DefaultModuleGuard } from './common/guards';
import { MainPageComponent } from './layout';
import { LoginModule } from './login';
import { ObserveUpgradeModule } from './modules/observe';
import { AnalyzeModuleTs } from './modules/analyze';
import { AdminModule } from './modules/admin';
import { WorkbenchUpgradeModule } from './modules/workbench';
import { routes as loginRoutes } from './login/routes';
import { routes as AnalyzeRoutes } from './modules/analyze/routes';
import { routes as ObserveRoutes } from './modules/observe/routes';
import { routes as WorkbenchRoutes } from './modules/workbench/routes';
import { routes as AdminRoutes } from './modules/admin/routes';

const routes: Routes = [{
  // name: 'root',
  path: '',
  canActivate: [IsUserLoggedInGuard, DefaultModuleGuard],
  canActivateChild: [IsUserLoggedInGuard],
  // redirectTo: 'analyze',
  component: MainPageComponent,
  pathMatch: 'full',
  children: [
    ...AnalyzeRoutes,
    ...ObserveRoutes,
    ...WorkbenchRoutes,
    ...AdminRoutes
  ]
}, ...loginRoutes];

@NgModule({
  imports: [
    LoginModule,
    ObserveUpgradeModule,
    AnalyzeModuleTs,
    AdminModule,
    WorkbenchUpgradeModule,
    RouterModule.forRoot(routes, {
    useHash: true,
    onSameUrlNavigation: 'reload'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
