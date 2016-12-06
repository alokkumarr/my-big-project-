import angular from 'angular';
import ComponentHandler from './component/componentHandler';

export const CommonModule = 'Common';

const modulesDependencies = [];

if (__DEVELOPMENT__) {
  const {MockModule} = require('./mock');

  modulesDependencies.push(MockModule);
}

angular
  .module(CommonModule, modulesDependencies)
  .factory('$componentHandler', ComponentHandler);
