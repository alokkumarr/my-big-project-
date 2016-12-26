import template from './jsPlumbJoinLabel.component.html';
import style from './jsPlumbJoinLabel.component.scss';

export const JSPlumbJoinLabel = {
  template,
  styles: [style],
  bindings: {
    connection: '<'
  },
  controller: class JSPlumbJoinLabelCtrl {
    constructor($mdDialog, $scope) {
      'ngInject';

      this.$mdDialog = $mdDialog;
      this.$scope = $scope;

      this.metadata = {
        leftTable: {
          name: 'Customers',
          field: 'Customer ID',
          fields: [{
            name: 'Customer ID'
          }, {
            name: 'Customer Name'
          }, {
            name: 'Address'
          }, {
            name: 'Phone Number'
          }]
        },
        rightTable: {
          name: 'Orders',
          field: 'Customer',
          fields: [{
            name: 'Order ID'
          }, {
            name: 'Shipper'
          }, {
            name: 'Customer'
          }, {
            name: 'Total Price'
          }, {
            name: 'Warehouse'
          }, {
            name: 'Address'
          }]
        },
        joinType: this.getRelationType()
      }
    }

    $postLink() {
    }

    getRelationType() {
      return (this.connectionType || 'inner').toLowerCase();
    }

    getRelationName() {
      const type = this.getRelationType();

      return type.charAt(0).toUpperCase() + type.slice(1);
    }

    getRelationIcon() {
      return `icon-${this.getRelationType()}-join`;
    }

    onClick(ev) {
      const originalMetadata = this.metadata;
      const scope = this.$scope.$new();

      scope.metadata = angular.copy(originalMetadata);

      this.$mdDialog
        .show({
          template: '<js-plumb-join-dialog metadata="metadata"></js-plumb-join-dialog>',
          targetEvent: ev,
          fullscreen: true,
          skipHide: true,
          scope: scope
        })
        .then(ev => {
          if (!ev) {
            return;
          }

          switch (ev.type) {
            case 'removeLink':
              this.connection.detach();
              break;
            case 'save':
              const metadata = ev.metadata;

              this.connectionType = metadata.joinType;

              if (originalMetadata.leftTable.field !== metadata.leftTable.field ||
                originalMetadata.rightTable.field !== metadata.rightTable.field) {
                this.connection.connect();
              }

              break;
          }
        });
    }
  }
};
