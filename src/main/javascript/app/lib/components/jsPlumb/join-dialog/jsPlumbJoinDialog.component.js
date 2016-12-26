import template from './jsPlumbJoinDialog.component.html';
import style from './jsPlumbJoinDialog.component.scss';

export const JSPlumbJoinDialog = {
  template,
  styles: [style],
  bindings: {
    metadata: '<'
  },
  controller: class JSPlumbJoinDialogCtrl {
    constructor($mdDialog) {
      'ngInject';

      this.$mdDialog = $mdDialog;
    }

    $postLink() {
    }

    isOfType(joinType) {
      return this.metadata.joinType === joinType;
    }

    setType(joinType) {
      this.metadata.joinType = joinType;
    }

    save() {
      this.$mdDialog.hide({
        type: 'save',
        metadata: this.metadata
      });
    }

    cancel() {
      this.$mdDialog.hide();
    }

    removeLink() {
      this.$mdDialog.hide({
        type: 'removeLink'
      });
    }
  }
};
