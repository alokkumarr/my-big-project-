import * as template from './analyze-card.component.html';
import style from './analyze-card.component.scss';
import * as forEach from 'lodash/forEach';
import cronstrue from 'cronstrue';
import * as moment from 'moment';
import * as isUndefined from 'lodash/isUndefined';
import * as isEmpty from 'lodash/isEmpty';

let self;
export const AnalyzeCardComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onAction: '&',
    highlightTerm: '<',
    cronJobs: '<'
  },
  controller: class AnalyzeCardController {

    constructor($timeout, $mdDialog, AnalyzeService, $log, AnalyzeActionsService, JwtService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$timeout = $timeout;
      this._AnalyzeService = AnalyzeService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$log = $log;
      this._JwtService = JwtService;

      this.canUserFork = false;
      this.cronReadbleMsg = 'No Schedule Set';
      self = this; // the execution context in onSuccesfulPublish doesn't seem to be same as this
    }

    $onInit() {
      this.canUserFork = this._JwtService.hasPrivilege('FORK', {
        subCategoryId: this.model.categoryId
      });
      this.analysisType = this.model.type;
      if (this.model.type === 'esReport') {
        this.analysisType = 'REPORT';
      }
      this.applyCronPropertytoCard();
    }

    applyCronPropertytoCard() {
      forEach(this.cronJobs, cron => {
        if (cron.jobDetails.analysisID === this.model.id && !isEmpty(cron.jobDetails.cronExpression)) {
          const cronLocal = this.convertToLocal(cron.jobDetails.cronExpression);
          this.cronReadbleMsg = cronstrue.toString(cronLocal);
        }
      });
    }

    convertToLocal(CronUTC) {
      const splitArray = CronUTC.split(' ');
      const date = new Date();
      date.setUTCHours(splitArray[2], splitArray[1]);
      const UtcTime = moment.utc(date).local().format('mm HH').split(' ');
      splitArray[1] = UtcTime[0];
      splitArray[2] = UtcTime[1];
      return splitArray.join(' ');
    }

    showExecutingFlag() {
      return this._AnalyzeService.isExecuting(this.model.id);
    }

    fork() {
      this._AnalyzeActionsService.fork(this.model);
    }

    onSuccessfulDeletion(analysis) {
      this.onAction({
        type: 'onSuccessfulDeletion',
        model: analysis
      });
    }

    onSuccessfulExecution(analysis) {
      this.onAction({
        type: 'onSuccessfulExecution',
        model: analysis
      });
    }

    onSuccessfulPublish(analysis) {
      this.cronReadbleMsg = '';
      if (!isUndefined(analysis.schedule.cronExpression)) {
        const cronLocalMsg = this.convertToLocal(analysis.schedule.cronExpression);
        this._$timeout(() => {
          self.cronReadbleMsg = cronstrue.toString(cronLocalMsg);
        });
      }
      this.onAction({
        type: 'onSuccessfulPublish',
        model: analysis
      });
    }
  }
};
