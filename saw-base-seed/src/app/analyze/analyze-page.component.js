import template from './analyze-page.component.html';

export const AnalyzePageComponent = {
  template,
  controller: class AnalyzePageController {
    /** @ngInject */
    constructor($mdSidenav) {
      this.$mdSidenav = $mdSidenav;

      this.menu = [{
        name: 'My Analyses',
        children: [{
          name: 'Order Fulfillment'
        }, {
          name: 'Category 2'
        }, {
          name: 'Category 3'
        }, {
          name: 'Category 4'
        }, {
          name: 'Category 5'
        }]
      }, {
        name: 'Folder 2'
      }, {
        name: 'Folder 3'
      }];
    }
  }
};
