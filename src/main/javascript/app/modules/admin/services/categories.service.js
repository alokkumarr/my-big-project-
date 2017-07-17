import fpGet from 'lodash/fp/get';
import filter from 'lodash/filter';

export function CategoriesManagementService($http, AppConfig) {
  'ngInject';
  const loginUrl = AppConfig.login.url;
  return {
    getActiveCategoriesList,
    getProducts,
    getModules,
    getParentCategories,
    saveCategory,
    deleteSubCategories,
    deleteCategories,
    updateCategory,
    searchCategories
  };
  function getActiveCategoriesList(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/fetch`, customerId).then(fpGet('data'));
  }
  function getProducts(customerId) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/products/list`, customerId).then(fpGet('data'));
  }
  function getModules(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/modules/list`, inputObject).then(fpGet('data'));
  }
  function getParentCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/parent/list`, inputObject).then(fpGet('data'));
  }
  function saveCategory(category) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/add`, category).then(fpGet('data'));
  }
  function deleteSubCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/subcategories/delete`, inputObject).then(fpGet('data'));
  }
  function deleteCategories(inputObject) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/delete`, inputObject).then(fpGet('data'));
  }
  function updateCategory(category) {
    return $http.post(`${loginUrl}/auth/admin/cust/manage/categories/edit`, category).then(fpGet('data'));
  }
  function searchCategories(categories, searchTerm = '', header) {
    if (!searchTerm) {
      return categories;
    }
    const term = searchTerm.toUpperCase();
    const matchIn = item => {
      return (item || '').toUpperCase().indexOf(term) !== -1;
    };
    const matchInList = list => {
      for (let i = 0; i < list.length; i++) {
        if ((list[i].subCategoryName || '').toUpperCase().indexOf(term) !== -1) {
          return true;
        }
      }
      return false;
    };
    return filter(categories, item => {
      switch (header) {
        default: {
          return matchIn(item.productName) ||
            matchIn(item.moduleName) ||
            matchIn(item.categoryName) ||
            matchInList(item.subCategories);
        }
        case 'PRODUCT': {
          return matchIn(item.productName);
        }
        case 'MODULE': {
          return matchIn(item.moduleName);
        }
        case 'CATEGORY': {
          return matchIn(item.categoryName);
        }
        case 'SUB CATEGORIES': {
          return matchInList(item.subCategories);
        }
      }
    });
  }
}
