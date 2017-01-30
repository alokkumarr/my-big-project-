import template from './analyze-filter-container.component.html';

export const AnalyzeFilterContainerComponent = {
  template,
  transclude: true,
  bindings: {
    filter: '<'
  },
  controller: class AnalyzeFilterContainerComponent {
    onOperatorSelected(value) {
      this.filter.operator = value;
    }
  }
};
