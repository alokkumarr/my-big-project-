import angular from 'angular';
import 'angular-toastr';
import 'angular-toastr/dist/angular-toastr.css';
import EventEmitter from './utils/eventEmitter';
import ComponentHandler from './utils/componentHandler';
import {dxDataGridService} from './services/dxDataGrid.service';
import {fileService} from './services/file.service';
import {toastMessageService} from './services/toastMessage.service';

export const CommonModule = 'Common';

const modulesDependencies = ['toastr'];

if (__MOCK__) {
  const {MockModule} = require('./mock');

  modulesDependencies.push(MockModule);
}

angular
  .module(CommonModule, modulesDependencies)
  .factory('dxDataGridService', dxDataGridService)
  .factory('fileService', fileService)
  .factory('toastMessage', toastMessageService)
  .factory('$eventEmitter', () => {
    return new EventEmitter();
  })
  .factory('$componentHandler', () => {
    return new ComponentHandler();
  });
