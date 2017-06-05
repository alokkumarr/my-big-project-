import template from './users-view.component.html';
import style from './users-view.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';

export const UsersViewComponent = {
  template,
  styles: [style],
  controller: class UsersViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, UsersManagementService, $window) {
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
      this.UsersManagementService.getActiveUsersList(this.resp.ticket.custID, token).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this.userList = this.admin.users;
        } else {
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
  }
};
