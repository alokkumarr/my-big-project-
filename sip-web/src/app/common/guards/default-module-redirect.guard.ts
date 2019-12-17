import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

import { CONFIG_KEY, PREFERENCES } from '../services/configuration.service';

const DEFAULT_ROUTE = '/analyze';
import { JwtService } from './../../common/services/jwt.service';
import {
  map,
  get
} from 'lodash';

@Injectable()
export class DefaultModuleGuard implements CanActivate {
  constructor(
    private router: Router,
    public jwt: JwtService
  ) {}

  canActivate() {
    const config = localStorage.getItem(CONFIG_KEY);
    if (!config) {
      this.router.navigate([DEFAULT_ROUTE]);
      return false;
    }

    try {
      const pref = JSON.parse(config);
      const isObservePrivileged = this.checkObservePrivilege();

      if (!Array.isArray(pref.preferences)) {
        this.router.navigate([DEFAULT_ROUTE]);
        return false;
      }

      const defaultDashboard = pref.preferences.filter(
        p => p.preferenceName === PREFERENCES.DEFAULT_DASHBOARD
      )[0];
      const defaultDashboardCat = pref.preferences.filter(
        p => p.preferenceName === PREFERENCES.DEFAULT_DASHBOARD_CAT
      )[0];

      if (
        !defaultDashboard ||
        !defaultDashboardCat ||
        !defaultDashboard.preferenceValue ||
        !defaultDashboardCat.preferenceValue ||
        !isObservePrivileged
      ) {
        this.router.navigate([DEFAULT_ROUTE]);
        return false;
      }

      this.router.navigate([`/observe`, defaultDashboardCat.preferenceValue], {
        queryParams: {
          dashboard: defaultDashboard.preferenceValue
        }
      });
      return false;
    } catch (err) {
      this.router.navigate([DEFAULT_ROUTE]);
      return false;
    }
  }

  checkObservePrivilege() {
    const token = this.jwt.getTokenObj();
    const product = get(token, 'ticket.products.[0]');
    const modules = [
      ...map(product.productModules, ({ productModName }) => {
        return productModName;
      })
    ];
    return modules.includes('OBSERVE');
  }
}
