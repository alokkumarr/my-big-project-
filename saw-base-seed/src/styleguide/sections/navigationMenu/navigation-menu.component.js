import angular from 'angular';
import template from './navigation-menu.component.html';

export const NavigationMenuComponent = {
  template,
  controller: class NavigationMenuController {
    constructor($mdSidenav) {
      this.$mdSidenav = $mdSidenav;
      this.menu = this.getMenu();
    }

    getMenu() {
      return [{
        name: 'Controls',
        url: 'controls'
      }, {
        name: 'Sql table',
        url: 'sqlTable'
      }, {
        name: 'Pivotgrid',
        url: 'pivotgrid'
      }, {
        name: 'Accordion menu',
        url: 'accordionMenu'
      }, {
        name: 'Panel',
        url: 'panel'
      }, {
        name: 'Charts',
        url: 'charts'
      }, {
        name: 'Modals',
        url: 'modals'
      }, {
        name: 'Snapshotkpi',
        url: 'snapshotKpi'
      }, {
        name: 'Analysis card',
        url: 'analysisCard'
      }].map(item => this.putAction(item));
    }

    putAction(object) {
      return angular.extend(object, {action: this.onItemClick.bind(this)});
    }

    onItemClick() {
      this.$mdSidenav('left').toggle();
    }
  }
};
