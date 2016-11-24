import template from './analyze-page.component.html';

export const AnalyzePageComponent = {
  template,
  controller: class AnalyzePageController {
    /** @ngInject */
    constructor($componentHandler, $mdSidenav) {
      this.$mdSidenav = $mdSidenav;

      const action = () => {
        const inst = $componentHandler.get('analyze-page-sidenav')[0];

        if (inst) {
          inst.toggleSidenav();
        }
      };

      this.menu = [{
        name: 'My Analyses',
        children: [{
          name: 'Order Fulfillment',
          url: '/analyze/1',
          action
        }, {
          name: 'Category 2',
          url: '/analyze/2',
          action
        }, {
          name: 'Category 3',
          url: '/analyze/3',
          action
        }, {
          name: 'Category 4',
          url: '/analyze/4',
          action
        }, {
          name: 'Category 5',
          url: '/analyze/5',
          action
        }]
      }, {
        name: 'Folder 2'
      }, {
        name: 'Folder 3'
      }];
    }
  }
};
