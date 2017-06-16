import template from './role-dialog.component.html';
import style from './role-dialog.component.scss';

export const RoleDialogComponent = {
  template,
  styles: [style],
  transclude: {
    header: '?roleDialogHeader',
    content: 'roleDialogContent',
    footer: '?roleDialogFooter'
  },
  bindings: {
    control: '<'
  },
  controller: class RoleDialogCtrl {
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
