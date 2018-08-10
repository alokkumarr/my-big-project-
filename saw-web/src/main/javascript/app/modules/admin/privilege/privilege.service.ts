import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import { IAdminDataService } from '../admin-data-service.interface';

type PrivilegeResponse = {
  privileges: any[],
  valid: boolean,
  validityMessage: string
};

type RolesResponse = {
  roles: any[],
  valid: boolean,
  validityMessage: string
};

@Injectable()
export class PrivilegeService implements IAdminDataService {

  constructor(
    private _adminService: AdminService
  ) {}

  getList(customerId) {
    return this._adminService.request<PrivilegeResponse>('privileges/fetch', customerId)
      .map(resp => resp.privileges);
  }

  save(user) {
    const options = {
      toast: { successMsg: 'Privilege is successfully added' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/upsert', user, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  remove(user) {
    const options = {
      toast: { successMsg: 'Privilege is successfully deleted' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/delete', user, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'Privilege is successfully Updated' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/upsert', user, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  getRoles(customerId) {
    return this._adminService.request<RolesResponse>('roles/roles/list', customerId)
      .map(resp => resp.roles);
  }

  getProducts(customerId) {
    return this._adminService.request<RolesResponse>('products/list', customerId)
      .map(resp => resp.products);
  }

  getModules(customerId) {
    return this._adminService.request<RolesResponse>('modules/list', customerId)
      .map(resp => resp.modules);
  }

  getCategories(customerId) {
    return this._adminService.request<RolesResponse>('categories/list', customerId)
      .map(resp => resp.categories);
  }

  getParentCategories(customerId) {
    return this._adminService.request<RolesResponse>('categories/parent/list', customerId)
      .map(resp => resp.categories);
  }

  getSubCategories(customerId) {
    return this._adminService.request<RolesResponse>('subCategoriesWithPrivilege/list', customerId)
      .map(resp => resp.categories);
  }
}
