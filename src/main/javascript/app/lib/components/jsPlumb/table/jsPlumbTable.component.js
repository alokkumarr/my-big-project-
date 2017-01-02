import isEmpty from 'lodash/isEmpty';

import template from './jsPlumbTable.component.html';
import style from './jsPlumbTable.component.scss';

export const JSPlumbTable = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class JSPlumbTableCtrl {
    constructor($scope, $element, $timeout) {
      'ngInject';

      this._$scope = $scope;
      this._$element = $element;
      this._$timeout = $timeout;
    }

    $onInit() {
      this.model.component = this;

      this._canvas = this.model.canvas;
      this._jsPlumbInst = this._canvas.component.getInstance();

      this.updatePosition();
    }

    $postLink() {
      this._jsPlumbInst.draggable(this._$element, {
        allowNegative: false,
        drag: event => {
          this.model.x = event.pos[0];
          this.model.y = event.pos[1];
        }
      });
    }

    updatePosition() {
      this._$element.css({
        left: `${this.model.x}px`,
        top: `${this.model.y}px`
      });
    }

    onFieldMouseDown($event, field) {
      const itemListXMiddle = $event.target.offsetWidth / 2;
      const side = itemListXMiddle > $event.offsetX ? 'left' : 'right';

      this._fieldActionTimer = this._$timeout(() => {
        const endpoint = field.getEndpoint(side);

        if (!endpoint) {
          field.addEndpoint(side);
        } else if (isEmpty(endpoint.component.endpoint.connections)) {
          field.removeEndpoint(endpoint);
        }
      }, 1000);
    }

    onFieldMouseUp() {
      if (this._fieldActionTimer) {
        this._$timeout.cancel(this._fieldActionTimer);
        this._fieldActionTimer = null;
      }
    }
  }
};
