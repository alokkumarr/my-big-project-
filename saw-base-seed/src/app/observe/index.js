import angular from 'angular';

import {ObserveService} from './common/observe.service';
import {ObservePageComponent} from './observe-page/observe-page.component';

export const ObserveModule = 'ObserveModule';

angular.module(ObserveModule, [])
  .component('observePage', ObservePageComponent)
  .factory('ObserveService', ObserveService);
