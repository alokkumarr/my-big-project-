import jsPlumb from 'jsplumb';
import isEmpty from 'lodash/isEmpty';
import find from 'lodash/find';

import template from './jsPlumbCanvas.component.html';
import style from './jsPlumbCanvas.component.scss';
import {JS_PLUMB_DEFAULT_SETTINGS} from '../settings';
import {CanvasModel} from '../models/canvasModel';

export const JSPlumbCanvas = {
  template,
  styles: [style],
  bindings: {
    id: '@'
  },
  controller: class JSPlumbCanvasCtrl {
    constructor($componentHandler, $element) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$element = $element;

      this.model = new CanvasModel();
      this._settings = JS_PLUMB_DEFAULT_SETTINGS;
    }

    $onInit() {
      this.model.component = this;
      this._unregister = this._$componentHandler.register(this.id, this);
    }

    $onDestroy() {
      this._unregister();
    }

    $postLink() {
      this._jsPlumbInst = jsPlumb.getInstance();
      this._jsPlumbInst.setContainer(this._$element);

      this._jsPlumbInst.bind('connection', info => {
        const sourceEndpointInst = info.sourceEndpoint.getParameter('component');
        const targetEndpointInst = info.targetEndpoint.getParameter('component');

        if (sourceEndpointInst && targetEndpointInst) {
          const sourceField = sourceEndpointInst.model.field;
          const targetField = targetEndpointInst.model.field;

          if (sourceField.table === targetField.table) {
            this._jsPlumbInst.detach(info.connection);
          }

          let join = this.model.findJoin(sourceField.table.name, sourceField.name, targetField.table.name, targetField.name);

          if (!join) {
            join = this.model.addJoin('inner', {
              table: sourceField.table.name,
              field: sourceField.name,
              side: sourceEndpointInst.model.side
            }, {
              table: targetField.table.name,
              field: targetField.name,
              side: targetEndpointInst.model.type
            });
          }
        }
      });
    }

    getInstance() {
      return this._jsPlumbInst;
    }

    getSettings() {
      return this._settings;
    }
  }
};
