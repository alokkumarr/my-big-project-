import template from './privileges-view.component.html';
import style from './privileges-view.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';
import {Subject} from 'rxjs/Subject';
import {AdminMenuData, PrivilegesTableHeader} from '../../consts';

export const PrivilegesViewComponent = {
  template,
  styles: [style],
  controller: class PrivilegesViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, PrivilegesManagementService, $window, $rootScope) {
      'ngInject';
      super($injector);
      this._$compile = $compile;
      this.$componentHandler = $componentHandler;
      this.PrivilegesManagementService = PrivilegesManagementService;
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
      this._privilegesCache = [];
      this._$rootScope.showProgress = true;
      this.PrivilegesManagementService.getActivePrivilegesList(this.custID).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this._privilegesCache = this.privilegeList = this.admin.privileges;
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
    openNewPrivilegeModal() {
      this.showDialog({
        controller: scope => {
          scope.onSaveAction = privileges => {
            this._privilegesCache = this.privilegeList = privileges;
            this.applySearchFilter();
            this.updater.next({privileges: this.privilegeList});
          };
        },
        template: '<privilege-new on-save="onSaveAction(privileges)"></privilege-new>',
        fullscreen: true
      });
    }
    openDeleteModal(privilege) {
      this.privilege = privilege;
      const tokenCustId = parseInt(this.resp.ticket.custID, 10);
      const tokenMasterLoginId = this.resp.ticket.masterLoginId;
      const privilegeObj = {
        privilegeId: privilege.privilegeId,
        customerId: tokenCustId,
        masterLoginId: tokenMasterLoginId
      };
      const confirm = this._$mdDialog.confirm()
        .title('Are you sure you want to delete this privilege?')
        .textContent('Privilege Details : ' + this.privilege.productName + ' --> ' + this.privilege.moduleName + ' --> ' + this.privilege.categoryName + ' --> ' + this.privilege.roleName + '.')
        .ok('Delete')
        .cancel('Cancel');
      this._$mdDialog.show(confirm).then(() => {
        this._$rootScope.showProgress = true;
        return this.PrivilegesManagementService.deletePrivilege(privilegeObj);
      }).then(data => {
        if (data.valid) {
          this._$rootScope.showProgress = false;
          this._privilegesCache = this.privilegeList = data.privileges;
          this.applySearchFilter();
          this.updater.next({privileges: this.privilegeList});
          this._$mdToast.show({
            template: '<md-toast><span> Privilege is successfully deleted </md-toast>',
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
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }

    openEditModal(privilege) {
      const editPrivilege = {};
      angular.merge(editPrivilege, privilege);
      this.showDialog({
        controller: scope => {
          scope.privilege = editPrivilege;
          scope.onUpdateAction = privileges => {
            this._privilegesCache = this.privilegeList = privileges;
            this.applySearchFilter();
            this.updater.next({privileges: this.privilegeList});
          };
        },
        template: '<privilege-edit edit-privilege="privilege" on-update="onUpdateAction(privileges)" ></privilege-edit>',
        fullscreen: false
      });
    }

    onPrivilegeAction(actionType, payload) {
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
      this.headerList = PrivilegesTableHeader;
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
          this.privilegeList = this.PrivilegesManagementService.searchPrivileges(this._privilegesCache, this.states.searchTerm, 'All');
          break;
        }
        case 2: {
          if (this.checkColumns(this.searchText[0].trim().toUpperCase())) {
            this.states.searchTermValue = this.searchText[1].trim();
            this.privilegeList = this.PrivilegesManagementService.searchPrivileges(this._privilegesCache, this.searchText[1].trim(), this.searchText[0].trim().toUpperCase());
          } else {
            this.states.searchTermValue = '';
            this.privilegeList = this.PrivilegesManagementService.searchPrivileges(this._privilegesCache, this.states.searchTermValue, 'All');
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
      this.updater.next({privileges: this.privilegeList});
    }
  }
};
