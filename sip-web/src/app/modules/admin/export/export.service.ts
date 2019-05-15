import { Injectable } from '@angular/core';
import * as fpGet from 'lodash/fp/get';

import { map as mapObservable, first } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';

const ANALYZE_MODULE_NAME = 'ANALYZE';
// const OBSERVE_MODULE_NAME = 'OBSERVE';

interface MetricResponse {
  data: { contents: Array<{}> };
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

  getAnalysesByCategoryId(subCategoryId: number | string) {
    return this._adminService.getAnalysesByCategoryId(subCategoryId);
  }
}
