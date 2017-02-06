import template from './root.component.html';

export const RootComponent = {
  template,
  controller: class RootController {
    constructor($mdPanel, $mdSidenav) {
      this.$mdPanel = $mdPanel;
      this.$mdSidenav = $mdSidenav;

      this.toggleRight = this.buildToggler('right');
      this.toggleLeft = this.buildToggler('left');
    }

    buildToggler(navID) {
      return () => {
        // Component lookup should always be available since we are not using `ng-if`
        this.$mdSidenav(navID).toggle();
      };
    }
  }
};
