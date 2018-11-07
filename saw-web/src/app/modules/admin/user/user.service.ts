import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import { IAdminDataService } from '../admin-data-service.interface';
import { map } from 'rxjs/operators';

interface UserResponse {
  users: any[];
  valid: boolean;
  validityMessage: string;
}

interface RolesResponse {
  roles: any[];
  valid: boolean;
  validityMessage: string;
}

@Injectable()
export class UserService implements IAdminDataService {
  constructor(public _adminService: AdminService) {}

  getList(customerId) {
    return this._adminService
      .request<UserResponse>('users/fetch', customerId)
      .pipe(map(resp => resp.users))
      .toPromise();
  }

  save(user) {
    const options = {
      toast: { successMsg: 'User is successfully added' }
    };
    return this._adminService
      .request<UserResponse>('users/add', user, options)
      .pipe(map(resp => (resp.valid ? resp.users : null)))
      .toPromise();
  }

  remove(user) {
    const options = {
      toast: { successMsg: 'User is successfully deleted' }
    };
    return this._adminService
      .request<UserResponse>('users/delete', user, options)
      .pipe(map(resp => (resp.valid ? resp.users : null)))
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'User is successfully Updated' }
    };
    return this._adminService
      .request<UserResponse>('users/edit', user, options)
      .pipe(map(resp => (resp.valid ? resp.users : null)))
      .toPromise();
  }

  getUserRoles(customerId) {
    return this._adminService
      .request<RolesResponse>('roles/list', customerId)
      .pipe(map(resp => resp.roles));
  }
}
