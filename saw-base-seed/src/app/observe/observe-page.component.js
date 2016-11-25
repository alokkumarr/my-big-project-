import template from './observe-page.component.html';
import style from './observe-page.component.scss';

export const ObservePageComponent = {
  template,
  styles: [style],
  controller: class ObserverPageController {
    /** @ngInject */
    constructor($componentHandler, $mdSidenav) {
      this.$componentHandler = $componentHandler;
      this.$mdSidenav = $mdSidenav;

      this.menu = [{
        name: 'Dashboard 1'
      }, {
        name: 'Dashboard 2'
      }, {
        name: 'Dashboard 3'
      }, {
        name: 'Dashboard 4'
      }];
    }

    $onInit() {
      this.leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.leftSideNav.update(this.menu);
    }
  }
};
