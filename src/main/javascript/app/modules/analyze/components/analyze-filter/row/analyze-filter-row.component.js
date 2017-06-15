import template from './analyze-filter-row.component.html';

export const AnalyzeFilterRowComponent = {
  template,
  bindings: {
    filter: '<',
    artifact: '<',
    onChange: '&'
  },
  controller: class AnalyzeFilterRowController {
    constructor() {
      this.searchText = '';
      this.selectedItem = null;
    }

    onArtifactChange(column) {
      if (column) {
        this.filter.column = column;
        this.onChange({filter: this.filter});
        this.filter.model = null;
      }
    }

    onFilterChange(model) {
      this.filter.model = model;
      this.onChange({filter: this.filter});
    }
  }
};
