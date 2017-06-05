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
      const publishId = this._$state.params.publishId;
      if (publishId) {
        this.loadAnalysisById(publishId);
      } else {
        // load the last published analysis
        this.loadLastPublishedAnalysis(analysisId);
      }
      this.loadOtherAnalyses(analysisId);
    }

    exportData() {
      this.requester.next({
        export: true
      });
    }

    loadAnalysisById(publishId) {
      this._AnalyzeService.getPublishedAnalysisById(publishId)
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

    loadOtherAnalyses(analysisId) {
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
