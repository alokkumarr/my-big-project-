import template from './panel.component.html';
import style from './panel.component.scss';

export const PanelComponent = {
  template,
  transclude: true,
  bindings: {
    title: '<',
    startCollapsed: '@'
  },
  controller: class PanelCtrl {
    constructor() {
      this.isCollapsed = false;
    }

    $onInit() {
      this.isCollapsed = Boolean(this.startCollapsed);
    }

    toggle() {
      this.isCollapsed = !this.isCollapsed;
    }
  }
};
