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
    $onInit() {
      const custId = parseInt(get(this._JwtService.getTokenObj(), 'ticket.custID'), 10);
      this.customerId = custId;
      const userId = get(this._JwtService.getTokenObj(), 'ticket.masterLoginId');
      this.masterLoginId = userId;
      this.getRoles(custId);
      this.getProducts(custId);
      this.getModules(this.editPrivilege.productId, 0);
      this.getCategories(this.editPrivilege.moduleId, 0);
      this.getSubCategories(this.editPrivilege.productId, this.editPrivilege.roleId, this.editPrivilege.moduleId, this.editPrivilege.categoryCode);
      this.privilegeListCodeName = ['View', 'Create', 'Execute', 'Publish', 'Fork', 'Edit', 'Export', 'Delete', 'All', '', '', '', '', '', '', ''];
      this.privilegeListCode = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];
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
      this._PrivilegesManagementService.getParentCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.categoriesList = this.response.category;
          if (flag === 1) {
            this.editPrivilege.categoryId = null;
            this.editPrivilege.categoryCode = '';
            this.subCategoriesList = [];
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
    getSubCategories(inputProductId, inputRoleId, inputModuleId, inputCategoryCode) {
      this.SCList = [];
      this.subCategoriesList = [];
      if (inputProductId > 0 && inputRoleId > 0 && inputModuleId > 0 && inputCategoryCode !== '') {
        this._$rootScope.showProgress = true;
        const inputObject = {
          customerId: this.customerId,
          roleId: inputRoleId,
          productId: inputProductId,
          moduleId: inputModuleId,
          categoryCode: inputCategoryCode
        };
        this._PrivilegesManagementService.getSubCategories(inputObject).then(response => {
          this.response = response;
          if (this.response.valid) {
            this.SCList = this.response.subCategories;
            if (this.SCList.length < 1) {
              this._$mdToast.show({
                template: '<md-toast><span> There is no Sub-Categories with Privilege</md-toast>',
                position: 'bottom left',
                toastClass: 'toast-primary'
              });
            } else {
              this.subCategoriesList = this.setUpPrivilegeForDisplay(this.SCList);
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
    }
    setUpPrivilegeForDisplay(list) {
      this.currentList = [];
      this.currentList = list;
      for (let i = 0; i < this.currentList.length; i++) {
        this.privilegeCodeStringInitArray = [];
        this.privilegeListCode = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];
        this.privilegeValue = this.currentList[i].privilegeCode;
        if (this.privilegeValue > 0) {
          if (this.privilegeValue === 128) {
            this.privilegeListCode = [true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false];
          } else {
            this.privilegeCodeStringInit = this.dec2bin(this.privilegeValue);
            this.privilegeCodeStringInitArray = this.privilegeCodeStringInit.split('');
            this.privilegeListCodeLength = this.privilegeListCode.length - 1;
            for (let i = this.privilegeCodeStringInitArray.length - 1; i > -1; i--) {
              this.privilegeListCode[this.privilegeListCodeLength] = this.privilegeCodeStringInitArray[i] !== '0';
              --this.privilegeListCodeLength;
            }
          }
        }
        this.currentList[i].privilegeCodeList = this.privilegeListCode;
      }
      return this.currentList;
    }
    setUpPrivilegeForSend(list) {
      this.sendList = [];
      this.returnList = [];
      angular.merge(this.sendList, list);
      for (let i = 0; i < this.sendList.length; i++) {
        this.newObject = {
          privilegeCode: 0,
          privilegeDesc: 'No Access',
          subCategoryId: 0,
          privilegeId: 0
        };
        this.changeNewObjectFlag = 0;
        this.desc = '';
        this.codeString = '';
        this.codeInteger = 0;
        if (this.sendList[i].privilegeCodeList[0] === false) {
          this.sendList[i].privilegeCodeList = [false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false];
        }
        if (this.sendList[i].privilegeCodeList[8] === true) {
          this.sendList[i].privilegeCodeList = [false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, false];
        }
        this.k = 0;
        for (let j = 0; j < this.sendList[i].privilegeCodeList.length; j++) {
          this.code = '0';
          if (this.sendList[i].privilegeCodeList[j] === true) {
            this.changeNewObjectFlag = 1;
            this.code = '1';
            if (this.k === 0) {
              this.desc = this.privilegeListCodeName[j];
              this.k = 1;
            } else {
              this.desc = this.desc + ',' + this.privilegeListCodeName[j];
            }
          }
          this.codeString = this.codeString + this.code;
        }
        if (this.changeNewObjectFlag === 0) {
          this.newObject.subCategoryId = this.sendList[i].subCategoryId;
          this.newObject.privilegeId = this.sendList[i].privilegeId;
          this.returnList.push(this.newObject);
        } else {
          this.codeInteger = parseInt(this.codeString, 2);
          this.newObject.privilegeCode = this.codeInteger;
          this.newObject.privilegeDesc = this.desc;
          this.newObject.subCategoryId = this.sendList[i].subCategoryId;
          this.newObject.privilegeId = this.sendList[i].privilegeId;
          this.returnList.push(this.newObject);
        }
      }
      return this.returnList;
    }
    modifyAllPrivilege(index, list) {
      this.allPrivilegeList = [];
      this.allPrivilegeList = list;
      for (let i = 0; i < this.allPrivilegeList.length; i++) {
        if (i === index) {
          if (this.allPrivilegeList[i].privilegeCodeList[8] === true) {
            this.allPrivilegeList[i].privilegeCodeList = [true, true, true, true, true, true, true, true, true, false, false, false, false, false, false, false];
          }
        }
      }
      this.subCategoriesList = this.allPrivilegeList;
    }
    updatePrivilege() {
      this._$rootScope.showProgress = true;
      this.finalPrivilege = [];
      this.finalPrivilege = this.setUpPrivilegeForSend(this.subCategoriesList);
      this.editPrivilege.subCategoriesPrivilege = this.finalPrivilege;
      this.editPrivilege.customerId = this.customerId;
      this.editPrivilege.masterLoginId = this.masterLoginId;
      for (let i = 0; i < this.categoriesList.length; i++) {
        if (this.editPrivilege.categoryCode === this.categoriesList[i].categoryCode) {
          this.editPrivilege.categoryType = this.categoriesList[i].categoryType;
          this.editPrivilege.categoryId = this.categoriesList[i].categoryId;
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
