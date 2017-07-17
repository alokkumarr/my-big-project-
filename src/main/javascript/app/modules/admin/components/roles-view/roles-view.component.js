import template from './roles-view.component.html';
import style from './roles-view.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';
import {Subject} from 'rxjs/Subject';
import {AdminMenuData, RolesTableHeader} from '../../consts';

export const RolesViewComponent = {
  template,
  styles: [style],
  controller: class RolesViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, RolesManagementService, $window, $rootScope) {
      'ngInject';
      super($injector);
      this._$compile = $compile;
      this.$componentHandler = $componentHandler;
      this.RolesManagementService = RolesManagementService;
      this._$window = $window;
      this._$state = $state;
      this._$mdDialog = $mdDialog;
      this._$mdToast = $mdToast;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this.updater = new Subject();
      this.resp = this._JwtService.getTokenObj();
      this.custID = this.resp.ticket.custID;
      this.custCode = this.resp.ticket.custCode;
      this._rolesCache = [];
      this._$rootScope.showProgress = true;
      this.RolesManagementService.getActiveRolesList(this.custID).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this._rolesCache = this.rolesList = this.admin.roles;
          this._$rootScope.showProgress = false;
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.admin.validityMessage + '</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
      this.roleTypes = [];
    }
    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];
      leftSideNav.update(AdminMenuData, 'ADMIN');
    }
    openNewRoleModal() {
      this._$rootScope.showProgress = true;
      this.RolesManagementService.getRoleTypes().then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.showDialog({
            controller: scope => {
              scope.roleTypes = this.response.roles;
              scope.onSaveAction = roles => {
                this._rolesCache = this.rolesList = roles;
                this.applySearchFilter();
                this.updater.next({roles: this.rolesList});
              };
            },
            template: '<role-new role-types="roleTypes" on-save="onSaveAction(roles)"></role-new>',
            fullscreen: true
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

    openDeleteModal(role) {
      this.role = role;
      const tokenCustId = parseInt(this.resp.ticket.custID, 10);
      const tokenMasterLoginId = this.resp.ticket.masterLoginId;
      const roleObj = {
        roleId: role.roleSysId,
        customerId: tokenCustId,
        masterLoginId: tokenMasterLoginId
      };
      const confirm = this._$mdDialog.confirm()
        .title('Are you sure you want to delete this role?')
        .textContent('Role Name : ' + this.role.roleName)
        .ok('Delete')
        .cancel('Cancel');
      this._$mdDialog.show(confirm).then(() => {
        this._$rootScope.showProgress = true;
        return this.RolesManagementService.deleteRole(roleObj);
      }).then(data => {
        if (data.valid) {
          this._$rootScope.showProgress = false;
          this._rolesCache = this.rolesList = data.roles;
          this.applySearchFilter();
          this.updater.next({roles: this.rolesList});
          this._$mdToast.show({
            template: '<md-toast><span> Role is successfully deleted </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + data.validityMessage + '</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      });
    }

    openEditModal(role) {
      const editRole = {};
      angular.merge(editRole, role);
      this._$rootScope.showProgress = true;
      this.RolesManagementService.getRoleTypes().then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.showDialog({
            controller: scope => {
              scope.roles = this.response.roles;
              scope.role = editRole;
              scope.onUpdateAction = roles => {
                this._rolesCache = this.rolesList = roles;
                this.applySearchFilter();
                this.updater.next({roles: this.rolesList});
              };
            },
            template: '<role-edit role-types="roles" edit-role="role" on-update="onUpdateAction(roles)" ></role-edit>',
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

    onRoleAction(actionType, payload) {
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
    checkColumns(name) {
      this.headerList = [];
      this.headerList = RolesTableHeader;
      for (let i = 0; i < this.headerList.length; i++) {
        if (this.headerList[i].name === name) {
          return true;
        }
      }
      return false;
    }
    applySearchFilter() {
      this.searchText = [];
      this.searchText = this.states.searchTerm.split(/:(.*)/).slice(0, -1);
      switch (this.searchText.length) {
        case 0: {
          this.states.searchTermValue = this.states.searchTerm;
          this.rolesList = this.RolesManagementService.searchRoles(this._rolesCache, this.states.searchTerm, 'All');
          break;
        }
        case 2: {
          if (this.checkColumns(this.searchText[0].trim().toUpperCase())) {
            this.states.searchTermValue = this.searchText[1].trim();
            this.rolesList = this.RolesManagementService.searchRoles(this._rolesCache, this.searchText[1].trim(), this.searchText[0].trim().toUpperCase());
          } else {
            this.states.searchTermValue = '';
            this.userList = this.RolesManagementService.searchRoles(this._rolesCache, this.states.searchTermValue, 'All');
            this._$mdToast.show({
              template: '<md-toast><span>"' + this.searchText[0].trim() + '" - Column does not exist.</md-toast>',
              position: 'bottom left',
              toastClass: 'toast-primary'
            });
          }
          break;
        }
        default: {
          break;
        }
      }
      this.updater.next({roles: this.rolesList});
    }
  }
};
