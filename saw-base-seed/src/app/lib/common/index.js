import angular from 'angular';
import {MockModule} from './mock';

export const CommonModule = 'Common';

angular
  .module(CommonModule, [MockModule]);
