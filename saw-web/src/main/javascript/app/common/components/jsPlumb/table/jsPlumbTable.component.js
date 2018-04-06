import * as isEmpty from 'lodash/isEmpty';

import * as template from './jsPlumbTable.component.html';
import style from './jsPlumbTable.component.scss';
import * as isUndefined from 'lodash/isUndefined';
import {AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE, AGGREGATE_TYPES_OBJ, NUMBER_TYPES, AGGREGATE_STRING_TYPES} from '../../../consts';

export const JSPlumbTable = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    didAnalysisChange: '='
  },
  controller: class JSPlumbTableCtrl {
    constructor($scope, $element, $timeout) {
      'ngInject';

      this._$scope = $scope;
      this._$element = $element;
      this._$timeout = $timeout;

      this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      this.AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
      this.DEFAULT_AGGREGATE_TYPE = DEFAULT_AGGREGATE_TYPE;
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
      if (!field.isJoinEligible) {
        return;
      }

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

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    checkType(field) {
      if (field.type === 'string') {
        this.AGGREGATE_TYPES = AGGREGATE_STRING_TYPES;
      } else {
        this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      }
      if (NUMBER_TYPES.includes(field.type) || field.type === 'string') {
        return true;
      }
    }

    onSelectAggregateType(aggregateType, field) {
      if (!isUndefined(field.format) && field.format.percentage) {
        delete field.format.percentage;
      }
      this.didAnalysisChange = true;
      field.aggregate = aggregateType.value;
      field.meta.aggregate = aggregateType.value;
    }

    clearAggegate(field) {
      if (field.format.percentage) {
        delete field.format.percentage;
      }
      delete field.aggregate;
      delete field.meta.aggregate;
    }
  }
};
