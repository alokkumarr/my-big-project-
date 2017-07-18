import jsPlumb from 'jsplumb';

import template from './jsPlumbCanvas.component.html';
import style from './jsPlumbCanvas.component.scss';
import {JS_PLUMB_DEFAULT_SETTINGS} from '../settings';
import {CanvasModel} from '../models/canvasModel';

const EVENTS = {
  JOIN_CHANGED: 'joinChanged'
};

export const JSPlumbCanvas = {
  template,
  styles: [style],
  bindings: {
    id: '@',
    onChange: '&'
  },
  controller: class JSPlumbCanvasCtrl {
    constructor($componentHandler, $eventEmitter, $element, $scope) {
      'ngInject';
      this._$componentHandler = $componentHandler;
      this._$eventEmitter = $eventEmitter;
      this._$element = $element;
      this._$scope = $scope;

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
              side: targetEndpointInst.model.side
            });

            this._$scope.$apply();
            this.onChange({
              name: EVENTS.JOIN_CHANGED,
              params: {}
            });
          }
        }
      });

      this._jsPlumbInst.bind('connectionDetached', info => {
        const connComponent = info.connection.getParameter('component');

        connComponent._canvas.removeJoin(connComponent.model);
        this.onChange({
          name: EVENTS.JOIN_CHANGED,
          params: {}
        });
      });

      this._jsPlumbInst.bind('connectionMoved', info => {
        if (info.newSourceId === info.originalSourceId && info.newTargetId === info.originalTargetId) {
          return;
        }

        const connComponent = info.connection.getParameter('component');

        const type = connComponent.model.type;

        connComponent._canvas.removeJoin(connComponent.model);

        const sourceEndpointInst = info.newSourceEndpoint.getParameter('component');
        const targetEndpointInst = info.newTargetEndpoint.getParameter('component');

        if (sourceEndpointInst && targetEndpointInst) {
          const sourceField = sourceEndpointInst.model.field;
          const targetField = targetEndpointInst.model.field;

          this.model.addJoin(type, {
            table: sourceField.table.name,
            field: sourceField.name,
            side: sourceEndpointInst.model.side
          }, {
            table: targetField.table.name,
            field: targetField.name,
            side: targetEndpointInst.model.side
          });

          this._$scope.$apply();
          this.onChange({
            name: EVENTS.JOIN_CHANGED,
            params: {}
          });
        }
      });
    }

    joinChanged(params) {
      this.onChange({
        name: EVENTS.JOIN_CHANGED,
        params
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
