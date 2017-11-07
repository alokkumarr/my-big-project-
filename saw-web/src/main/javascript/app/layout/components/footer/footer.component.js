import * as template from './footer.component.html';

export const LayoutFooterComponent = {
  template,
  controller: class LayoutFooterController {
    constructor() {
      this.version = __VERSION__;
    }
  }
};
