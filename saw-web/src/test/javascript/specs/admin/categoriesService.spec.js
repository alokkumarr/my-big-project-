import 'core-js';
import {AdminModule} from '../../../../main/javascript/app/modules/admin';
import {customerId, inputObject} from './categoriesData';

describe('CategoriesManagementService', () => {
  let CategoriesManagementService;

  beforeEach(() => {
    angular.mock.module(AdminModule);
    angular.mock.inject($injector => {
      CategoriesManagementService = $injector.get('CategoriesManagementService');
    });
  });

  it('CategoriesManagementService exists', () => {
    expect(CategoriesManagementService).to.be.an('object');
  });

  describe('getActiveCategoriesList(customerId)', () => {
    it('get All Categories List', () => {
      const responseData = CategoriesManagementService.getActiveCategoriesList(customerId);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getProducts(customerId)', () => {
    it('get Products List', () => {
      const responseData = CategoriesManagementService.getProducts(customerId);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getModules(inputObject)', () => {
    it('get Modules List', () => {
      const responseData = CategoriesManagementService.getModules(inputObject);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getParentCategories(inputObject)', () => {
    it('get Only Parent-Categories List', () => {
      const responseData = CategoriesManagementService.getParentCategories(inputObject);
      expect(responseData).to.be.an('object');
    });
  });
});
