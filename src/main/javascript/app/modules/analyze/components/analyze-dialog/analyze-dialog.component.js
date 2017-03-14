import template from './analyze-dialog.component.html';
import style from './analyze-dialog.component.scss';

export const AnalyzeDialogComponent = {
  template,
  styles: [style],
  transclude: {
    header: '?analyzeDialogHeader',
    content: 'analyzeDialogContent',
    footer: '?analyzeDialogFooter'
  },
  bindings: {
    control: '<'
  },
  controller: class AnalyzeDialogCtrl {
    constructor($mdDialog, $transclude) {
      'ngInject';

      this.$mdDialog = $mdDialog;
      this.$transclude = $transclude;

      this.states = {
        loader: false
      };
    }

    $onInit() {
      if (this.control) {
        this.control.$dialog = this;
      }
    }

    isTranscludePresent(name) {
      return this.$transclude.isSlotFilled(name);
    }

    showLoader() {
      this.states.loader = true;
    }

    hideLoader() {
      this.states.loader = false;
    }

    closeDialog() {
      this.$mdDialog.hide();
    }
  }
};
