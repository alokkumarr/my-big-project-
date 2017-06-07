import {BehaviorSubject} from 'rxjs/BehaviorSubject';

import template from './analyze-published-detail.component.html';
import style from './analyze-published-detail.component.scss';

export const AnalyzePublishedDetailComponent = {
  template,
  styles: [style],
  controller: class AnalyzePublishedDetailController {
    constructor(AnalyzeService, $state, $window, $mdDialog) {
      'ngInject';
      this._AnalyzeService = AnalyzeService;
      this._$state = $state;
      this._$window = $window;
      this._$mdDialog = $mdDialog;
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
        this.loadExecutedAnalyses(analysisId);
      } else {
        this.loadAnalysisById(analysisId).then(() => {
          this.loadExecutedAnalyses(analysisId);
        });
      }
    }

    showExecutingFlag() {
      return this.analysis && this._AnalyzeService.isExecuting(this.analysis.id);
    }

    executeAnalysis() {
      if (this.analysis) {
        this._AnalyzeService.executeAnalysis(this.analysis);
      }
    }

    exportData() {
      this.requester.next({
        export: true
      });
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

    loadLastPublishedAnalysis(analysisId) {
      this._AnalyzeService.getLastPublishedAnalysis(analysisId)
        .then(analysis => {
          this.analysis = analysis;
        });
    }

    loadExecutedAnalyses(analysisId) {
      this._AnalyzeService.getPublishedAnalysesByAnalysisId(analysisId)
        .then(analyses => {
          this.analyses = analyses;
        });
    }

    openPublishModal(ev) {
      const tpl = '<analyze-publish-dialog model="$ctrl.analysis" on-publish="$ctrl.onPublish($data)"></analyze-publish-dialog>';

      this._$mdDialog
        .show({
          template: tpl,
          controllerAs: '$ctrl',
          autoWrap: false,
          fullscreen: true,
          focusOnOpen: false,
          multiple: true,
          targetEvent: ev,
          clickOutsideToClose: true
        });
    }

  }
};
