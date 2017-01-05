class SidenavTargetDirectiveClass {
  constructor() {
    this.restrict = 'A';
    this.bindToController = {
      sidenavTarget: '@',
      hideOnOpen: '<'
    };
    this.controllerAs = 'SidenavTargetCtrl';

    this.controller = class SideNavTargetController {
      constructor($mdSidenav, $element) {
        this.$mdSidenav = $mdSidenav;
        this.$element = $element;

        this._sidenavInst = null;

        $element.on('click', () => {
          this.toggleSidenav();
        });
      }

      toggleSidenav() {
        if (this._sidenavInst) {
          this._sidenavInst.toggle();
        }
      }

      isTargetOpen() {
        return this._sidenavInst ? this._sidenavInst.isOpen() : false;
      }
    };

    this.link = ($scope, $element, $attrs, ctrl) => {
      if (ctrl.sidenavTarget) {
        ctrl._sidenavInst = ctrl.$mdSidenav(ctrl.sidenavTarget);
      }

      if (ctrl.hideOnOpen) {
        $scope.$watch(ctrl.isTargetOpen.bind(ctrl), val => {
          if (val) {
            ctrl.$element.css('display', 'none');
          } else {
            ctrl.$element.css('display', 'initial');
          }
        });
      }

      $scope.$on('$destroy', () => {
        ctrl._sidenavInst = null;
      });
    };
  }
}

export default () => {
  return new SidenavTargetDirectiveClass();
};
