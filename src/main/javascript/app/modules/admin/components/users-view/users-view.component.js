import template from './users-view.component.html';
import style from './users-view.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const UsersViewComponent = {
  template,
  styles: [style],
  controller: class UsersViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, UsersManagementService, $window, $rootScope) {
      'ngInject';
      super($injector);
      this._$compile = $compile;
      this.$componentHandler = $componentHandler;
      this.UsersManagementService = UsersManagementService;
      this._$window = $window;
      this._$state = $state;
      this._$mdDialog = $mdDialog;
      this._$mdToast = $mdToast;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this.admin = {};
      this.states = {
        searchTerm: ''
      };
      const token = this._JwtService.get();
      if (!token) {
        $window.location.assign('/login.html');
        return;
      }
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace('-', '+').replace('_', '/');
      this.resp = angular.fromJson(this._$window.atob(base64));
      this._$rootScope.showProgress = true;
      this.UsersManagementService.getActiveUsersList(this.resp.ticket.custID, token).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this.userList = this.admin.users;
          this._$rootScope.showProgress = false;
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.admin.validityMessage + '</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      });
      this.custCode = this.resp.ticket.custCode;
    }
    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];
      const menuData = [{
        id: '',
        name: 'User'
      }, {
        id: '',
        name: 'Role'
      }, {
        id: '',
        name: 'Privilege'
      }];
      leftSideNav.update(menuData, 'ADMIN');
    }
    openNewUserModal() {
      this._$rootScope.showProgress = true;
      this.UsersManagementService.getRoles(this.resp.ticket.custID, this._JwtService.get()).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.showDialog({
            controller: scope => {
              scope.roles = this.response.roles;
              scope.userList = this.userList;
            },
            template: '<user-new roles="roles" user-list="userList"></user-new>',
            fullscreen: false
          });
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

    openDeleteModal(user) {
      this.user = user;
      const token = this._JwtService.get();
      const tokenCustId = this.resp.ticket.custID;
      const tokenMasterLoginId = this.resp.ticket.masterLoginId;
      const userObj = {
        userId: user.userId,
        customerId: tokenCustId,
        masterLoginId: tokenMasterLoginId
      };
      const confirm = this._$mdDialog.confirm()
        .title('Are you sure you want to delete this user?')
        .textContent('User ID :' + this.user.masterLoginId)
        .ok('Delete')
        .cancel('Cancel');

      this._$mdDialog.show(confirm).then(() => {
        this._$rootScope.showProgress = true;
        return this.UsersManagementService.deleteUser(userObj, token);
      }).then(data => {
        if (data.valid) {
          this._$rootScope.showProgress = false;
          this.userList = data.users;
          this._$mdToast.show({
            template: '<md-toast><span> User is successfully inactivated </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
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

    openEditModal(user) {
      this.editUser = user;
      this._$rootScope.showProgress = true;
      this.UsersManagementService.getRoles(this.resp.ticket.custID, this._JwtService.get()).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.showDialog({
            controller: scope => {
              scope.roles = this.response.roles;
              scope.user = this.editUser;
              scope.userList = this.userList;
            },
            template: '<user-edit roles="roles" user="user" ></user-edit>',
            fullscreen: false
          });
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

    onCardAction(actionType, payload) {
      switch (actionType) {
        case 'delete':
          this.openDeleteModal(payload);
          break;
        case 'edit':
          this.openEditModal(payload);
          break;
        default:
      }
    }
  }
};
