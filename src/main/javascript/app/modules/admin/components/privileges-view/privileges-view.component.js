import template from './privileges-view.component.html';
import style from './privileges-view.component.scss';
import AbstractComponentController from 'app/lib/common/components/abstractComponent';
import {Subject} from 'rxjs/Subject';
import {AdminMenuData} from '../../consts';

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
      this._$rootScope.showProgress = true;
      this.PrivilegesManagementService.getActivePrivilegesList(this.custID).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this.privilegeList = this.admin.privileges;
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
            this.updater.next({privileges});
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
          this.privilegeList = data.privileges;
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
            this.updater.next({privileges});
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
  }
};
