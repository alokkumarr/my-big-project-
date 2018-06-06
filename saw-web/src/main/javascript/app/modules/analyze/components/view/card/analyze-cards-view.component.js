
import * as forEach from 'lodash/forEach';
import * as template from './analyze-cards-view.component.html';

export const AnalyzeCardsViewComponent = {
  template,
  bindings: {
    analyses: '<',
    analysisType: '<',
    onAction: '&',
    searchTerm: '<',
    cronJobs: '<'
  },
  controller: class AnalyzeCardsViewController {
    constructor() {
      this.filterReports = this.filterReports.bind(this);
    }

    onCardAction(type, model) {
      this.onAction({type, model});
    }

    filterReports(item) {
      let isIncluded;

      if (this.analysisType === 'all') {
        isIncluded = true;
      } else if (this.analysisType === 'scheduled') {
        isIncluded = false;
        forEach(this.cronJobs, cron => {
          if (cron.jobDetails.analysisID === item.id) {
            isIncluded = true;
          }
        });
        return isIncluded;
      } else {
        isIncluded = this.analysisType === item.type;
      }

      return isIncluded;
    }
  }
};
