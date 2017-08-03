import template from './user-dialog.component.html';
import style from './user-dialog.component.scss';

export const UserDialogComponent = {
  template,
  styles: [style],
  transclude: {
    header: '?userDialogHeader',
    content: 'userDialogContent',
    footer: '?userDialogFooter'
  },
  bindings: {
    control: '<'
  },
  controller: class UserDialogCtrl {
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
