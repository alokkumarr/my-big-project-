import { Routes } from '@angular/router';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';
import { IsUserNotLoggedInGuard } from './guards';
import { LoginPageComponent } from './page';

export const routes: Routes = [
  {
    path: '',
    component: LoginPageComponent,
    canActivate: [IsUserNotLoggedInGuard],
    canActivateChild: [IsUserNotLoggedInGuard],
    children: [
      {
        path: '',
        component: LoginComponent,
        pathMatch: 'full'
      },
      {
        // name: 'changePassword',
        path: 'changePwd',
        component: PasswordChangeComponent
      },
      {
        // name: 'preResetPassword',
        path: 'preResetPwd',
        component: PasswordPreResetComponent
      },
      {
        // name: 'resetPassword',
        path: 'resetPassword',
        component: PasswordResetComponent
      }
    ]
  }
];
