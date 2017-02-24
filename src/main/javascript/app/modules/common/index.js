import angular from 'angular';

import {MenuService} from './services/menu.service';

export const CommonModule = 'CommonModule';

angular.module(CommonModule, [])
  .factory('MenuService', MenuService);
