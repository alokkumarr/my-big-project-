import map from 'lodash/map';

import template from './analyze-publish-dialog.component.html';
import style from './analyze-publish-dialog.component.scss';

export const AnalyzePublishDialogComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onPublish: '&'
  },
  controller: class AnalyzePublishDialogController {
    constructor($mdDialog) {
      'ngInject';

      this._$mdDialog = $mdDialog;
      this.dateFormat = 'mm/dd/yyyy';
      this.subjects = ['Anyone at AT&T', 'Only Analysts', 'Only I'];
      this.subject = this.subjects[0];
      this.predicates = ['VIEW', 'FORK_AND_EDIT'];
      this.predicate = this.predicates[0];
      this.scheduleOptions = ['PUBLISH_ONCE', 'SCHEDULE'];
      this.schedule = this.scheduleOptions[0];

      this.repeatIntervals = ['DAYS', 'WEEKS'];
      this.repeatInterval = this.repeatIntervals[0];
      this.repeatOrdinals = [1, 2, 3, 4, 5, 6, 7];
      this.repeatOrdinal = this.repeatOrdinals[0];
      this.daysOfWeek = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY'];
      this.repeatOnDaysOfWeek = map(this.daysOfWeek, dayString => {
        return {
          keyword: dayString,
          checked: false
        };
      });

      this.endCriteria = {
        never: {
          keyword: 'NEVER'
        },
        after: {
          keyword: 'AFTER',
          occurenceCount: 1
        },
        on: {
          keyword: 'ON',
          endDate: null
        }
      };
      this.endCriterion = this.endCriteria.never.keyword;
    }

    cancel() {
      this._$mdDialog.cancel();
    }

    publish() {
      this._$mdDialog.hide();
    }
  }
};
