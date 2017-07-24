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

  openPreviewModal(template, ev, model) {

    this.showModal({
      template,
      controller: scope => {
        scope.model = model;
      },
      fullscreen: true
    }, ev);
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

  openSaveModal(ev, payload) {
    const tpl = '<analyze-save-dialog model="model" on-save="onSave($data)"></analyze-save-dialog>';

    this.showModal({
      template: tpl,
      fullscreen: true,
      controller: scope => {
        scope.model = payload;

        scope.onSave = data => {
          this.model.id = data.id;
          this.model.name = data.name;
          this.model.description = data.description;
          this.model.category = data.categoryId;
        };
      }
    }, ev)
      .then(successfullySaved => {
        if (successfullySaved) {
          this.endDraftMode();
          this.$dialog.hide(successfullySaved);
        }
      });
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
