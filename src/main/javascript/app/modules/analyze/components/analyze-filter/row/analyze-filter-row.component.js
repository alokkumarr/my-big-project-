import template from './analyze-filter-row.component.html';
import {BOOLEAN_CRITERIA} from '../../../services/filter.service';

export const AnalyzeFilterRowComponent = {
  template,
  bindings: {
    filter: '<',
    artifact: '<',
    onChange: '&',
    analysisType: '@'
  },
  controller: class AnalyzeFilterRowController {
    constructor() {
      this.searchText = '';
      this.selectedItem = null;
      this.BOOLEAN_CRITERIA = BOOLEAN_CRITERIA;
    }

    onBooleanCriteriaSelected(value) {
      this.filter.booleanCriteria = value;
      this.onChange({filter: this.filter});
    }

    onArtifactChange(column) {
      if (column) {
        this.filter.column = column;
        this.onChange({filter: this.filter});
      }
    }

    onFilterChange(model) {
      this.filter.model = model;
      this.onChange({filter: this.filter});
    }
  }
};
