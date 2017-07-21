import defaultsDeep from 'lodash/defaultsDeep';

export default class AbstractDesignerComponentController {
  constructor($mdDialog) {
    this._$mdDialog = $mdDialog;

    this.draftMode = false;
    this.showProgress = false;
  }

  $onInit() {
  }

  $onDestroy() {
  }

  startDraftMode() {
    this.draftMode = true;
  }

  endDraftMode() {
    this.draftMode = false;
  }

  startProgress() {
    this.showProgress = true;
  }

  endProgress() {
    this.showProgress = false;
  }

  openDescriptionModal(ev, model) {
    const tpl = '<analyze-description-dialog model="model" on-save="onSave($data)"></analyze-description-dialog>';

    this.showModal({
      template: tpl,
      controller: scope => {
        scope.model = {
          description: model.description
        };

        scope.onSave = data => {
          this.startDraftMode();
          model.description = data.description;
        };
      }
    }, ev);
  }

  showModal(config, ev) {
    config = defaultsDeep(config, {
      multiple: true,
      autoWrap: false,
      focusOnOpen: false,
      targetEvent: ev,
      clickOutsideToClose: true
    });

    return this._$mdDialog.show(config);
  }
}
