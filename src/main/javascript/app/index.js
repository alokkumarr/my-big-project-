import angular from 'angular';
import 'angular-ui-router';

import 'angular-material';
import 'angular-material/angular-material.css';

import 'angular-local-storage';
import 'angular-sanitize';
import 'angular-translate';
import 'angular-translate/dist/angular-translate-loader-partial/angular-translate-loader-partial';
import 'angular-translate/dist/angular-translate-interpolation-messageformat/angular-translate-interpolation-messageformat';

import 'ng-idle';

import 'mottle';

import 'devextreme/ui/data_grid';
import 'devextreme/integration/angular';
import 'devextreme/dist/css/dx.common.css';
import 'devextreme/dist/css/dx.light.css';

import 'fonts/icomoon.css';

import AppConfig from '../../../../appConfig';

import {routesConfig} from './routes';
import {themeConfig} from './theme';
import {i18nConfig} from './i18n';
import {config} from './config';
import {interceptor} from './http-interceptor';
import {runConfig} from './run';

import {LibModule} from './lib';
import {CommonModule} from './modules/common';
import {ObserveModule} from './modules/observe';
import {AnalyzeModule} from './modules/analyze';
import {AlertsModule} from './modules/alerts';
import {AdminModule} from './modules/admin';

import {LayoutHeaderComponent, LayoutContentComponent, LayoutFooterComponent} from './layout';

// import from login module
import {AuthServiceFactory} from '../login/services/auth.service';
import {UserServiceFactory} from '../login/services/user.service';
import {JwtServiceFactory} from '../login/services/jwt.service';

export const AppModule = 'app';

angular
  .module(AppModule, [
    'ui.router',
    'LocalStorageModule',
    'ngSanitize',
    'ngMaterial',
    'ngIdle',
    'dx',
    'pascalprecht.translate',
    LibModule,
    CommonModule,
    ObserveModule,
    AnalyzeModule,
    AlertsModule,
    AdminModule
  ])
  .config(routesConfig)
  .config(themeConfig)
  .config(i18nConfig)
  .config(config)
  .config(interceptor)
  .run(runConfig)
  .value('AppConfig', AppConfig)
  .factory('AuthService', AuthServiceFactory)
  .factory('UserService', UserServiceFactory)
  .factory('JwtService', JwtServiceFactory)
  .component('layoutHeader', LayoutHeaderComponent)
  .component('layoutContent', LayoutContentComponent)
  .component('layoutFooter', LayoutFooterComponent);
