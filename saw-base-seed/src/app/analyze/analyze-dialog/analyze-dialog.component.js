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
  controller: class AnalyzeDialogCtrl {
    constructor($mdDialog, $transclude) {
      'ngInject';

      this.$mdDialog = $mdDialog;
      this.$transclude = $transclude;
    }

    $onInit() {

    }

    isTranscludePresent(name) {
      return this.$transclude.isSlotFilled(name);
    }

    closeDialog() {
      this.$mdDialog.hide();
    }
  }
};
