import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as get from 'lodash/get';
import * as map from 'lodash/map';
import * as fpGet from 'lodash/fp/get';

import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../../login/services/jwt.service';

const MODULE_NAME = 'ANALYZE';

type AnalysisResponse = {
  data: {contents: {analyze: any[]}};
};

@Injectable()
export class ImportService {

  constructor(
    private _adminService: AdminService,
    private _jwtService: JwtService
  ) {}

  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
  }

  getAnalysesFor(subCategoryId) {
    const params = this.getRequestParams([
      ['contents.action', 'search'],
      ['contents.keys.[0].categoryId', subCategoryId]
    ]);
    return this._adminService.request<AnalysisResponse>('analysis', params)
      .map(fpGet(`data.contents.analyze`))
      .toPromise();
  }

  createAnalysis(metricId, type) {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', metricId],
      ['contents.keys.[0].analysisType', type]
    ]);
    return this._adminService.request<AnalysisResponse>('analysis', params)
      .map(fpGet(`data.contents.analyze.[0]`))
      .toPromise();
  }

  updateAnalysis(analysis) {
    const params = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', analysis.id],
      ['contents.keys.[0].type', analysis.type],
      ['contents.analyze', [analysis]]
    ]);
    return this._adminService.request<AnalysisResponse>('analysis', params)
      .map(fpGet(`data.contents.analyze.[0]`))
      .toPromise();
  }
}
