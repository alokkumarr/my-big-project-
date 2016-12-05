class SidenavTargetDirectiveClass {
  constructor() {
    this.restrict = 'A';
    this.bindToController = {
      sidenavTarget: '@'
    };
    this.controllerAs = 'SidenavTargetCtrl';

    this.controller = class SideNavTargetController {
      constructor($mdSidenav, $element) {
        this.$mdSidenav = $mdSidenav;

        $element.on('click', () => {
          this.toggleSidenav(this.sidenavTarget);
        });
      }

      toggleSidenav(target) {
        if (target) {
          this.$mdSidenav(target).toggle();
        }
      }
    };
  }
}

export const SidenavTargetDirective = () => {
  return new SidenavTargetDirectiveClass();
};
