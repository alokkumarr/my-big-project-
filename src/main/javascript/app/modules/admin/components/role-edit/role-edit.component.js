import template from './role-edit.component.html';
import style from './role-edit.component.scss';
import get from 'lodash/get';

export const RoleEditComponent = {
  template,
  bindings: {
    roleTypes: '<',
    editRole: '<',
    onUpdate: '&'
  },
  styles: [style],
  controller: class RoleEditController {
    constructor($scope, $mdDialog, RolesManagementService, JwtService, $rootScope, $mdToast, $componentHandler, $state) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._RolesManagementService = RolesManagementService;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this._$mdToast = $mdToast;
      this._$componentHandler = $componentHandler;
      this._$state = $state;
    }

    $onInit() {
      this.statuses = [{
        ind: 1,
        name: 'ACTIVE'
      }, {
        ind: 0,
        name: 'INACTIVE'
      }];
      const custCode = get(this._JwtService.getTokenObj(), 'ticket.custCode');
      const userId = get(this._JwtService.getTokenObj(), 'ticket.masterLoginId');
      this.editRole.masterLoginId = userId;
      this.editRole.customerCode = custCode;
      if (this.editRole.activeStatusInd === 'Active') {
        this.editRole.activeStatusInd = 1;
      } else {
        this.editRole.activeStatusInd = 0;
      }
    }

    updateRole() {
      this._$rootScope.showProgress = true;
      const eRole = this.editRole;
      this._RolesManagementService.updateRole(eRole).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Role is successfully Updated </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onUpdate({roles: this.response.roles});
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.response.validityMessage + '</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }
  }
};
