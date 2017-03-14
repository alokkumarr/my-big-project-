import angular from 'angular';

import {routesConfig} from './routes';
import {i18nConfig} from './i18n';

export const AlertsModule = 'AlertsModule';

angular.module(AlertsModule, [])
  .config(i18nConfig)
  .config(routesConfig);
