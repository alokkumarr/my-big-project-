import * as get from 'lodash/get';
import * as template from './user-new.component.html';

import style from './user-new.component.scss';

export const UserNewComponent = {
  template,
  bindings: {
    roles: '<',
    onSave: '&'
  },
  styles: [style],
  controller: class UserNewController {
    constructor($scope, $mdDialog, UsersManagementService, JwtService, $rootScope, $mdToast, $componentHandler, $state) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._UsersManagementService = UsersManagementService;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this._$mdToast = $mdToast;
      this._$componentHandler = $componentHandler;
      this._$state = $state;
    }

    $onInit() {
      const custId = parseInt(get(this._JwtService.getTokenObj(), 'ticket.custID'), 10);
      this.user = {
        activeStatusInd: 1,
        customerId: custId,
        masterLoginId: '',
        firstName: '',
        lastName: ''
      };
      this.statuses = [{
        ind: 1,
        name: 'ACTIVE'
      }, {
        ind: 0,
        name: 'INACTIVE'
      }];
    }

    addToUserId(firstName, lastName) {
      this.user.masterLoginId = firstName + '.' + lastName;
    }
    createUser() {
      this._$rootScope.showProgress = true;
      this._UsersManagementService.saveUser(this.user).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> User is successfully added </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onSave({users: this.response.users});
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