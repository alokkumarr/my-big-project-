import * as get from 'lodash/get';
import * as template from './privilege-edit.component.html';
import style from './privilege-edit.component.scss';

export const PrivilegeEditComponent = {
  template,
  bindings: {
    editPrivilege: '<',
    onUpdate: '&'
  },
  styles: [style],
  controller: class PrivilegeEditController {
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
      this.getModules(this.editPrivilege.productId, 0);
      this.getCategories(this.editPrivilege.moduleId, 0);
      this.privilegeCodeStringInitArray = [];
      this.privilegeCodeStringInit = this.dec2bin(this.editPrivilege.privilegeCode);
      this.privilegeCodeStringInitArray = this.privilegeCodeStringInit.split('');
      this.privilegeListCodeName = ['Access/View', 'Create', 'Execute', 'Publish', 'Fork', 'Edit', 'Export', 'Delete', 'All', '', '', '', '', '', '', ''];
      this.privilegeListCode = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];
      this.privilegeListCodeLength = this.privilegeListCode.length - 1;
      for (let i = this.privilegeCodeStringInitArray.length - 1; i > -1; i--) {
        if (this.privilegeCodeStringInitArray[i] === '0') {
          this.privilegeListCode[this.privilegeListCodeLength] = false;
        } else {
          this.privilegeListCode[this.privilegeListCodeLength] = true;
        }
        --this.privilegeListCodeLength;
      }
    }
    dec2bin(dec) {
      return (dec >>> 0).toString(2);
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
    getModules(inputProductId, flag) {
      const inputObject = {
        customerId: this.customerId,
        productId: inputProductId,
        moduleId: 0
      };
      this._PrivilegesManagementService.getModules(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.modulesList = this.response.modules;
          if (flag === 1) {
            this.editPrivilege.moduleId = null;
          }
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
    getCategories(inputModuleId, flag) {
      const inputObject = {
        customerId: this.customerId,
        productId: 0,
        moduleId: inputModuleId
      };
      this._PrivilegesManagementService.getCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.categoriesList = this.response.category;
          if (flag === 1) {
            this.editPrivilege.categoryId = null;
          }
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
    checkAndUpdatePrivilege() {
      this.flag = 0;
      for (let i = 0; i < this.privilegeListCode.length; i++) {
        if (this.privilegeListCode[i] === true) {
          this.flag = 1;
        }
      }
      if (this.flag === 1) {
        this.updatePrivilege();
      } else {
        this._$mdToast.show({
          template: '<md-toast><span>Please select atleast one privilege.</md-toast>',
          position: 'bottom left',
          toastClass: 'toast-primary'
        });
      }
    }
    updatePrivilege() {
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
      const privilegeCode = parseInt(this.privilegeCodeString, 2);
      this.editPrivilege.privilegeDesc = this.privilegeCodeDesc;
      this.editPrivilege.privilegeCode = privilegeCode;
      this.editPrivilege.customerId = this.customerId;
      this.editPrivilege.masterLoginId = this.masterLoginId;
      for (let i = 0; i < this.categoriesList.length; i++) {
        if (this.editPrivilege.categoryId === this.categoriesList[i].categoryId) {
          this.editPrivilege.categoryType = this.categoriesList[i].categoryType;
        }
      }
      this._PrivilegesManagementService.updatePrivilege(this.editPrivilege).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Privilege is successfully updated </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onUpdate({privileges: this.response.privileges});
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
