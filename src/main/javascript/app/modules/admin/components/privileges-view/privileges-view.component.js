import * as template from './privileges-view.component.html';
import style from './privileges-view.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';
import {Subject} from 'rxjs/Subject';
import {AdminMenuData, PrivilegesTableHeader} from '../../consts';

const SEARCH_CONFIG = [
  {keyword: 'PRODUCT', fieldName: 'productName'},
  {keyword: 'MODULE', fieldName: 'moduleName'},
  {keyword: 'CATEGORY', fieldName: 'categoryName'},
  {keyword: 'ROLE', fieldName: 'roleName'},
  {keyword: 'PRIVILEGE DESC', fieldName: 'privilegeDesc'}
];

export const PrivilegesViewComponent = {
  template,
  styles: [style],
  controller: class PrivilegesViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, PrivilegesManagementService, $window, $rootScope, LocalSearchService) {
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
      this._LocalSearchService = LocalSearchService;
      this.updater = new Subject();
      this.resp = this._JwtService.getTokenObj();
      this.custID = this.resp.ticket.custID;
      this.custCode = this.resp.ticket.custCode;
      this._privilegesCache = [];
      this.states = {};
      this._$rootScope.showProgress = true;
      this.PrivilegesManagementService.getActivePrivilegesList(this.custID).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this._privilegesCache = this.privilegeList = this.admin.privileges;
          this.states.searchTerm = this._$state.params.role ? `role:"${this._$state.params.role}"` : '';
          /* eslint-disable */
          this.states.searchTerm && this.applySearchFilter();
          /* eslint-enable */
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
      const searchCriteria = this._LocalSearchService.parseSearchTerm(this.states.searchTerm);
      this.states.searchTermValue = searchCriteria.trimmedTerm;

      this._LocalSearchService.doSearch(searchCriteria, this._privilegesCache, SEARCH_CONFIG).then(data => {
        this.privilegeList = data;
        this.updater.next({privileges: this.privilegeList});
      }, err => {
        this._$mdToast.show({
          template: `<md-toast><span>${err.message}</span></md-toast>`,
          position: 'bottom left',
          toastClass: 'toast-primary'
        });
      });
    }
  }
};
