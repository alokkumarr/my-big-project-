import angular from 'angular';
import EventHandler from './component/eventHandler';
import ComponentHandler from './component/componentHandler';
import recursionHelper from './recursion-helper.service';

export const CommonModule = 'Common';

const modulesDependencies = [];

// I commented this because we need the mocks for the demo
// and the application will be deployed
// if (__DEVELOPMENT__) {
const {MockModule} = require('./mock');

modulesDependencies.push(MockModule);
//

angular
  .module(CommonModule, modulesDependencies)
  .factory('$eventHandler', EventHandler)
  .factory('recursionHelper', recursionHelper)
  .factory('$componentHandler', ComponentHandler);
