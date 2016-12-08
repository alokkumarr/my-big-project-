import template from './accordionMenu.component.html';
import style from './accordionMenu.component.scss';

export const AccordionMenu = {
  template,
  styles: [style],
  bindings: {
    source: '<'
  },
  controller: class AccordionMenuCtrl {
    constructor($element) {
      'ngInject';

      this.$element = $element;
    }

    $postLink() {
    }
  }
};
