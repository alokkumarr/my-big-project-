import template from './observe-page.component.html';

export const ObservePageComponent = {
  template,
  controller: class ObserverPageController {
    /** @ngInject */
    constructor($mdSidenav) {
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
  }
};
