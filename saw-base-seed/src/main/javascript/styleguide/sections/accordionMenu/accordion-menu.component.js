import template from './accordion-menu.component.html';

export const AccordionMenuComponent = {
  template,
  controller: class AccordionMenuController {
    constructor($http) {
      'ngInject';

      $http.get('/api/menu')
        .then(response => {
          this.menu = response.data;
        });
    }
  }
};
