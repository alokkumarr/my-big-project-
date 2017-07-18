import template from './panel.component.html';
import style from './panel.component.scss';

export const PanelComponent = {
  template,
  styles: [style],
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
      this.isCollapsed = this.startCollapsed === 'true';
    }

    toggle() {
      this.isCollapsed = !this.isCollapsed;
    }
  }
};
