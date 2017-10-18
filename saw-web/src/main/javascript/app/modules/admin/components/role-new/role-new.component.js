import * as get from 'lodash/get';
import * as template from './role-new.component.html';
import style from './role-new.component.scss';

export const RoleNewComponent = {
  template,
  bindings: {
    roleTypes: '<',
    onSave: '&'
  },
  styles: [style],
  controller: class RoleNewController {
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
      const custId = parseInt(get(this._JwtService.getTokenObj(), 'ticket.custID'), 10);
      const custCode = get(this._JwtService.getTokenObj(), 'ticket.custCode');
      const userId = get(this._JwtService.getTokenObj(), 'ticket.masterLoginId');
      this.role = {
        activeStatusInd: 1,
        custSysId: custId,
        customerCode: custCode,
        masterLoginId: userId,
        myAnalysis: true
      };
      this.statuses = [{
        ind: 1,
        name: 'ACTIVE'
      }, {
        ind: 0,
        name: 'INACTIVE'
      }];
    }
    createRole() {
      this._$rootScope.showProgress = true;
      this._RolesManagementService.saveRole(this.role).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Role is successfully added </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onSave({roles: this.response.roles});
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
