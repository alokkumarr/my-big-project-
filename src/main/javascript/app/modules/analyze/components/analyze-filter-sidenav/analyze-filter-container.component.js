import template from './analyze-filter-container.component.html';
import {FILTER_OPERATORS} from '../../services/filter.service';

export const AnalyzeFilterContainerComponent = {
  template,
  transclude: true,
  bindings: {
    filter: '<'
  },
  controller: class AnalyzeFilterContainerComponent {
    constructor() {
      this.FILTER_OPERATORS = FILTER_OPERATORS;
    }
    onOperatorSelected(value) {
      this.filter.operator = value;
    }
  }
};
