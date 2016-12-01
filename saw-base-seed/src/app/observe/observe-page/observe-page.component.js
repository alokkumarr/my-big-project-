import template from './observe-page.component.html';
import style from './observe-page.component.scss';

export const ObservePageComponent = {
  template,
  styles: [style],
  controller: class ObserverPageController {
    constructor($componentHandler, ObserveService) {
      'ngInject';

      this.$componentHandler = $componentHandler;
      this.ObserveService = ObserveService;
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.ObserveService.getMenu()
        .then(data => {
          leftSideNav.update(data);
        });
    }
  }
};
