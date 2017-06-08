import template from './user-edit.component.html';
import style from './user-edit.component.scss';

export const UserEditComponent = {
  template,
  bindings: {
    roles: '<',
    user: '<',
    onUpdate: '&'
  },
  styles: [style],
  controller: class UserNewController {
    constructor($scope, $mdDialog, UsersManagementService, JwtService, $rootScope, $mdToast, $componentHandler) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._UsersManagementService = UsersManagementService;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this._$mdToast = $mdToast;
      this._$componentHandler = $componentHandler;
      this.editUser = this.user;
    }

    $onInit() {
      this.statuses = [{
        ind: 'Active',
        name: 'ACTIVE'
      }, {
        ind: 'Inactive',
        name: 'INACTIVE'
      }];
    }

    updateUser() {
      this._$rootScope.showProgress = true;
      const eUser = this.editUser;
      if (this.editUser.activeStatusInd === 'Active') {
        eUser.activeStatusInd = 1;
      } else {
        eUser.activeStatusInd = 0;
      }
      this._UsersManagementService.updateUser(eUser, this._JwtService.get()).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> User is successfully Updated </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onUpdate({users: this.response.users});
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.response.validityMessage + '</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      });
    }
  }
};
