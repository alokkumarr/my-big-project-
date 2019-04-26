import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as fpGet from 'lodash/fp/get';
import * as fpSortBy from 'lodash/fp/sortBy';

import { map as mapObservable, first } from 'rxjs/operators';
import { Observable, zip } from 'rxjs';

import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';

const ANALYZE_MODULE_NAME = 'ANALYZE';
// const OBSERVE_MODULE_NAME = 'OBSERVE';

interface MetricResponse {
  data: { contents: Array<{}> };
}
interface AnalysisResponse {
  data: { contents: { analyze: any[] } };
}
interface DashboardResponse {
  data: { contents: { observe: any[] } };
}

@Injectable()
export class ExportService {
  constructor(
    public _adminService: AdminService,
    public _jwtService: JwtService
  ) {}

  /**
   * Gets list of analyses from legacy endpoint (non-dsl).
   * This endpoint is for backward-compatibility. Remove when not needed.
   *
   * @param {*} subCategoryId
   * @returns {Observable<Analysis[]>}
   * @memberof AnalyzeService
   */
  getAnalysesForNonDSL(subCategoryId): Observable<any[]> {
    const payload = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.keys.[0].categoryId', subCategoryId.toString()]
    ]);
    return <Observable<any[]>>(
      this._adminService
        .request(`analysis`, payload, { forWhat: 'export' })
        .pipe(mapObservable(fpGet('contents.analyze')))
    );
  }

  getAnalysesDSL(subCategoryId: string | number): Observable<any[]> {
    return <Observable<any[]>>this._adminService.getRequest(
      `dslanalysis?category=${subCategoryId}`,
      {
        forWhat: 'export'
      }
    );
  }

  /**
   * Stitches non-dsl and dsl endpoints for listing analyses and provides
   * results as a single array.
   *
   * @param {*} subCategoryId
   * @returns {Observable<Analysis[]>}
   * @memberof AnalyzeService
   */
  getAnalysesByCategoryId(
    subCategoryId: string | number /* , opts = {} */
  ): Observable<Array<any>> {
    // Create fp sort's type to nail everything down with types
    type FPSort<T> = (input: Array<T>) => Array<T>;

    return zip(
      this.getAnalysesForNonDSL(subCategoryId),
      this.getAnalysesDSL(subCategoryId)
    ).pipe(
      // Merge list of analyses from both observables into one
      mapObservable(([nonDSLAnalyses, dslAnalyses]) => {
        return [].concat(nonDSLAnalyses).concat(dslAnalyses);
      }),

      // Sort all the analyses based on their create time in descending order (newest first).
      // Uses correct time field based on if analysis is new dsl type or not
      mapObservable(<FPSort<any>>(
        fpSortBy([
          analysis => -(analysis.createdTime || analysis.createdTimestamp || 0)
        ])
      ))
    );
  }
  getDashboardsForCategory(categoryId): Observable<any[]> {
    const userId = this._jwtService.getUserId();
    return this._adminService
      .getRequest<DashboardResponse>(
        `observe/dashboards/${categoryId}/${userId}`,
        { forWhat: 'export' }
      )
      .pipe(
        first(),
        mapObservable(fpGet(`contents.observe`))
      );
  }

  getMetricList$(): Observable<any[]> {
    const projectId = 'workbench';
    return this._adminService
      .getRequest<MetricResponse>(
        `internal/semantic/md?projectId=${projectId}`,
        { forWhat: 'export' }
      )
      .pipe(mapObservable(fpGet(`contents.[0].${ANALYZE_MODULE_NAME}`)));
  }

  getMetricList() {
    return this.getMetricList$().toPromise();
  }

  getAnalysisByMetricIds(metricIds) {
    const customerCode = get(this._jwtService.getTokenObj(), 'ticket.custCode');
    const params = {
      contents: {
        action: 'export',
        keys: map(metricIds, metricId => ({
          customerCode,
          module: ANALYZE_MODULE_NAME,
          semanticId: metricId
        }))
      }
    };
    return this._adminService
      .request<AnalysisResponse>('analysis', params, { forWhat: 'export' })
      .pipe(mapObservable(fpGet(`contents.analyze`)))
      .toPromise();
  }

  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', ANALYZE_MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
  }
}
