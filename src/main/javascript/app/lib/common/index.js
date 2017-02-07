import angular from 'angular';
import EventHandler from './component/eventHandler';
import ComponentHandler from './component/componentHandler';

export const CommonModule = 'Common';

const modulesDependencies = [];

if (__MOCK__) {
  const {MockModule} = require('./mock');

  modulesDependencies.push(MockModule);
}

angular
  .module(CommonModule, modulesDependencies)
  .factory('$eventHandler', EventHandler)
  .factory('$componentHandler', ComponentHandler);
