import template from './panel.component.html';

export const PanelComponent = {
  template,
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
