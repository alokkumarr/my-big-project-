import { Injectable } from '@angular/core';
import { Analysis, AnalysisDSL } from '../../../models';
import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';

import * as fpGet from 'lodash/fp/get';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import { map } from 'rxjs/operators';

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

  /**
   * Transforms a list of analyses into a map for easy lookup
   * by analysis name, metric name and analysis type.
   * Helps in quickly finding possible duplicates while importing.
   *
   * @param {Analysis[]} analyses
   * @returns {{ [reference: string]: Analysis }}
   * @memberof ImportService
   */
  createReferenceMapFor(
    analyses: (Analysis | AnalysisDSL)[]
  ): { [reference: string]: Analysis } {
    return analyses.reduce((acc, analysis) => {
      acc[
        `${analysis.name}:${analysis.semanticId}:${analysis.type}`
      ] = analysis;
      return acc;
    }, {});
  }

  getRequestParams(params = []) {
    const reqParams = this._jwtService.getRequestParams();

    set(reqParams, 'contents.keys.[0].module', MODULE_NAME);
    forEach(params, tuple => {
      set(reqParams, tuple[0], tuple[1]);
    });
    return reqParams;
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
