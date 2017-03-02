import template from './analyze-page.component.html';
import style from './analyze-page.component.scss';

export const AnalyzePageComponent = {
  template,
  styles: [style],
  controller: class AnalyzePageController {
    constructor($componentHandler, MenuService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.MenuService = MenuService;
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.MenuService.getMenu('ANALYZE')
        .then(data => {
          leftSideNav.update(data);
        });
    }
  }
};
