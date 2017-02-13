import angular from 'angular';
import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';

export const CommonModule = 'Common';

const modulesDependencies = [];

if (__MOCK__) {
  const {MockModule} = require('./mock');

  modulesDependencies.push(MockModule);
}

angular
  .module(CommonModule, modulesDependencies)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  });
