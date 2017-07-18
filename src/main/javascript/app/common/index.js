import angular from 'angular';

import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';

import {CommonServiceModule} from './services';
import {CommonComponentModule} from './components';
import {CommonFilterModule} from './filters';
import {CommonDirectiveModule} from './directives';

export const CommonModule = 'CommonModule';

const moduleDependencies = [
  CommonServiceModule,
  CommonComponentModule,
  CommonFilterModule,
  CommonDirectiveModule
];

if (__MOCK__) {
  const {MockModule} = require('./mock');

  moduleDependencies.push(MockModule);
}

angular
  .module(CommonModule, moduleDependencies)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  });
