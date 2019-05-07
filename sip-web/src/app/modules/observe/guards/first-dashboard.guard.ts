import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { ObserveService } from '../services/observe.service';

import { map } from 'rxjs/operators';

import * as find from 'lodash/find';

@Injectable()
export class FirstDashboardGuard implements CanActivate {
  constructor(private router: Router, private observe: ObserveService) {}

  canActivate(route: ActivatedRouteSnapshot) {
    return this.observe.reloadMenu().pipe(
      map(menu => {
        return this.redirectToFirstDash(route, menu);
      })
    );
  }

  redirectToFirstDash(
    route: ActivatedRouteSnapshot,
    menu,
    force = false
  ): boolean {
    let basePath, dashboard, mode;

    if (route && route.children && route.children.length) {
      basePath = route.children[0].url[0].path;
      dashboard = route.queryParams.dashboard;
      mode = route.queryParams.mode;
    }

    /* If not on observe page, or a dashboard is being provided in url, don't
     * load first dashboard automatically.
     * If mode is set, then we're loading designer. Don't override it.
     * If force is set, this behaviour is overriden, and first dashboard is
     * loaded regardless.
     */
    if (((basePath && basePath !== 'observe') || dashboard || mode) && !force) {
      return true;
    }

    const categoryWithDashboard = find(menu, cat => {
      const subCategory = find(cat.children, subCat => {
        return subCat.children.length > 0;
      });

      return Boolean(subCategory);
    });

    const categoryWithSubCategory = find(menu, cat => cat.children.length > 0);

    if (categoryWithDashboard) {
      /* If a dashboard has been found in some category/subcategory, redirect to that */
      const subCategory = find(categoryWithDashboard.children, subCat => {
        return subCat.children.length > 0;
      });

      this.router.navigate(['observe', subCategory.id], {
        queryParams: {
          dashboard: subCategory.children[0].id
        }
      });
      return false;
    } else if (categoryWithSubCategory) {
      /* Otherwise, redirect to the first empty subcategory available. */
      this.router.navigate(
        ['observe', categoryWithSubCategory.children[0].id],
        {
          queryParams: {
            dashboard: ''
          }
        }
      );
      return false;
    }
  }
}
