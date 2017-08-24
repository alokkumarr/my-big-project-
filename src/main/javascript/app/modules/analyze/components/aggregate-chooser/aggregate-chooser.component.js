import template from './aggregate-chooser.component.html';
import {AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE, AGGREGATE_TYPES_OBJ} from '../../consts';

export const AggregateChooserComponent = {
  template,
  bindings: {
    artifactColumn: '<',
    onSelect: '&'
  },
  controller: class AggregateChooserController {
    constructor() {
      'ngInject';
      this.AGGREGATE_TYPES = AGGREGATE_TYPES;
      this.AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;
      this.DEFAULT_AGGREGATE_TYPE = DEFAULT_AGGREGATE_TYPE;
    }

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    onSelectAggregateType(aggregateType, artifactColumn) {
      this.onSelect({aggregateType, artifactColumn});
    }
  }
};
