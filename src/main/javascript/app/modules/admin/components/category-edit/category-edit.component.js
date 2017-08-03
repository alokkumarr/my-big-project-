import get from 'lodash/get';
import template from './category-edit.component.html';
import style from './category-edit.component.scss';

export const CategoryEditComponent = {
  template,
  bindings: {
    editCategory: '<',
    onUpdate: '&'
  },
  styles: [style],
  controller: class CategoryEditController {
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
      this.getModules(this.editCategory.productId, 0);
      this.newSubCategoriesList = [];
      if (this.editCategory.subCategories.length > 0) {
        this.changeSelectedSubCategory(this.editCategory.subCategories[0]);
        this.subCategoryFlag = true;
      }
      this.newCategoryName = this.editCategory.categoryName;
      this.statuses = [{
        ind: 1,
        name: 'ACTIVE'
      }, {
        ind: 0,
        name: 'INACTIVE'
      }];
    }
    changeSelectedSubCategory(subCategory) {
      this.preSelectedSubCategory = {};
      angular.merge(this.preSelectedSubCategory, subCategory);
      if (this.newSubCategoriesList.length < 1) {
        this.newSubCategoriesList.push(this.preSelectedSubCategory);
        this.selectedSubCategory = this.newSubCategoriesList[0];
      } else {
        this.index = 0;
        this.endFlag = 0;
        for (let i = 0; i < this.newSubCategoriesList.length; i++) {
          if (this.newSubCategoriesList[i].subCategoryId === this.preSelectedSubCategory.subCategoryId) {
            this.endFlag = 1;
            this.selectedSubCategory = this.newSubCategoriesList[i];
            break;
          }
        }
        if (this.endFlag === 0) {
          this.newSubCategoriesList.push(this.preSelectedSubCategory);
          this.selectedSubCategory = this.newSubCategoriesList[this.newSubCategoriesList.length - 1];
        }
      }
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
    getModules(inputProductId, flag) {
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
          if (flag === 1) {
            this.editCategory.moduleId = null;
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
    updateCategory() {
      this._$rootScope.showProgress = true;
      if (this.newCategoryName === this.editCategory.categoryName) {
        this.editCategory.iscatNameChanged = false;
        this.editCategory.categoryName = this.newCategoryName;
      } else {
        this.editCategory.iscatNameChanged = true;
        this.editCategory.categoryName = this.newCategoryName;
      }
      this.editCategory.masterLoginId = this.masterLoginId;
      this.finalUpdatedCategory = {};
      this.finalUpdatedSubCategoryList = [];
      angular.merge(this.finalUpdatedCategory, this.editCategory);
      for (let i = 0; i < this.newSubCategoriesList.length; i++) {
        for (let j = 0; j < this.editCategory.subCategories.length; j++) {
          if (this.newSubCategoriesList[i].subCategoryId === this.editCategory.subCategories[j].subCategoryId) {
            if ((this.newSubCategoriesList[i].subCategoryName !== this.editCategory.subCategories[j].subCategoryName) || (this.newSubCategoriesList[i].subCategoryDesc !== this.editCategory.subCategories[j].subCategoryDesc) || (this.newSubCategoriesList[i].activestatusInd !== this.editCategory.subCategories[j].activestatusInd)) {
              this.finalUpdatedSubCategoryList.push(this.newSubCategoriesList[i]);
            }
          }
        }
      }
      this.finalUpdatedCategory.subCategories = this.finalUpdatedSubCategoryList;
      this._CategoriesManagementService.updateCategory(this.finalUpdatedCategory).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this._$mdToast.show({
            template: '<md-toast><span> Category updated successfully.</md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onUpdate({categories: this.response.categories});
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
