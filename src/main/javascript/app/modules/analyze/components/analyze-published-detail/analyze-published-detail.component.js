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
    }

    $onInit() {
      this.loadAnalysis();
    }

    loadAnalysis() {
      this._AnalyzeService.getAnalysisById(this._$state.params.publishId)
        .then(analysis => {
          this.analysis = analysis;
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
