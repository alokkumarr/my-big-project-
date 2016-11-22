import template from './sidenav-btn.component.html';

export const SidenavBtnComponent = {
  template,
  bindings: {
    target: '@'
  },
  controller: class SidenavBtnController {
    constructor($mdSidenav) {
      this.$mdSidenav = $mdSidenav;
    }

    toggleSidenav() {
      if (this.target) {
        this.$mdSidenav(this.target).toggle();
      }
    }
  }
};
