import get from 'lodash/get';
import template from './category-delete.component.html';
import style from './category-delete.component.scss';

export const CategoryDeleteComponent = {
  template,
  bindings: {
    deleteCategory: '<',
    onDelete: '&',
    onSearch: '&'
  },
  styles: [style],
  controller: class CategoryDeleteController {
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
    }
    doDeleteSubCategory(subCategory) {
      this._$rootScope.showProgress = true;
      const inputObject = {
        categoryId: subCategory.subCategoryId,
        customerId: this.customerId,
        masterLoginId: this.masterLoginId,
        categoryCode: this.deleteCategory.categoryCode
      };
      this._CategoriesManagementService.deleteSubCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this.deleteCategory.subCategories = this.response.subCategories;
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>Sub-category deleted successfully.</span></md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
          this.onSearch({flag: true});
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.response.validityMessage + '</span></md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }
    doDeleteCategory() {
      this._$rootScope.showProgress = true;
      const inputObject = {
        categoryId: this.deleteCategory.categoryId,
        customerId: this.customerId,
        masterLoginId: this.masterLoginId,
        categoryCode: this.deleteCategory.categoryCode
      };
      this._CategoriesManagementService.deleteCategories(inputObject).then(response => {
        this.response = response;
        if (this.response.valid) {
          this._$rootScope.showProgress = false;
          this.$dialog.hide(true);
          this.onDelete({categories: this.response.categories});
          this._$mdToast.show({
            template: '<md-toast><span>Category deleted successfully.</span></md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.response.validityMessage + '</span></md-toast>',
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
