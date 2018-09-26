import { Routes } from '@angular/router';
import {
  LoginComponent,
  PasswordChangeComponent,
  PasswordPreResetComponent,
  PasswordResetComponent
} from './components';

export const routes: Routes = [{
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
}];
