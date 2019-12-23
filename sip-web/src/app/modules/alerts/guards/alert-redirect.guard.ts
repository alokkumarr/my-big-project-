import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import * as get from 'lodash/get';
import * as first from 'lodash/first';
import { JwtService } from '../../../common/services';

@Injectable()
export class AlertRedirectGuard implements CanActivate {
  constructor(private _jwt: JwtService, private _router: Router) {}

  canActivate() {
    const cat = this._jwt.getCategories('ALERTS');
    const subFeatures = get(cat, '0.productModuleSubFeatures');
    const firstFeature = first(subFeatures);
    if (firstFeature) {
      const url = firstFeature.defaultURL;
      setTimeout(() => {
        this._router.navigate(['alerts', url]);
      }, 100);
      return true;
    }
    return false;
  }
}
