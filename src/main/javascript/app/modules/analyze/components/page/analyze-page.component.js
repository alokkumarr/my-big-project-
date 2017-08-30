import fpFirst from 'lodash/fp/first';
import fpGet from 'lodash/fp/get';
import fpPipe from 'lodash/fp/pipe';

import template from './analyze-page.component.html';
import style from './analyze-page.component.scss';
import {LAST_ANALYSES_CATEGORY_ID} from '../../consts';

export const AnalyzePageComponent = {
  template,
  styles: [style],
  controller: class AnalyzePageController {
    constructor($componentHandler, MenuService, $state, localStorageService, AnalyzeService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.MenuService = MenuService;
      this._$state = $state;
      this._AnalyzeService = AnalyzeService;
      this._localStorageService = localStorageService;
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.MenuService.getMenu('ANALYZE')
        .then(data => {
          leftSideNav.update(data, 'ANALYZE');
          leftSideNav.openSidenavManually();
          this._AnalyzeService.updateMenu(data);
          this.goToDefaultChildStateIfNeeded(data);
        });
    }

    goToDefaultChildStateIfNeeded(menu) {
      // only redirect if we go to the parent analyze state
      if (this._$state.current.name !== 'analyze') {
        return;
      }
      const id = this._localStorageService.get(LAST_ANALYSES_CATEGORY_ID);
      if (id) {
        this._$state.go('analyze.view', {id});
      } else {
        const defaultAnalysesCategoryId = fpPipe(
          fpFirst,
          fpGet('children'),
          fpFirst,
          fpGet('id')
        )(menu);
        this._$state.go('analyze.view', {id: defaultAnalysesCategoryId});
      }
    }
  }
};
