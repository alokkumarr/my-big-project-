import template from './jsPlumbJoinDialog.component.html';
import style from './jsPlumbJoinDialog.component.scss';

export const JSPlumbJoinDialog = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class JSPlumbJoinDialogCtrl {
    constructor($mdDialog) {
      'ngInject';

      this._$mdDialog = $mdDialog;
    }

    $onInit() {
      this._connector = this.model.connector;
      this.type = this._connector.model.type;
      this.leftSide = this._connector.model.leftSide;
      this.rightSide = this._connector.model.rightSide;
      this.leftSideField = this.leftSide.field;
      this.rightSideField = this.rightSide.field;
    }

    isOfType(type) {
      return this.type === type;
    }

    setType(type) {
      this.type = type;
    }

    save() {
      this._$mdDialog.hide();

      let shouldRender = false;

      this._connector.model.type = this.type;

      if (this.leftSide.field !== this.leftSideField) {
        this.leftSide.field = this.leftSideField;
        shouldRender = true;
      }

      if (this.rightSide.field !== this.rightSideField) {
        this.rightSide.field = this.rightSideField;
        shouldRender = true;
      }

      if (shouldRender) {
        this._connector.render();
      }
    }

    cancel() {
      this._$mdDialog.hide();
    }

    remove() {
      this._$mdDialog.hide();
      this._connector.detach();
    }
  }
};
