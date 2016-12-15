import template from './sidenav.component.html';
import style from './_sidenav.component.scss';

export const SidenavComponent = {
  template,
  styles: [style],
  bindings: {
    id: '@',
    menu: '<'
  },
  controller: class SidenavController {
    constructor($componentHandler, $mdSidenav, $timeout) {
      this.$componentHandler = $componentHandler;
      this.$mdSidenav = $mdSidenav;
      this.$timeout = $timeout;

      this._sidenavInst = null;
    }

    $onInit() {
      this.unregister = this.$componentHandler.register(this.id, this);
    }

    $onDestroy() {
      this.unregister();
      this._sidenavInst = null;
    }

    $postLink() {
      if (this.id) {
        this._sidenavInst = this.$mdSidenav(this.id);
      }
    }

    isOpen() {
      return this._sidenavInst ? this._sidenavInst.isOpen() : false;
    }

    toggleSidenav() {
      if (this._sidenavInst) {
        this.$timeout(() => {
          this._sidenavInst.toggle();
        });
      }
    }

    update(data) {
      this.menu = data;
    }
  }
};
