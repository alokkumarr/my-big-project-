import angular from 'angular';

import {routesConfig} from './routes';
import {runConfig} from './run';

import {AuthServiceFactory} from './services/auth.service';
import {UserServiceFactory} from './services/user.service';
import {JwtServiceFactory} from './services/jwt.service';

import {LoginComponent} from './components/login/login.component';
import {PasswordChangeComponent} from './components/passwordChange/passwordChange.component';

export const LoginModule = 'LoginModule';

angular
  .module(LoginModule, [])
  .config(routesConfig)
  .run(runConfig)
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory)
  .component('passwordChangeComponent', PasswordChangeComponent)
  .component('loginComponent', LoginComponent);
