import template from './sidenav-btn.component.html';

export const SidenavBtnComponent = {
  template,
  bindings: {
    target: '@'
  },
  controller: class SidenavBtnController {
    constructor($mdSidenav) {
      this.$mdSidenav = $mdSidenav;

      this._sidenavInst = null;
    }

    $onDestroy() {
      this._sidenavInst = null;
    }

    $postLink() {
      if (this.target) {
        this._sidenavInst = this.$mdSidenav(this.target);
      }
    }

    toggleSidenav() {
      if (this._sidenavInst) {
        this._sidenavInst.toggle();
      }
    }

    isTargetOpen() {
      return this._sidenavInst ? this._sidenavInst.isOpen() : false;
    }
  }
};
