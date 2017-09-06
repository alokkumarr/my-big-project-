import * as template from './category-dialog.component.html';
import style from './category-dialog.component.scss';

export const CategoryDialogComponent = {
  template,
  styles: [style],
  transclude: {
    header: '?categoryDialogHeader',
    content: 'categoryDialogContent',
    footer: '?categoryDialogFooter'
  },
  bindings: {
    control: '<'
  },
  controller: class CategoryDialogCtrl {
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

    cancel(payload) {
      this.$mdDialog.cancel(payload);
    }

    hide(payload) {
      this.$mdDialog.hide(payload);
    }
  }
};
