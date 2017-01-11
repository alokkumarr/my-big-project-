export function DescriptionController($scope, $mdDialog, description) {
  'ngInject';

  $scope.model = {description};

  $scope.cancel = () => {
    $mdDialog.cancel();
  };

  $scope.save = newDescription => {
    $mdDialog.hide(newDescription);
  };
}
