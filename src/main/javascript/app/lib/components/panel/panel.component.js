import template from './panel.component.html';
import style from './panel.component.scss';

export const PanelComponent = {
  template,
  styles: [style],
  transclude: true,
  bindings: {
    title: '@'
  },
  controller: class PanelCtrl {
    constructor() {
      this.isCollapsed = false;
    }

    $postLink() {
    }

    toggle() {
      this.isCollapsed = !this.isCollapsed;
    }
  }
};
