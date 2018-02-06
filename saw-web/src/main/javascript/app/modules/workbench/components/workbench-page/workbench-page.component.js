
import * as template from './workbench-page.component.html';
import style from './workbench-page.component.scss';

export const WorkbenchPageComponent = {
  template,
  styles: [style],
  controller: class WorkbenchPageController {
    constructor($componentHandler, MenuService, $state, localStorageService, $rootScope, WorkbenchService, AnalyzeService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.MenuService = MenuService;
      this._$state = $state;
      this._WorkbenchService = WorkbenchService;
      this._localStorageService = localStorageService;
      this._$rootScope = $rootScope;
      this._AnalyzeService = AnalyzeService;
    }

    $onInit() {
      this._$rootScope.showProgress = true;

      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];
      this.MenuService.getMenu('ANALYZE')
        .then(data => {
          leftSideNav.update(data, 'ANALYZE');
          // leftSideNav.openSidenavManually();
          this._AnalyzeService.updateMenu(data);
          this._AnalyzeService.updateMenu(data);
          this._$state.go('workbench.datasets');
        });
    }
  }
};
