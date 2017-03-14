import angular from 'angular';
import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';
import {dxDataGridService} from './services/dxDataGrid.service';

export const CommonModule = 'Common';

const modulesDependencies = [];

if (__MOCK__) {
  const {MockModule} = require('./mock');

  modulesDependencies.push(MockModule);
}

angular
  .module(CommonModule, modulesDependencies)
  .factory('dxDataGridService', dxDataGridService)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  });
