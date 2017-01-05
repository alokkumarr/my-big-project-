import template from './jsPlumbJoinLabel.component.html';
import style from './jsPlumbJoinLabel.component.scss';

export const JSPlumbJoinLabel = {
  template,
  styles: [style],
  bindings: {
    model: '<'
  },
  controller: class JSPlumbJoinLabelCtrl {
    constructor($scope, $mdDialog) {
      'ngInject';

      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
    }

    $onInit() {
      this._connector = this.model.connector;
    }

    getRelationType() {
      return this._connector.model.type.toLowerCase();
    }

    getRelationName() {
      const type = this.getRelationType();

      return type.charAt(0).toUpperCase() + type.slice(1);
    }

    getRelationIcon() {
      return `icon-${this.getRelationType()}-join`;
    }

    onClick(ev) {
      const scope = this._$scope.$new();

      scope.model = {
        connector: this._connector
      };

      this._$mdDialog
        .show({
          template: '<js-plumb-join-dialog model="model"></js-plumb-join-dialog>',
          targetEvent: ev,
          fullscreen: true,
          skipHide: true,
          scope: scope
        });
    }
  }
};
