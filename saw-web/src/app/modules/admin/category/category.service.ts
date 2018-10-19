import { Injectable } from '@angular/core';
import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';
import { map } from 'rxjs/operators';
import { IAdminDataService } from '../admin-data-service.interface';

interface CategoryResponse {
  categories: any[];
  valid: boolean;
  validityMessage: string;
}
interface ProuctsResponse {
  categories: any[];
  valid: boolean;
  validityMessage: string;
}
interface ModulesResponse {
  categories: any[];
  valid: boolean;
  validityMessage: string;
}

@Injectable()
export class CategoryService implements IAdminDataService {
  customerId;
  constructor(
    public _adminService: AdminService,
    public _jwtService: JwtService
  ) {
    const token = _jwtService.getTokenObj();
    const customerId = token.ticket.custID;
    this.customerId = customerId;
  }

  getList() {
    const customerId = parseInt(this.customerId, 10);
    return this._adminService
      .request<CategoryResponse>('categories/fetch', customerId)
      .pipe(map(resp => resp.categories))
      .toPromise();
  }

  save(user) {
    const options = {
      toast: { successMsg: 'Category is successfully added' }
    };
    return this._adminService
      .request<CategoryResponse>('categories/add', user, options)
      .pipe(map(resp => (resp.valid ? resp.categories : null)))
      .toPromise();
  }

  remove(params: {
    categoryId: number;
    customerId: number;
    s;
    masterLoginId: string;
    categoryCode: number;
  }) {
    const options = {
      toast: { successMsg: 'Category is successfully deleted' }
    };
    return this._adminService
      .request<CategoryResponse>('categories/delete', params, options)
      .pipe(map(resp => (resp.valid ? resp.categories : null)))
      .toPromise();
  }

  removeSubCategory(params: {
    categoryId: number;
    customerId: number;
    masterLoginId: string;
    categoryCode: number;
  }) {
    const options = {
      toast: { successMsg: 'Subcategory is successfully deleted' }
    };
    return this._adminService
      .request<CategoryResponse>('subcategories/delete', params, options)
      .pipe(map(resp => (resp.valid ? resp.subCategories : null)))
      .toPromise();
  }

  update(user) {
    const options = {
      toast: { successMsg: 'Category is successfully Updated' }
    };
    return this._adminService
      .request<CategoryResponse>('categories/edit', user, options)
      .pipe(map(resp => (resp.valid ? resp.categories : null)))
      .toPromise();
  }

  getProducts(customerId) {
    return this._adminService
      .request<ProuctsResponse>('products/list', customerId)
      .pipe(map(resp => resp.products))
      .toPromise();
  }

  getModules(params: {
    customerId: string;
    productId: number;
    moduleId: number;
  }) {
    return this._adminService
      .request<ModulesResponse>('modules/list', params)
      .pipe(map(resp => resp.modules))
      .toPromise();
  }

  getParentCategories(params: {
    customerId: string;
    productId: number;
    moduleId: number;
  }) {
    return this._adminService
      .request<CategoryResponse>('categories/parent/list', params)
      .pipe(map(resp => resp.category))
      .toPromise();
  }
}
