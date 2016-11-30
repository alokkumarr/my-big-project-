import angular from 'angular';

import {ObservePageComponent} from './observe-page/observe-page.component';

export const ObserveModule = 'ObserveModule';

angular.module(ObserveModule, [])
  .component('observePage', ObservePageComponent);
