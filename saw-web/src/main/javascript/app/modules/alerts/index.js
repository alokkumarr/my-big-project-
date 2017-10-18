import * as angular from 'angular';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

export const AlertsModule = 'AlertsModule';
import {CommonModule} from '../../common';

angular.module(AlertsModule, [
  CommonModule
])
  .config(i18nConfig)
  .config(routesConfig);
