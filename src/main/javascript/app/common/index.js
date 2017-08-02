import angular from 'angular';
import 'angular-ui-router';

import 'angular-material';

import 'angular-local-storage';
import 'angular-sanitize';
import 'angular-translate';
import 'angular-translate/dist/angular-translate-loader-partial/angular-translate-loader-partial';
import 'angular-translate/dist/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat';

import 'ng-idle';

import 'mottle';

import 'devextreme/ui/data_grid';
import 'devextreme/integration/angular';

import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';

import {CommonServiceModule} from './services';
import {CommonComponentModule} from './components';
import {CommonFilterModule} from './filters';
import {CommonDirectiveModule} from './directives';
// import from login module
import {AuthServiceFactory} from '../../login/services/auth.service';
import {UserServiceFactory} from '../../login/services/user.service';
import {JwtServiceFactory} from '../../login/services/jwt.service';

import AppConfig from '../../../../../appConfig';

export const CommonModule = 'CommonModule';

const moduleDependencies = [
  'ui.router',
  'LocalStorageModule',
  'ngSanitize',
  'ngMaterial',
  'ngIdle',
  'dx',
  'pascalprecht.translate',
  CommonServiceModule,
  CommonComponentModule,
  CommonFilterModule,
  CommonDirectiveModule
];

// if (__MOCK__) {
//   const {MockModule} = require('./mock');

//   moduleDependencies.push(MockModule);
// }

angular
  .module(CommonModule, moduleDependencies)
  .value('AppConfig', AppConfig)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  })
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory);
