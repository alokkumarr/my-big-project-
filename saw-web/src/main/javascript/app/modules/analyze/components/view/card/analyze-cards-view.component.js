import * as isEmpty from 'lodash/isEmpty';

import * as template from './analyze-cards-view.component.html';

export const AnalyzeCardsViewComponent = {
  template,
  bindings: {
    analyses: '<',
    analysisType: '<',
    filter: '<',
    onAction: '&',
    searchTerm: '<',
    cronJobs: '<'
  },
  controller: class AnalyzeCardsViewController {
    constructor() {
      this.filterReports = this.filterReports.bind(this);
    }

    onCardAction(type, model) {
      console.log(this.cronJobs);
      this.onAction({type, model});
    }

    filterReports(item) {
      let isIncluded;

      if (this.analysisType === 'all') {
        isIncluded = true;
      } else if (this.analysisType === 'scheduled') {
        isIncluded = !isEmpty(item.scheduleHuman);
      } else {
        isIncluded = this.analysisType === item.type;
      }

      return isIncluded;
    }
  }
};
