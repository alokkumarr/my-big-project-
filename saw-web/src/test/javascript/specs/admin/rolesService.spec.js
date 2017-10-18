import {AdminModule} from '../../../../main/javascript/app/modules/admin';
import {customerId} from './rolesData';

describe('RolesManagementService', () => {
  let RolesManagementService;

  beforeEach(() => {
    angular.mock.module(AdminModule);
    angular.mock.inject($injector => {
      RolesManagementService = $injector.get('RolesManagementService');
    });
  });

  it('RolesManagementService exists', () => {
    expect(RolesManagementService).to.be.an('object');
  });

  describe('getRoleTypes()', () => {
    it('get Role Types List', () => {
      const responseData = RolesManagementService.getRoleTypes();
      expect(responseData).to.be.an('object');
    });
  });

  describe('getActiveRolesList(customerId)', () => {
    it('get Roles List', () => {
      const responseData = RolesManagementService.getActiveRolesList(customerId);
      expect(responseData).to.be.an('object');
    });
  });
});
