import { Injectable } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { AdminService } from '../main-view/admin.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import {tap} from 'rxjs/operators/tap';
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

type ModulesResponse = {
  modules: any[],
  valid: boolean,
  validityMessage: string
};

type ProuctsResponse = {
  products: any[],
  valid: boolean,
  validityMessage: string
};

type CategoryResponse = {
  categories: any[],
  valid: boolean,
  validityMessage: string
};

@Injectable()
export class PrivilegeService implements IAdminDataService {

  constructor(
    private _adminService: AdminService,
    private _toastMessage: ToastService
  ) {}

  getList(customerId) {
    return this._adminService.request<PrivilegeResponse>('privileges/fetch', customerId)
      .map(resp => resp.privileges)
      .toPromise();
  }

  save(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully added' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/upsert', privilege, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  remove(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully deleted' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/delete', privilege, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  update(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully Updated' }
    };
    return this._adminService.request<PrivilegeResponse>('privileges/upsert', privilege, options)
      .map(resp => resp.valid ? resp.privileges : null)
      .toPromise();
  }

  getRoles(customerId) {
    return this._adminService.request<RolesResponse>('roles/list', customerId)
      .map(resp => resp.roles)
      .toPromise();
  }

  getProducts(customerId) {
    return this._adminService.request<ProuctsResponse>('products/list', customerId)
      .map(resp => resp.products)
      .toPromise();
  }

  getModules(params: {
    customerId: string,
    productId: number,
    moduleId: number
  }) {
    return this._adminService.request<ModulesResponse>('modules/list', params)
      .map(resp => resp.modules)
      .toPromise();
  }

  getCategories(customerId) {
    return this._adminService.request<CategoryResponse>('categories/list', customerId)
      .map(resp => resp.categories);
  }

  getParentCategories(params: {
    customerId: string,
    productId: number,
    moduleId: number
  }) {
    return this._adminService.request<CategoryResponse>('categories/parent/list', params)
      .map(resp => resp.category)
      .pipe(
        tap(categories => {
          if (isEmpty(categories)) {
            this._toastMessage.error('There are no Categories for this Module');
          }
        }
      ))
      .toPromise();
  }

  getSubCategories(params: {
    customerId: string,
    roleId: number,
    productId: number,
    moduleId: number,
    categoryCode: string
  }) {
    return this._adminService.request<CategoryResponse>('subCategoriesWithPrivilege/list', params)
      .map(resp => resp.subCategories)
      .pipe(
        tap(subCategories => {
          if (isEmpty(subCategories)) {
            this._toastMessage.error('There are no Sub-Categories with Privilege');
          }
        }
      ))
      .toPromise();
  }
}
