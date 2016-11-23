import template from './sidenav.component.html';
import style from './sidenav.component.scss';

export const SidenavComponent = {
  template,
  styles: [style],
  bindings: {
    id: '@',
    menu: '<'
  },
  controller: class SidenavController {
    constructor($mdSidenav) {
      this.$mdSidenav = $mdSidenav;
    }

    toggleSidenav() {
      if (this.id) {
        this.$mdSidenav(this.id).toggle();
      }
    }
  }
};
