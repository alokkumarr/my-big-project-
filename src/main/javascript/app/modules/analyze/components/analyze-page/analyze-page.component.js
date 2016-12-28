import template from './analyze-page.component.html';
import style from './analyze-page.component.scss';

export const AnalyzePageComponent = {
  template,
  styles: [style],
  controller: class AnalyzePageController {
    constructor($componentHandler, AnalyzeService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.AnalyzeService = AnalyzeService;
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.AnalyzeService.getMenu()
        .then(data => {
          leftSideNav.update(data);
        });
    }
  }
};
