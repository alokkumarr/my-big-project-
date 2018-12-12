import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as fpGet from 'lodash/fp/get';

import { map as mapObservable, first } from 'rxjs/operators';
import { Observable } from 'rxjs/Observable';

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

  getAnalysesByCategoryId(categoryId: number | string): Observable<any[]> {
    const customerCode = get(this._jwtService.getTokenObj(), 'ticket.custCode');
    const params = {
      contents: {
        action: 'search',
        keys: [
          {
            customerCode,
            module: ANALYZE_MODULE_NAME,
            categoryId: categoryId.toString()
          }
        ]
      }
    };
    return this._adminService
      .request<AnalysisResponse>('analysis', params, { forWhat: 'export' })
      .pipe(
        first(),
        mapObservable(fpGet('contents.analyze'))
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

  getMetricList() {
    const projectId = 'workbench';
    return this._adminService
      .getRequest<MetricResponse>(
        `internal/semantic/md?projectId=${projectId}`,
        { forWhat: 'export' }
      )
      .pipe(mapObservable(fpGet(`contents.[0].${ANALYZE_MODULE_NAME}`)))
      .toPromise();
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
