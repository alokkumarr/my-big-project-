import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import { IAdminDataService } from '../admin-data-service.interface';
import { map } from 'rxjs/operators';

interface RolesResponse {
  roles: any[];
  valid: boolean;
  validityMessage: string;
}

@Injectable()
export class RoleService implements IAdminDataService {
  constructor(public _adminService: AdminService) {}

  getList(customerId) {
    return this._adminService
      .request<RolesResponse>('roles/fetch', customerId)
      .pipe(map(resp => resp.roles))
      .toPromise();
  }

  save(user) {
    const options = {
      toast: { successMsg: 'Role is successfully added' }
    };
    return this._adminService
      .request<RolesResponse>('roles/add', user, options)
      .pipe(map(resp => (resp.valid ? resp.roles : null)))
      .toPromise();
  }

  remove(user) {
    const options = {
      toast: { successMsg: 'Role is successfully deleted' }
    };
    return this._adminService
      .request<RolesResponse>('roles/delete', user, options)
      .pipe(map(resp => (resp.valid ? resp.roles : null)))
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'Role is successfully Updated' }
    };
    return this._adminService
      .request<RolesResponse>('roles/edit', user, options)
      .pipe(map(resp => (resp.valid ? resp.roles : null)))
      .toPromise();
  }

  getRoleTypes(customerId) {
    return this._adminService
      .request<RolesResponse>('roles/types/list', customerId)
      .pipe(map(resp => resp.roles));
  }
}
