import angular from 'angular';

import {routesConfig} from './routes';

export const AlertsModule = 'AlertsModule';

angular.module(AlertsModule, [])
  .config(routesConfig);
