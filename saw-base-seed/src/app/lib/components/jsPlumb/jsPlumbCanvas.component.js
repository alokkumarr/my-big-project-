import 'mottle';
import jsPlumb from 'jsplumb';

import {JS_PLUMB_SETTINGS} from './settings';
import template from './jsPlumbCanvas.component.html';
import style from './jsPlumbCanvas.component.scss';

export const JSPlumbCanvas = {
  template,
  styles: [style],
  bindings: {
    tables: '<'
  },
  controller: class JSPlumbCanvasCtrl {
    constructor($element) {
      'ngInject';

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
