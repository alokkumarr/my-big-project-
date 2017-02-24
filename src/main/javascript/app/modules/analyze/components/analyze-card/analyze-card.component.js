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

    constructor($mdDialog, $state) {
      'ngInject';
      this._$mdDialog = $mdDialog;
      this._$state = $state;
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

    openPrintModal(ev) {
    }

    openExportModal(ev) {
    }

    execute() {
      this.goToAnalysis(this.model.id);
    }

    goToAnalysis(id) {
      this._$state.go('analyze.publishedDetail', {publishId: id});
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
