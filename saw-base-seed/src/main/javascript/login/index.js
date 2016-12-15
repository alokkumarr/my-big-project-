import angular from 'angular';
import 'angular-material';
import 'angular-ui-router';
import 'angular-material/angular-material.css';
import '../../../../fonts/style.css';

import {themeConfig} from '../app/theme';
import {loginComponent} from '../login/login.component';
import {ChangeComponent} from './change.component';

import {UserService} from '../login/user.service';
import {JwtService} from '../login/jwt.service';

import {routesConfig} from './routes';

export const loginModule = 'login';

angular
  .module(loginModule, [
    'ngMaterial',
    'ui.router'
  ])
  .config(themeConfig)
  .config(routesConfig)
  .factory('UserService', UserService)
  .factory('JwtService', JwtService)
  .component('changeComponent', ChangeComponent)
  .component('loginComponent', loginComponent);
