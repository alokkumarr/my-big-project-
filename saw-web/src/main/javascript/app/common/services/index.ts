import * as angular from 'angular';
import { downgradeInjectable } from '@angular/upgrade/static';
import 'angular-toastr';
import 'angular-toastr/dist/angular-toastr.css';

import {MenuService} from './menu.service';
import {LocalSearchService} from './local-search.service';
import {HeaderProgressService} from './header-progress.service';
import {dxDataGridService} from './dxDataGrid.service';
import {fileService} from './file.service';
import {ToastService} from './toastMessage.service';
import {SideNavService} from './sidenav.service';
import {ErrorDetailDialogService} from './error-detail-dialog.service';
import {ErrorDetailService} from './error-detail.service';

export const CommonServiceModule = 'CommonModule.Service';

const moduleDependencies = ['toastr'];

angular.module(CommonServiceModule, moduleDependencies)
  .factory('fileService', fileService)
  .factory('ErrorDetailDialogService', downgradeInjectable(ErrorDetailDialogService) as Function)
  .factory('ErrorDetailService', downgradeInjectable(ErrorDetailService) as Function)
  .service('dxDataGridService', dxDataGridService)
  .service('LocalSearchService', LocalSearchService)
  .service('toastMessage', ToastService)
  .service('HeaderProgress', HeaderProgressService)
  .service('SidenavService', SideNavService)
  .service('MenuService', MenuService);
