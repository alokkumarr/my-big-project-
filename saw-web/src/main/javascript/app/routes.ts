import { Routes } from '@angular/router';
import { IsUserLoggedInGuard, DefaultModuleGuard } from './common/guards';
import { MainPageComponent } from './layout';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './login/components';
import { LoginPageComponent } from './login/page';
import {
  IsUserNotLoggedInGuard
} from './login/guards';

export const routes: Routes = [{
  // name: 'root',
  path: '',
  canActivate: [IsUserLoggedInGuard, DefaultModuleGuard],
  canActivateChild: [IsUserLoggedInGuard],
  // redirectTo: 'analyze',
  component: MainPageComponent,
  pathMatch: 'full'
}, {
  // name: 'login',
  path: 'login',
  component: LoginPageComponent,
  canActivate: [IsUserNotLoggedInGuard],
  canActivateChild: [IsUserNotLoggedInGuard],
  children: [{
    // name: 'resetPassword',
    path: '',
    component: LoginComponent,
    pathMatch: 'full'
  }, {
    // name: 'changePassword',
    path: 'changePwd',
    component: PasswordChangeComponent
  }, {
    // name: 'preResetPassword',
    path: 'preResetPwd',
    component: PasswordPreResetComponent
  }, {
    // name: 'resetPassword',
    path: 'resetPassword',
    component: PasswordResetComponent
  }]
}];
