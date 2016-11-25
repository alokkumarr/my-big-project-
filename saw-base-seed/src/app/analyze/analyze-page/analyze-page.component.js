import template from './analyze-page.component.html';
import style from './analyze-page.component.scss';

export const AnalyzePageComponent = {
  template,
  styles: [style],
  controller: class AnalyzePageController {
    /** @ngInject */
    constructor($componentHandler, $mdSidenav) {
      this.$componentHandler = $componentHandler;
      this.$mdSidenav = $mdSidenav;

      this.menu = [{
        name: 'My Analyses',
        children: [{
          name: 'Order Fulfillment',
          url: '/analyze/1'
        }, {
          name: 'Category 2',
          url: '/analyze/2'
        }, {
          name: 'Category 3',
          url: '/analyze/3'
        }, {
          name: 'Category 4',
          url: '/analyze/4'
        }, {
          name: 'Category 5',
          url: '/analyze/5'
        }]
      }, {
        name: 'Folder 2'
      }, {
        name: 'Folder 3'
      }];
    }

    $onInit() {
      this.leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.leftSideNav.update(this.menu);
    }
  }
};
