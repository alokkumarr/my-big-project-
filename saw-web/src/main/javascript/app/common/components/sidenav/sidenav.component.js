import * as template from './sidenav.component.html';
import style from './sidenav.component.scss';

export const SidenavComponent = {
  template,
  styles: [style],
  bindings: {
    id: '@',
    menu: '<'
  },
  controller: class SidenavController {
    constructor($componentHandler, $mdSidenav, $timeout, $scope, SidenavService) {
      this.$componentHandler = $componentHandler;
      this.$mdSidenav = $mdSidenav;
      this.$timeout = $timeout;
      this._$scope = $scope;
      this._moduleName = '';
      this._SidenavService = SidenavService;

      this._sidenavInst = null;
    }

    $onInit() {
      this.unregister = this.$componentHandler.register(this.id, this);
      this._$scope.$watch(() => this.isOpen(), newVal => {
        this._SidenavService.sidenavEvent.next(newVal);
      });
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

    openSidenavManually() {
      if (!this.isOpen()) {
        this.$timeout(() => {
          this._sidenavInst.toggle();
        });
      }
    }
    getMenuHeader() {
      return {
        analyze: 'Analyses',
        observe: 'Dashboards',
        admin: 'Manage'
      }[this._moduleName.toLowerCase()] || '';
    }

    update(data, moduleName = '') {
      this._moduleName = moduleName;
      this.menu = data;
    }
  }
};
