export function RenameDialogController($scope, $mdDialog) {
  'ngInject';

    $scope.model = {};

    $scope.cancel = () => {
      $mdDialog.cancel();
    };

    $scope.rename = newName => {
      $mdDialog.hide(newName);
    };
  }
