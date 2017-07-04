import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import get from 'lodash/get';

import template from './analyze-published-detail.component.html';
import style from './analyze-published-detail.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const AnalyzePublishedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzePublishedDetailController extends AbstractComponentController {
    constructor($injector, AnalyzeService, $state, $rootScope, $mdDialog,
                $window, toastMessage, FilterService, AnalyzeActionsService) {
      'ngInject';
      super($injector);

      this._AnalyzeService = AnalyzeService;
      this._AnalyzeActionsService = AnalyzeActionsService;
      this._$state = $state;
      this._$rootScope = $rootScope;
      this._FilterService = FilterService;
      this._toastMessage = toastMessage;
      this._$window = $window; // used for going back from the template
      this._$mdDialog = $mdDialog;
      this._executionId = $state.params.executionId;
      this.isPublished = true;

      this.requester = new BehaviorSubject({});
    }

    $onInit() {
      const analysisId = this._$state.params.analysisId;
      const analysis = this._$state.params.analysis;
      if (analysis) {
        this.analysis = analysis;
        if (!this.analysis.schedule) {
          this.isPublished = false;
        }
        this.loadExecutionData();
        this.loadExecutedAnalyses(analysisId);
      } else {
        this.loadAnalysisById(analysisId).then(() => {
          this.loadExecutionData();
          this.loadExecutedAnalyses(analysisId);
        });
      }
    }

    showExecutingFlag() {
      return this.analysis && this._AnalyzeService.isExecuting(this.analysis.id);
    }

    exportData() {
      this.requester.next({
        export: true
      });
    }

    loadExecutionData() {
      if (this._executionId) {
        this._AnalyzeService.getExecutionData(this.analysis.id, this._executionId).then(data => {
          this.requester.next({data});
        });
      }
    }

    loadAnalysisById(analysisId) {
      return this._AnalyzeService.readAnalysis(analysisId)
        .then(analysis => {
          this.analysis = analysis;
          if (!this.analysis.schedule) {
            this.isPublished = false;
          }
        });
    }

    /* If data for a particular execution is not requested,
       load data from the most recent execution */
    loadLastPublishedAnalysis() {
      if (!this._executionId) {
        this._executionId = get(this.analyses, '[0].id', null);
        this.loadExecutionData();
      }
    }

    loadExecutedAnalyses(analysisId) {
      this._AnalyzeService.getPublishedAnalysesByAnalysisId(analysisId)
        .then(analyses => {
          this.analyses = analyses;
          this.loadLastPublishedAnalysis();
        });
    }

    publish() {
      this._AnalyzeActionsService.publish(this.analysis);
    }

    onSuccessfulDeletion(analysis) {
      this._$state.go('analyze.view', {id: analysis.categoryId});
    }
  }
};
