import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { Router, ActivatedRoute } from '@angular/router';

import 'rxjs/add/operator/map';
import 'rxjs/add/observable/of';

import * as fpGet from 'lodash/fp/get';
import * as forEach from 'lodash/forEach';
import * as find from 'lodash/find';
import * as map from 'lodash/map';
import * as add from 'lodash/add';

import { JwtService } from '../../../../login/services/jwt.service';
import { MenuService } from '../../../common/services/menu.service';
import { Dashboard } from '../models/dashboard.interface';
import APP_CONFIG from '../../../../../../../appConfig';
import { BULLET_CHART_COLORS } from '../consts';

@Injectable()
export class ObserveService {
  private api = fpGet('api.url', APP_CONFIG);

  constructor(
    private http: HttpClient,
    private jwt: JwtService,
    private router: Router,
    private route: ActivatedRoute,
    private menu: MenuService
  ) {}

  addModelStructure(model) {
    return {
      contents: {
        observe: [model]
      }
    };
  }

  /* Saves dashboard. If @model.entityId not present, uses create operation.
     Otherwise uses update operation.
  */
  saveDashboard(model: Dashboard) {
    let method = 'post',
      endpoint = 'create';
    if (fpGet('entityId', model)) {
      method = 'put';
      endpoint = `update/${model.entityId}`;
      model.updatedBy = this.jwt.getUserId();
    } else {
      // Log the creator id if creating for first time
      model.createdBy = this.jwt.getUserId();
    }

    return this.http[method](
      `${this.api}/observe/dashboards/${endpoint}`,
      this.addModelStructure(model)
    ).map(fpGet('contents.observe.0'));
  }

  getArtifacts({ semanticId }): Observable<any> {
    return this.http.post(`${this.api}/kpi`, {
      keys: [
        {
          customerCode: this.jwt.customerCode,
          module: 'observe',
          semanticId,
          analysisType: 'kpi'
        }
      ],
      action: 'fetch'
    });
  }

  /**
   * Executes a kpi
   *
   * @param {*} model
   * @returns {Observable<any>}
   * @memberof ObserveService
   */
  executeKPI(kpi: any): Observable<any> {
    // return this.http.get(`${this.api}/observe/kpi/execute`).map(fpGet('contents.observe.0'));

    return this.http.post(`${this.api}/kpi`, {
      keys: [
        {
          customerCode: this.jwt.customerCode,
          module: 'observe',
          semanticId: kpi.semanticId,
          analysisType: 'kpi'
        }
      ],
      action: 'execute',
      kpi
    });
  }

  getDashboard(entityId: string): Observable<Dashboard> {
    return this.http
      .get(`${this.api}/observe/dashboards/${entityId}`)
      .map(fpGet('contents.observe.0'));
  }

  getDashboardsForCategory(
    categoryId,
    userId = this.jwt.getUserId()
  ): Observable<Array<Dashboard>> {
    return this.http
      .get(`${this.api}/observe/dashboards/${categoryId}/${userId}`)
      .map(fpGet('contents.observe'));
  }

  deleteDashboard(dashboard: Dashboard) {
    return this.http
      .delete(`${this.api}/observe/dashboards/${dashboard.entityId}`)
      .map(fpGet('contents.observe'));
  }

  getModelValues(filter) {
    const payload = {
      globalFilters: [
        {
          tableName: filter.tableName,
          semanticId: filter.semanticId,
          filters: [
            {
              columnName: filter.columnName,
              type: filter.type,
              size: 1000,
              order: 'asc'
            }
          ],
          esRepository: filter.esRepository
        }
      ]
    };

    return this.http
      .post(`${this.api}/filters`, payload)
      .map(fpGet(filter.columnName));
  }

  reloadMenu() {
    return Observable.create(observer => {
      this.menu.getMenu('OBSERVE').then(data => {
        let count = this.getSubcategoryCount(data);
        forEach(data, category => {
          forEach(category.children || [], subCategory => {
            this.getDashboardsForCategory(subCategory.id).subscribe(
              (dashboards: Array<Dashboard>) => {
                dashboards = dashboards || [];
                subCategory.children = subCategory.children || [];

                subCategory.children = subCategory.children.concat(
                  map(dashboards, dashboard => ({
                    id: dashboard.entityId,
                    name: dashboard.name,
                    url: [`/observe`, subCategory.id],
                    queryParams: { dashboard: dashboard.entityId },
                    data: dashboard
                  }))
                );

                if (--count <= 0) {
                  observer.next(data);
                  observer.complete();
                }
              },
              error => {
                if (--count <= 0) {
                  observer.next(data);
                  observer.complete();
                }
              }
            );
          });
        });
      });
      return observer;
    });
  }

  updateSidebar(data) {
    // const data = [
    //   {
    //     id: 1,
    //     name: 'My Dashboards',
    //     children: [
    //       { id: 2, name: 'Testing', url: `#!/observe/d8939bf3-d8f4-4ee7-89c4-f2a4fd4abca9::PortalDataSet::1513945502617`}
    //     ]
    //   }
    // ];

    this.menu.updateMenu(data, 'OBSERVE');
  }

  /* Try to redirect to first dashboard or first empty subcategory */
  redirectToFirstDash(menu, force = false) {
    /* Only redirect if on root observe state */
    const basePath = this.route.snapshot.children[0].url[0].path;
    if (basePath !== 'observe' && !force) {
      return;
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
  }

  getSubcategoryCount(data) {
    let count = 0;
    forEach(data, category => {
      count += category.children.length;
    });

    return count;
  }

  buildPlotBandsForBullet(bandColVal, b1, b2, bulletVal, bullTarget) {
    const cb = find(BULLET_CHART_COLORS, { value: bandColVal });
    const plotBands = [];
    const seriesData = [{ y: bulletVal, target: bullTarget }];
    if (b1 === 0 || b2 === 0) {
      const bt1 = b1 === 0 ? b2 : b1;
      plotBands.push(
        {
          from: 0,
          to: bt1,
          color: cb.b1
        },
        {
          from: bt1,
          to: 9e9,
          color: cb.b3
        }
      );
    } else {
      plotBands.push(
        {
          from: 0,
          to: b1,
          color: cb.b1
        },
        {
          from: b1,
          to: b2,
          color: cb.b2
        },
        {
          from: b2,
          to: add(b2, 9e9),
          color: cb.b3
        }
      );
    }

    return { plotBands, seriesData };
  }
}
