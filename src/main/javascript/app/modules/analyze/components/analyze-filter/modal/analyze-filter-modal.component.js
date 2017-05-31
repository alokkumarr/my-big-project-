import template from './analyze-filter-modal.component.html';

export const AnalyzeFilterModalComponent = {
  template,
  bindings: {
    filters: '<',
    artifacts: '<'
  },
  controller: class AnalyzeFlterModalController {
    $onInit() {
      console.log('artifacts: ', this.artifacts);
      console.log('filters: ', this.filters);
    }

    addFilter(artifactName) {
      if (!this.filters[artifactName]) {
        this.filters[artifactName] = [];
      }
      this.filters[artifactName].push({});
    }

    onFilterChange(filter, artifactName, index) {
      this.filters[artifactName][index] = filter;
    }
  }
};
