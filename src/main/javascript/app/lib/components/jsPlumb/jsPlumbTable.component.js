import template from './jsPlumbTable.component.html';
import style from './jsPlumbTable.component.scss';

export const JSPlumbTable = {
  template,
  styles: [style],
  require: {
    canvas: '^jsPlumbCanvas'
  },
  bindings: {
    metadata: '<',
    settings: '<'
  },
  controller: class JSPlumbTableCtrl {
    constructor($scope, $element, $timeout) {
      'ngInject';

      this.$scope = $scope;
      this.$element = $element;
      this.$timeout = $timeout;
    }

    $onInit() {
      this.jsPlumbInst = this.canvas.getInstance();
      this.updatePosition();
    }

    $postLink() {
      this.jsPlumbInst.draggable(this.$element, {
        allowNegative: false,
        drag: event => {
          this.metadata.x = event.pos[0];
          this.metadata.y = event.pos[1];
        }
      });

      this.jsPlumbInst.bind('connection', info => {
        const sourceEndpointInst = info.sourceEndpoint.getParameter('instance');
        const targetEndpointInst = info.targetEndpoint.getParameter('instance');

        if (sourceEndpointInst && targetEndpointInst) {
          if (sourceEndpointInst.table === targetEndpointInst.table) {
            this.jsPlumbInst.detach(info.connection);
          }
        }
      });
    }

    updatePosition() {
      this.$element.css({
        left: `${this.metadata.x}px`,
        top: `${this.metadata.y}px`
      });
    }

    onFieldMouseDown($event, field) {
      const itemListXMiddle = $event.target.offsetWidth / 2;
      const anchor = itemListXMiddle > $event.offsetX ? 'LeftMiddle' : 'RightMiddle';

      this._fieldActionTimer = this.$timeout(() => {
        this.addEndpointTo(field, anchor);
      }, 1000);
    }

    onFieldMouseUp() {
      if (this._fieldActionTimer) {
        this.$timeout.cancel(this._fieldActionTimer);
        this._fieldActionTimer = null;
      }
    }

    addEndpointTo(field, anchor) {
      const endpoint = {
        uuid: Date.now(),
        anchor,
        connections: []
      };

      if (!field.endpoints) {
        field.endpoints = [];
      }

      field.endpoints.push(endpoint);

      this.$scope.$apply();

      return endpoint;
    }
  }
};
