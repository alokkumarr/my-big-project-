import template from './jsPlumbJoinLabel.component.html';
import style from './jsPlumbJoinLabel.component.scss';

export const JSPlumbJoinLabel = {
  template,
  styles: [style],
  bindings: {
    model: '<',
    onChange: '&'
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
      const tpl = '<js-plumb-join-dialog model="$ctrl.model" on-change="$ctrl.joinChanged(params)"></js-plumb-join-dialog>';

      this._$mdDialog
        .show({
          template: tpl,
          controller: scope => {
            scope.$ctrl.model = {
              connector: this._connector
            };

            scope.$ctrl.joinChanged = params => {
              this.onChange({params});
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
