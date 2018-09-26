import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import * as fpFirst from 'lodash/fp/first';
import * as fpGet from 'lodash/fp/get';
import * as fpPipe from 'lodash/fp/pipe';

import { MenuService } from '../../../common/services/menu.service';
import { LAST_ANALYSES_CATEGORY_ID } from '../../../common/local-storage-keys';

@Injectable()
export class DefaultAnalyzeCategoryGuard implements CanActivate {
  constructor(private _router: Router, private _menu: MenuService) {}

  canActivate() {
    this._menu.getMenu('ANALYZE').then(menu => {
      this.goToDefaultChildStateIfNeeded(menu);
    });
    return true;
  }

  goToDefaultChildStateIfNeeded(menu) {
    const id = window.localStorage[LAST_ANALYSES_CATEGORY_ID] ||
    fpPipe(
      fpFirst,
      fpGet('children'),
      fpFirst,
      fpGet('id')
    )(menu);
    this._router.navigate(['analyze', id]);
  }
}
