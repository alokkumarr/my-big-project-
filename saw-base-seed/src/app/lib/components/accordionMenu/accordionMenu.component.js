import template from './accordionMenu.component.html';
import style from './accordionMenu.component.scss';

export const AccordionMenu = {
  template,
  styles: [style],
  bindings: {
    source: '<'
  },
  controller: class AccordionMenuCtrl {
    /** @ngInject */
    constructor($element) {
      this.$element = $element;
    }

    $postLink() {
    }
  }
};
