export class DialogController {
  constructor($scope, $mdDialog) {
    'ngInject';
    this.$mdDialog = $mdDialog;
  }
  hide() {
    this.$mdDialog.hide();
  }

  cancel() {
    this.$mdDialog.cancel();
  }

  answer(answer) {
    this.$mdDialog.hide(answer);
  }
}
