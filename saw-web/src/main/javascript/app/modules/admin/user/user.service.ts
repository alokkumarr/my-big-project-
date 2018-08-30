import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import { IAdminDataService } from '../admin-data-service.interface';

type UserResponse = {
  users: any[],
  valid: boolean,
  validityMessage: string
};

type RolesResponse = {
  roles: any[],
  valid: boolean,
  validityMessage: string
};

@Injectable()
export class UserService implements IAdminDataService {

  constructor(
    private _adminService: AdminService
  ) {}

  getList(customerId) {
    return this._adminService.request<UserResponse>('users/fetch', customerId)
      .map(resp => resp.users)
      .toPromise();
  }

  save(user) {
    const options = {
      toast: { successMsg: 'User is successfully added' }
    };
    return this._adminService.request<UserResponse>('users/add', user, options)
      .map(resp => resp.valid ? resp.users : null)
      .toPromise();
  }

  remove(user) {
    const options = {
      toast: { successMsg: 'User is successfully deleted' }
    };
    return this._adminService.request<UserResponse>('users/delete', user, options)
      .map(resp => resp.valid ? resp.users : null)
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'User is successfully Updated' }
    };
    return this._adminService.request<UserResponse>('users/edit', user, options)
      .map(resp => resp.valid ? resp.users : null)
      .toPromise();
  }

  getUserRoles(customerId) {
    return this._adminService.request<RolesResponse>('roles/list', customerId)
      .map(resp => resp.roles);
  }
}
