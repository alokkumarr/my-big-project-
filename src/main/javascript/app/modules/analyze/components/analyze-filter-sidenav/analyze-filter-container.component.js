import template from './analyze-filter-container.component.html';
import {BOOLEAN_CRITERIA} from '../../services/filter.service';

export const AnalyzeFilterContainerComponent = {
  template,
  transclude: true,
  bindings: {
    filter: '<'
  },
  controller: class AnalyzeFilterContainerComponent {
    constructor() {
      this.BOOLEAN_CRITERIA = BOOLEAN_CRITERIA;
    }
    onBooleanCriteriaSelected(value) {
      this.filter.booleanCriteria = value;
    }
  }
};
