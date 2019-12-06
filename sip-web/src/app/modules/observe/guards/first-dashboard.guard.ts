import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import * as find from 'lodash/find';

import { ObserveService } from '../services/observe.service';
import {
  PREFERENCES,
  ConfigService
} from '../../../common/services/configuration.service';

@Injectable()
export class FirstDashboardGuard implements CanActivate {
  constructor(
    private router: Router,
    private observe: ObserveService,
    private configService: ConfigService
  ) {}

  canActivate(route: ActivatedRouteSnapshot) {
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
    if ((basePath && basePath !== 'observe') || dashboard || mode) {
      return true;
    }
    this.redirectToFavoriteOrFirstDashboard();
    return true;
  }

  redirectToFavoriteOrFirstDashboard() {
    const favouriteDashboardId = this.configService.getPreference(
      PREFERENCES.DEFAULT_DASHBOARD
    );
    const favouriteDashboardCategory = this.configService.getPreference(
      PREFERENCES.DEFAULT_DASHBOARD_CAT
    );
    console.log('favouriteDashboardCategory', favouriteDashboardCategory);
    console.log('favouriteDashboardId', favouriteDashboardId);
    if (!favouriteDashboardId) {
      this.redirectToFirstDash();
    } else {
      this.redirectToFavoriteDashboard(
        favouriteDashboardCategory,
        favouriteDashboardId
      );
    }
  }

  redirectToFavoriteDashboard(
    favouriteDashboardCategory,
    favouriteDashboardId
  ) {
    this.router.navigate(['observe', favouriteDashboardCategory], {
      queryParams: {
        dashboard: favouriteDashboardId
      }
    });
  }

  redirectToFirstDash(): void {
    this.observe
      .reloadMenu()
      .toPromise()
      .then(menu => {
        const categoryWithDashboard = find(menu, cat => {
          const subCategory = find(cat.children, subCat => {
            return subCat.children.length > 0;
          });

          return Boolean(subCategory);
        });

        const categoryWithSubCategory = find(
          menu,
          cat => cat.children.length > 0
        );

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
        }
      });
  }
}
