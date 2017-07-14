import get from 'lodash/get';
import template from './privilege-new.component.html';
import style from './privilege-new.component.scss';

export const PrivilegeNewComponent = {
  template,
  bindings: {
    onSave: '&'
  },
  styles: [style],
  controller: class PrivilegeNewController {
    constructor($scope, $mdDialog, PrivilegesManagementService, JwtService, $rootScope, $mdToast, $componentHandler, $state) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._PrivilegesManagementService = PrivilegesManagementService;
      this._JwtService = JwtService;
      this._$rootScope = $rootScope;
      this._$mdToast = $mdToast;
      this._$componentHandler = $componentHandler;
      this._$state = $state;
    }
    checkAccessPrivilege() {
      this.countFlag = 0;
      for (let i = 1; i < this.privilegeListCode.length; i++) {
        if (this.privilegeListCode[i] === true) {
          this.countFlag = 1;
        }
      }
      if (this.countFlag === 1) {
        this.privilegeListCode[0] = true;
      } else {
        this.privilegeListCode[0] = false;
      }
    }
    $onInit() {
      const custId = parseInt(get(this._JwtService.getTokenObj(), 'ticket.custID'), 10);
      this.customerId = custId;
      const userId = get(this._JwtService.getTokenObj(), 'ticket.masterLoginId');
      this.masterLoginId = userId;
      this.getRoles(custId);
      this.getProducts(custId);
      this.privilegeListCodeName = ['Access/View', 'Create', 'Execute', 'Publish', 'Fork', 'Edit', 'Export', 'Delete', 'All', '', '', '', '', '', '', ''];
      this.privilegeListCode = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];
    }
    getRoles(custID) {
      this._PrivilegesManagementService.getRoles(custID).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.rolesList = this.response.roles;
          this._$rootScope.showProgress = false;
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
    getProducts(custID) {
      this._PrivilegesManagementService.getProducts(custID).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.productsList = this.response.products;
          this._$rootScope.showProgress = false;
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
    getModules(inputProductId) {
      const inputObject = {
        customerId: this.customerId,
        productId: inputProductId,
        moduleId: 0
      };
      this._PrivilegesManagementService.getModules(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.modulesList = this.response.modules;
          this.privilege.moduleId = null;
          this._$rootScope.showProgress = false;
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
    getCategories(inputModuleId) {
      const inputObject = {
        customerId: this.customerId,
        productId: 0,
        moduleId: inputModuleId
      };
      this._PrivilegesManagementService.getCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.categoriesList = this.response.category;
          this.privilege.categoryId = null;
          this._$rootScope.showProgress = false;
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
    checkAndCreatePrivilege() {
      this.flag = 0;
      for (let i = 0; i < this.privilegeListCode.length; i++) {
        if (this.privilegeListCode[i] === true) {
          this.flag = 1;
        }
      }
      if (this.flag === 1) {
        this.createPrivilege();
      } else {
        this._$mdToast.show({
          template: '<md-toast><span>Please select atleast one privilege.</md-toast>',
          position: 'bottom left',
          toastClass: 'toast-primary'
        });
      }
    }
    createPrivilege() {
      this._$rootScope.showProgress = true;
      this.privilegeCodeString = '';
      this.privilegeCodeDesc = '';
      if (this.privilegeListCode[8] === true) {
        this.privilegeListCode = [false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false];
      }
      this.j = 0;
      for (let i = 0; i < this.privilegeListCode.length; i++) {
        this.code = '0';
        if (this.privilegeListCode[i] === true) {
          this.code = '1';
          if (this.j === 0) {
            this.privilegeCodeDesc = this.privilegeListCodeName[i];
            this.j = 1;
          } else {
            this.privilegeCodeDesc = this.privilegeCodeDesc + ',' + this.privilegeListCodeName[i];
          }
        }
        this.privilegeCodeString = this.privilegeCodeString + this.code;
      }
      for (let i = 0; i < this.categoriesList.length; i++) {
        if (this.privilege.categoryId === this.categoriesList[i].categoryId) {
          this.privilege.categoryType = this.categoriesList[i].categoryType;
        }
      }
      const privilegeCode = parseInt(this.privilegeCodeString, 2);
      this.privilege.privilegeDesc = this.privilegeCodeDesc;
      this.privilege.privilegeCode = privilegeCode;
      this.privilege.customerId = this.customerId;
      this.privilege.masterLoginId = this.masterLoginId;
      this._PrivilegesManagementService.savePrivilege(this.privilege).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Privilege is successfully added </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onSave({privileges: this.response.privileges});
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
