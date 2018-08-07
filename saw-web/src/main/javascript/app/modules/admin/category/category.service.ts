import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import {tap} from 'rxjs/operators/tap';
import { IAdminDataService } from '../admin-data-service.interface';

type CategoryResponse = {
  categories: any[],
  valid: boolean,
  validityMessage: string
};
type ProuctsResponse = {
  categories: any[],
  valid: boolean,
  validityMessage: string
};
type ModulesResponse = {
  categories: any[],
  valid: boolean,
  validityMessage: string
};

@Injectable()
export class CategoryService implements IAdminDataService {

  constructor(
    private _adminService: AdminService
  ) {}

  getList(customerId) {
    return this._adminService.request<CategoryResponse>('categories/fetch', customerId)
      .map(resp => resp.categories);
  }

  save(user) {
    const options = {
      toast: { successMsg: 'Category is successfully added' }
    };
    return this._adminService.request<CategoryResponse>('categories/add', user, options)
      .map(resp => resp.valid ? resp.categories : null)
      .toPromise();
  }

  remove(params: {
    categoryId: number,
    customerId: number,s
    masterLoginId: string,
    categoryCode: number
  }) {
    const options = {
      toast: { successMsg: 'Category is successfully deleted' }
    };
    return this._adminService.request<CategoryResponse>('categories/delete', params, options)
      .map(resp => resp.valid ? resp.categories : null)
      .toPromise();
  }

  removeSubCategory(params: {
    categoryId: number,
    customerId: number,
    masterLoginId: string,
    categoryCode: number
  }) {
    const options = {
      toast: { successMsg: 'Subcategory is successfully deleted' }
    };
    return this._adminService.request<CategoryResponse>('subcategories/delete', params, options)
      .map(resp => resp.valid ? resp.subCategories : null)
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'Category is successfully Updated' }
    };
    return this._adminService.request<CategoryResponse>('categories/edit', user, options)
      .map(resp => resp.valid ? resp.categories : null)
      .toPromise();
  }

  getProducts(customerId) {
    return this._adminService.request<ProuctsResponse>('products/list', customerId)
      .pipe(tap(resp => console.log('resp', resp)))
      .map(resp => resp.products)
      .toPromise();
  }

  getModules(params: {
    customerId: string,
    productId: number,
    moduleId: number
  }) {
    return this._adminService.request<ModulesResponse>('modules/list', params)
      .pipe(tap(resp => console.log('resp', resp)))
      .map(resp => resp.modules)
      .toPromise();
  }

  getParentCategories(params: {
    customerId: string,
    productId: number,
    moduleId: number
  };) {
    return this._adminService.request<CategoryResponse>('categories/parent/list', params)
      .pipe(tap(resp => console.log('resp', resp)))
      .map(resp => resp.category)
      .toPromise();
  }
}
