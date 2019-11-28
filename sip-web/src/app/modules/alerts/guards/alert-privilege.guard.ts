import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot } from '@angular/router';
import * as get from 'lodash/get';
import * as some from 'lodash/some';
import { JwtService } from '../../../common/services';

@Injectable()
export class AlertPrivilegeGuard implements CanActivate {
  constructor(private _jwt: JwtService) {}

  canActivate(route: ActivatedRouteSnapshot) {
    const cat = this._jwt.getCategories('ALERTS');
    const subFeatures = get(cat, '0.productModuleSubFeatures');
    const { path } = route.routeConfig;
    const isFeatureSupported = some(
      subFeatures,
      ({ defaultURL }) => path === defaultURL
    );
    return isFeatureSupported;
  }
}
