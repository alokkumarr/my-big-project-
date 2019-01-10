import { Injectable } from '@angular/core';
import * as isEmpty from 'lodash/isEmpty';
import { AdminService } from '../main-view/admin.service';
import { ToastService } from '../../../common/services/toastMessage.service';
import { map, tap } from 'rxjs/operators';
import { IAdminDataService } from '../admin-data-service.interface';
import { getPrivilegeDescription } from './privilege-code-transformer';
import { Observable } from 'rxjs';

interface PrivilegeResponse {
  privileges: any[];
  valid: boolean;
  validityMessage: string;
}

interface RolesResponse {
  roles: any[];
  valid: boolean;
  validityMessage: string;
}

interface ModulesResponse {
  modules: any[];
  valid: boolean;
  validityMessage: string;
}

interface ProuctsResponse {
  products: any[];
  valid: boolean;
  validityMessage: string;
}

interface CategoryResponse {
  categories: any[];
  valid: boolean;
  validityMessage: string;
}

interface ModulePrivilegeResponse {
  priviliges: {
    [id: string]: string;
  };
  valid: boolean;
  message: string;
}

@Injectable()
export class PrivilegeService implements IAdminDataService {
  constructor(
    public _adminService: AdminService,
    public _toastMessage: ToastService
  ) {}

  getList(customerId) {
    return this.getAllPrivilegeMap().then(privilegeMap => {
      return this.getPrivilegeList(customerId, privilegeMap);
    });
  }

  getPrivilegeList(
    customerId,
    allowedPrivileges: { [moduleName: string]: string[] }
  ) {
    return this._adminService
      .request<PrivilegeResponse>('privileges/fetch', customerId)
      .pipe(
        map(resp => resp.privileges),

        /* Stored privilege description can get outdated, and may be wrong due
         * to previous bugs for some privileges.
         * Calculate and show descriptions on the fly instead of showing stored ones.
         */
        map(privileges =>
          (privileges || []).map(privilege => ({
            ...privilege,
            privilegeDesc: getPrivilegeDescription(
              privilege.privilegeCode,
              allowedPrivileges[privilege.moduleName]
            )
          }))
        )
      )
      .toPromise();
  }

  /**
   * Returns a map of all the allowed privileges by module.
   * Example: {
   *   ANALYZE: ['CREATE', 'EXPORT'],
   *   OBSERVE: ['FORK', 'EXPORT']
   * }
   *
   * @returns
   * @memberof PrivilegeService
   */
  getAllPrivilegeMap(): Promise<{ [moduleName: string]: string[] }> {
    return (<any>this.getModulePrivilegeMap(''))
      .toPromise()
      .then((resp: any[]) => {
        return (resp || []).reduce(
          (accum, { moduleName, privilegeCodeName }) => {
            accum[moduleName] = accum[moduleName] || [];
            accum[moduleName].push(privilegeCodeName);
            return accum;
          },
          {}
        );
      });
  }

  save(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully added' }
    };
    return this._adminService
      .request<PrivilegeResponse>('privileges/upsert', privilege, options)
      .pipe(map(resp => (resp.valid ? resp.privileges : null)))
      .toPromise();
  }

  remove(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully deleted' }
    };
    return this._adminService
      .request<PrivilegeResponse>('privileges/delete', privilege, options)
      .pipe(map(resp => (resp.valid ? resp.privileges : null)))
      .toPromise();
  }

  update(privilege) {
    const options = {
      toast: { successMsg: 'Privilege is successfully Updated' }
    };
    return this._adminService
      .request<PrivilegeResponse>('privileges/upsert', privilege, options)
      .pipe(map(resp => (resp.valid ? resp.privileges : null)))
      .toPromise();
  }

  getRoles(customerId) {
    return this._adminService
      .request<RolesResponse>('roles/list', customerId)
      .pipe(map(resp => resp.roles))
      .toPromise();
  }

  getModulePrivilegeMap(moduleId): Observable<ModulePrivilegeResponse> {
    return this._adminService.getRequest<ModulePrivilegeResponse>(
      `module-privileges/${moduleId}`
    );
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

  getCategories(customerId) {
    return this._adminService
      .request<CategoryResponse>('categories/list', customerId)
      .pipe(map(resp => resp.categories));
  }

  getParentCategories(params: {
    customerId: string;
    productId: number;
    moduleId: number;
  }) {
    return this._adminService
      .request<CategoryResponse>('categories/parent/list', params)
      .pipe(
        map(resp => resp.category),
        tap(categories => {
          if (isEmpty(categories)) {
            this._toastMessage.error('There are no Categories for this Module');
          }
        })
      )
      .toPromise();
  }

  getSubCategories(params: {
    customerId: string;
    roleId: number;
    productId: number;
    moduleId: number;
    categoryCode: string;
  }) {
    return this._adminService
      .request<CategoryResponse>('subCategoriesWithPrivilege/list', params)
      .pipe(
        map(resp => resp.subCategories),
        tap(subCategories => {
          if (isEmpty(subCategories)) {
            this._toastMessage.error(
              'There are no Sub-Categories with Privilege'
            );
          }
        })
      )
      .toPromise();
  }
}
