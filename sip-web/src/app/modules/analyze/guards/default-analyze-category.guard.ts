import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  ParamMap
} from '@angular/router';
import * as fpFirst from 'lodash/fp/first';
import * as fpGet from 'lodash/fp/get';
import * as fpPipe from 'lodash/fp/pipe';

import { MenuService } from '../../../common/services/menu.service';
import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';

@Injectable()
export class DefaultAnalyzeCategoryGuard implements CanActivate {
  constructor(private _router: Router, public _menu: MenuService) {}

  canActivate(route: ActivatedRouteSnapshot) {
    const params: ParamMap = fpGet('children[0].paramMap', route);
    if (params && params.keys.length) {
      return true;
    }

    const queryParams: ParamMap = fpGet('children[0].queryParamMap', route);
    if (queryParams && queryParams.keys.length) {
      return true;
    }

    return this._menu.getMenu('ANALYZE').then(menu => {
      return this.goToDefaultChildStateIfNeeded(menu);
    });
  }

  goToDefaultChildStateIfNeeded(menu): boolean {
    const id =
      window.localStorage[LAST_ANALYSES_CATEGORY_ID] ||
      fpPipe(fpFirst, fpGet('children'), fpFirst, fpGet('id'))(menu);
    this._router.navigate(['analyze', id]);
    return false;
  }
}
