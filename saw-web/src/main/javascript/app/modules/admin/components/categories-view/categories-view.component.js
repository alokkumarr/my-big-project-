import * as map from 'lodash/map';

import * as template from './categories-view.component.html';
import style from './categories-view.component.scss';
import AbstractComponentController from 'app/common/components/abstractComponent';
import {Subject} from 'rxjs/Subject';
import {AdminMenuData, CategoriesTableHeader} from '../../consts';

const SEARCH_CONFIG = [
  {keyword: 'PRODUCT', fieldName: 'productName'},
  {keyword: 'MODULE', fieldName: 'moduleName'},
  {keyword: 'CATEGORY', fieldName: 'categoryName'},
  {keyword: 'SUB CATEGORIES', fieldName: 'subCategories', accessor: input => map(input, sc => sc.subCategoryName)}
];

export const CategoriesViewComponent = {
  template,
  styles: [style],
  controller: class CategoriesViewPageController extends AbstractComponentController {
    constructor($componentHandler, $injector, $compile, $state, $mdDialog, $mdToast, JwtService, CategoriesManagementService, $window, $rootScope, LocalSearchService) {
      'ngInject';
      super($injector);
      this._$compile = $compile;
      this.$componentHandler = $componentHandler;
      this.CategoriesManagementService = CategoriesManagementService;
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
      this._categoriesCache = [];
      this._$rootScope.showProgress = true;
      this.CategoriesManagementService.getActiveCategoriesList(this.custID).then(admin => {
        this.admin = admin;
        if (this.admin.valid) {
          this.categoriesList = this.admin.categories;
          this._categoriesCache = this.admin.categories;
          this._$rootScope.showProgress = false;
        } else {
          this._$rootScope.showProgress = false;
          this._$mdToast.show({
            template: '<md-toast><span>' + this.admin.validityMessage + '</span></md-toast>',
            position: 'bottom left',
            toastClass: 'toast-primary'
          });
        }
      }).catch(() => {
        this._$rootScope.showProgress = false;
      });
    }
    $onInit() {
      const leftSideNav = this.$componentHandler.get('left-side-nav')[0];
      leftSideNav.update(AdminMenuData, 'ADMIN');
    }
    openNewCategoryModal() {
      this.showDialog({
        controller: scope => {
          scope.onSaveAction = categories => {
            this.categoriesList = categories;
            this._categoriesCache = categories;
            this.applySearchFilter();
          };
        },
        template: '<category-new on-save="onSaveAction(categories)"></category-new>',
        fullscreen: true
      });
    }
    openEditModal(category) {
      const editCategory = {};
      angular.merge(editCategory, category);
      this.showDialog({
        controller: scope => {
          scope.category = editCategory;
          scope.onUpdateAction = categories => {
            this.categoriesList = categories;
            this._categoriesCache = categories;
            this.applySearchFilter();
          };
        },
        template: '<category-edit edit-category="category" on-update="onUpdateAction(categories)" ></category-edit>',
        fullscreen: false
      });
    }

    openDeleteModal(category) {
      const deleteCategory = category;
      this.showDialog({
        controller: scope => {
          scope.category = deleteCategory;
          scope.onDeleteAction = categories => {
            this.categoriesList = categories;
            this._categoriesCache = categories;
            this.applySearchFilter();
          };
          scope.executeSearchFilter = flag => {
            if (flag) {
              this.applySearchFilter();
            }
          };
        },
        template: '<category-delete delete-category="category" on-delete="onDeleteAction(categories)" on-search="executeSearchFilter(flag)"></category-delete>',
        fullscreen: false
      });
    }
    onCategoryAction(actionType, payload) {
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
      this.headerList = CategoriesTableHeader;
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

      this._LocalSearchService.doSearch(searchCriteria, this._categoriesCache, SEARCH_CONFIG).then(data => {
        this.categoriesList = data;
        this.updater.next({categories: this.categoriesList});
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
