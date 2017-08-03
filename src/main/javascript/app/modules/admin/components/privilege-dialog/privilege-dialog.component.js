import template from './privilege-dialog.component.html';
import style from './privilege-dialog.component.scss';

export const PrivilegeDialogComponent = {
  template,
  styles: [style],
  transclude: {
    header: '?privilegeDialogHeader',
    content: 'privilegeDialogContent',
    footer: '?privilegeDialogFooter'
  },
  bindings: {
    control: '<'
  },
  controller: class PrivilegeDialogCtrl {
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
