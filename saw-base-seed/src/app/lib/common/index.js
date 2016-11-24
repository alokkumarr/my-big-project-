import angular from 'angular';
import {MockModule} from './mock';
import ComponentHandler from './component/componentHandler';

export const CommonModule = 'Common';

angular
  .module(CommonModule, [
    MockModule
  ])
  .factory('$componentHandler', ComponentHandler);

