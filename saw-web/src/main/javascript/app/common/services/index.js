import * as angular from 'angular';
import 'angular-toastr';
import 'angular-toastr/dist/angular-toastr.css';

import {MenuService} from './menu.service';
import {LocalSearchService} from './local-search.service';
import {HeaderProgressService} from './header-progress.service';
import {errorDetailService} from './error-detail.service';
import {dxDataGridService} from './dxDataGrid.service';
import {fileService} from './file.service';
import {toastMessageService} from './toastMessage.service';

export const CommonServiceModule = 'CommonModule.Service';

const moduleDependencies = ['toastr'];

angular.module(CommonServiceModule, moduleDependencies)
  .factory('dxDataGridService', dxDataGridService)
  .factory('LocalSearchService', LocalSearchService)
  .factory('ErrorDetail', errorDetailService)
  .factory('fileService', fileService)
  .factory('toastMessage', toastMessageService)
  .service('HeaderProgress', HeaderProgressService)
  .service('MenuService', MenuService);
