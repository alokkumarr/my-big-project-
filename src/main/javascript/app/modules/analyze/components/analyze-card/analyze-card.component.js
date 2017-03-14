import template from './analyze-card.component.html';
import style from './analyze-card.component.scss';

export const AnalyzeCardComponent = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onAction: '&',
    highlightTerm: '<'
  },
  controller: class AnalyzeCardController {

    constructor($mdDialog, $state, AnalyzeService) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$state = $state;
      this._AnalyzeService = AnalyzeService;
    }

    openMenu($mdMenu, ev) {
      $mdMenu.open(ev);
    }

    openPublishModal(ev) {
      const tpl = '<analyze-publish-dialog model="$ctrl.model" on-publish="$ctrl.onPublish($data)"></analyze-publish-dialog>';

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

    openPrintModal() {
    }

    openExportModal() {
    }

    execute(analysisId) {
      this._AnalyzeService.executeAnalysis(analysisId)
        .then(analysis => {
          this.goToAnalysis(analysis.analysisId, analysis.publishedAnalysisId);
        });
    }

    goToAnalysis(analysisId, publishId) {
      this._$state.go('analyze.publishedDetail', {analysisId, publishId});
    }

    fork() {
      this.onAction({
        type: 'fork',
        model: this.model
      });
    }

    edit() {
      this.onAction({
        type: 'edit',
        model: this.model
      });
    }
  }
};
