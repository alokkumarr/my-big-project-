import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as fpGet from 'lodash/fp/get';

import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../../login/services/jwt.service';

const MODULE_NAME = 'ANALYZE';

type MetricResponse = {
  data: {contents: Array<{}>}
};
type AnalysisResponse = {
  data: {contents: {analyze: any[]}};
};

@Injectable()
export class ExportService {

  constructor(
    private _adminService: AdminService,
    private _jwtService: JwtService
  ) {}

  getMetricList() {
    const params = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.select', 'headers'],
      ['contents.context', 'Semantic']
    ]);
    return this._adminService.request<MetricResponse>('md', params, {forWhat: 'export'})
      .map(resp => {
        console.log('resposne metrics', resp);
        return resp;
      })
      .map(fpGet(`contents.[0].${MODULE_NAME}`))
      .toPromise();
  }

  getAnalysisByMetricIds(metricIds: []) {
    const customerCode = get(this._jwtService.getTokenObj(), 'ticket.custCode');
    const params = {
      contents: {
        action: 'export',
        keys: map(metricIds, metricId => ({
          customerCode,
          module: MODULE_NAME,
          semanticId: metricId
        }))
      }
    };
    return this._adminService.request<AnalysisResponse>('analysis', params, {forWhat: 'export'})
      .map(fpGet(`contents.analyze`))
      .toPromise();
  }

  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
  }
}
