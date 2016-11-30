import template from './observe-page.component.html';
import style from './observe-page.component.scss';

export const ObservePageComponent = {
  template,
  styles: [style],
  controller: class ObserverPageController {
    /** @ngInject */
    constructor($componentHandler, $http) {
      this.$componentHandler = $componentHandler;
      this.$http = $http;
    }

    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];

      this.$http.get('/api/menu/observe')
        .then(response => {
          leftSideNav.update(response.data);
        });
    }
  }
};
