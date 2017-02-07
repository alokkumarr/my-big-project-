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
      const tpl = '<js-plumb-join-dialog model="$ctrl.model"></js-plumb-join-dialog>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.$ctrl.model = {
              connector: this._connector
            };
          },
          controllerAs: '$ctrl',
          targetEvent: ev,
          fullscreen: true,
          multiple: true
        });
    }
  }
};
