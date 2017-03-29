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

    constructor($mdDialog, $state, AnalyzeService, $log) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$state = $state;
      this._AnalyzeService = AnalyzeService;
      this._$log = $log;
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

    openDeleteModal() {
      const confirm = this._$mdDialog.confirm()
        .title('Are you sure you want to delete this analysis?')
        .textContent('Any published analyses will also be deleted.')
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        return this._AnalyzeService.deleteAnalysis(this.model.id);
      }).then(data => {
        this.onAction({
          type: 'delete',
          model: data
        });
      }, err => {
        if (err) {
          this._$log.error(err);
        }
      });
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
