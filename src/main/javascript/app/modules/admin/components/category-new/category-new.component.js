import get from 'lodash/get';
import template from './category-new.component.html';
import style from './category-new.component.scss';

export const CategoryNewComponent = {
  template,
  bindings: {
    onSave: '&'
  },
  styles: [style],
  controller: class CategoryNewController {
    constructor($scope, $mdDialog, CategoriesManagementService, JwtService, $rootScope, $mdToast, $componentHandler, $state) {
      'ngInject';
      this._$scope = $scope;
      this._$mdDialog = $mdDialog;
      this._CategoriesManagementService = CategoriesManagementService;
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
      this.getProducts(custId);
      this.subCategoryInd = false;
      this.category = {
        activeStatusInd: 1
      };
      this.statuses = [{
        ind: 1,
        name: 'ACTIVE'
      }, {
        ind: 0,
        name: 'INACTIVE'
      }];
    }
    getProducts(custID) {
      this._$rootScope.showProgress = true;
      this._CategoriesManagementService.getProducts(custID).then(response => {
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
      this._$rootScope.showProgress = true;
      const inputObject = {
        customerId: this.customerId,
        productId: inputProductId,
        moduleId: 0
      };
      this._CategoriesManagementService.getModules(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.modulesList = this.response.modules;
          this.category.moduleId = null;
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
      this._$rootScope.showProgress = true;
      const inputObject = {
        customerId: this.customerId,
        productId: 0,
        moduleId: inputModuleId
      };
      this._CategoriesManagementService.getParentCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.categoriesList = this.response.category;
          this.category.categoryId = null;
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
    createCategory() {
      this._$rootScope.showProgress = true;
      if (this.subCategoryInd) {
        this.category.subCategoryInd = this.subCategoryInd;
        for (let i = 0; i < this.categoriesList.length; i++) {
          if (this.category.categoryId === this.categoriesList[i].categoryId) {
            this.category.categoryType = this.categoriesList[i].categoryType;
            this.category.categoryCode = this.categoriesList[i].categoryCode;
          }
        }
      }
      this.category.customerId = this.customerId;
      this.category.masterLoginId = this.masterLoginId;
      this._CategoriesManagementService.saveCategory(this.category).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Category is successfully added </md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onSave({categories: this.response.categories});
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
