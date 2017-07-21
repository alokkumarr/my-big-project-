import defaultsDeep from 'lodash/defaultsDeep';

export default class AbstractDesignerComponentController {
  constructor($mdDialog) {
    this._$mdDialog = $mdDialog;

    this.draftMode = false;
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

  showDialog(config) {
    config = defaultsDeep(config, {
      controllerAs: '$ctrl',
      multiple: false,
      autoWrap: false,
      focusOnOpen: false,
      clickOutsideToClose: true,
      fullscreen: false
    });

    return this._$mdDialog.show(config);
  }
}
