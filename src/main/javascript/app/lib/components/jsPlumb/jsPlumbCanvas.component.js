import 'mottle';
import jsPlumb from 'jsplumb';

import template from './jsPlumbCanvas.component.html';
import style from './_jsPlumbCanvas.component.scss';
import {JS_PLUMB_DEFAULT_SETTINGS} from './settings';

export const JSPlumbCanvas = {
  template,
  styles: [style],
  bindings: {
    tables: '<',
    settings: '<'
  },
  controller: class JSPlumbCanvasCtrl {
    constructor($element) {
      'ngInject';

      this.$element = $element;
      this.settings = this.settings || JS_PLUMB_DEFAULT_SETTINGS;
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
