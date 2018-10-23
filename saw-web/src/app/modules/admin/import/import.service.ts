import { Injectable } from '@angular/core';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import { Analysis } from '../../../models';
import * as fpGet from 'lodash/fp/get';
import { map } from 'rxjs/operators';

import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';

const MODULE_NAME = 'ANALYZE';

interface AnalysisResponse {
  data: { contents: { analyze: Analysis[] } };
}

@Injectable()
export class ImportService {
  constructor(
    public _adminService: AdminService,
    public _jwtService: JwtService
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
    return this._adminService
      .request<AnalysisResponse>('analysis', params, { forWhat: 'import' })
      .pipe(map(fpGet(`contents.analyze`)))
      .toPromise();
  }

  createAnalysis(semanticId, type) {
    const params = this.getRequestParams([
      ['contents.action', 'create'],
      ['contents.keys.[0].id', semanticId],
      ['contents.keys.[0].analysisType', type]
    ]);
    return this._adminService
      .request<AnalysisResponse>('analysis', params, { forWhat: 'import' })
      .pipe(map(fpGet(`contents.analyze.[0]`)))
      .toPromise();
  }

  updateAnalysis(analysis): Promise<Analysis> {
    const params = this.getRequestParams([
      ['contents.action', 'update'],
      ['contents.keys.[0].id', analysis.id],
      ['contents.keys.[0].type', analysis.type],
      ['contents.analyze', [analysis]]
    ]);
    return this._adminService
      .request<AnalysisResponse>('analysis', params, { forWhat: 'import' })
      .pipe(map(data => <Analysis>fpGet(`contents.analyze.[0]`, data)))
      .toPromise();
  }
}
