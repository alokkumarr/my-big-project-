import template from './analyze-cards-view.component.html';

export const AnalyzeCardsViewComponent = {
  template,
  bindings: {
    reports: '<',
    reportType: '<',
    filter: '<',
    onAction: '&',
    searchTerm: '<'
  },
  controller: class AnalyzeCardsViewController {
    constructor() {
      this.filterReports = this.filterReports.bind(this);
    }

    onCardAction(type, model) {
      this.onAction({type, model});
    }

    filterReports(item) {
      let isIncluded = true;

      if (this.reportType !== 'all') {
        isIncluded = this.reportType === item.type;
      }

      return isIncluded;
    }
  }
};
