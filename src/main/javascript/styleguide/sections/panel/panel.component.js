import template from './panel.component.html';

export const PanelComponent = {
  template,
  controller: class PanelController {
    constructor() {
      this.title = '';
    }
  }
};
