import {OldAdminModule} from '../../../../main/javascript/app/modules/admin';
import {customerId} from './usersData';

describe('UsersManagementService', () => {
  let UsersManagementService;

  beforeEach(() => {
    angular.mock.module(OldAdminModule);
    angular.mock.inject($injector => {
      UsersManagementService = $injector.get('UsersManagementService');
    });
  });

  it('UsersManagementService exists', () => {
    expect(UsersManagementService).to.be.an('object');
  });

  describe('getRoles(customerId)', () => {
    it('get Roles List', () => {
      const responseData = UsersManagementService.getRoles(customerId);
      expect(responseData).to.be.an('object');
    });
  });

  describe('getActiveUsersList(customerId)', () => {
    it('get users List', () => {
      const responseData = UsersManagementService.getActiveUsersList(customerId);
      expect(responseData).to.be.an('object');
    });
  });
});
