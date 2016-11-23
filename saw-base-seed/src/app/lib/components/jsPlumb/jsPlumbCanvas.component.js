import template from './jsPlumbCanvas.component.html';
import style from './jsPlumbCanvas.component.scss';
import {JS_PLUMB_SETTINGS} from './settings';

import jsPlumb from 'jsPlumb';
import 'mottle';

export const JSPlumbCanvas = {
  template,
  styles: [style],
  bindings: {
    tables: '<'
  },
  controller: class JSPlumbCanvasCtrl {
    /** @ngInject */
    constructor($element) {
      this.$element = $element;
      this.settings = JS_PLUMB_SETTINGS;
    }

    $postLink() {
      this.jsPlumbInst = jsPlumb.getInstance();
      this.jsPlumbInst.setContainer(this.$element);
    }

    getInstance() {
      return this.jsPlumbInst;
    }
  }
};
