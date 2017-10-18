import {AdminModule} from '../../../../main/javascript/app/modules/admin';
import {customerId, inputObject} from './privilegesData';

describe('PrivilegesManagementService', () => {
  let PrivilegesManagementService;

  beforeEach(() => {
    angular.mock.module(AdminModule);
    angular.mock.inject($injector => {
      PrivilegesManagementService = $injector.get('PrivilegesManagementService');
    });
  });

  it('PrivilegesManagementService exists', () => {
    expect(PrivilegesManagementService).to.be.an('object');
  });

  describe('getActivePrivilegesList(customerId)', () => {
    it('get Privileges List', () => {
      const responseData = PrivilegesManagementService.getActivePrivilegesList(customerId);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getRoles(customerId)', () => {
    it('get Roles List', () => {
      const responseData = PrivilegesManagementService.getRoles(customerId);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getProducts(customerId)', () => {
    it('get Products List', () => {
      const responseData = PrivilegesManagementService.getProducts(customerId);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getModules(inputObject)', () => {
    it('get Modules List', () => {
      const responseData = PrivilegesManagementService.getModules(inputObject);
      expect(responseData).to.be.an('object');
    });
  });
  describe('getCategories(inputObject)', () => {
    it('get Categories List', () => {
      const responseData = PrivilegesManagementService.getCategories(inputObject);
      expect(responseData).to.be.an('object');
    });
  });
});
