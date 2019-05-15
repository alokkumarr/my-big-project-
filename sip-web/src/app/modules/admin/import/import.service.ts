import { Injectable } from '@angular/core';
import { Analysis, AnalysisDSL } from '../../../models';
import { AdminService } from '../main-view/admin.service';
import { JwtService } from '../../../common/services';
import { AnalyzeService } from '../../analyze/services/analyze.service';

import * as find from 'lodash/find';
import * as values from 'lodash/values';

@Injectable()
export class ImportService {
  constructor(
    public _adminService: AdminService,
    public _jwtService: JwtService,
    private _analyzeService: AnalyzeService
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
    analyses: (Analysis | AnalysisDSL)[],
    metrics: { [metricName: string]: any }
  ): { [reference: string]: Analysis } {
    const metricArray: any[] = values(metrics);
    return analyses.reduce((acc, analysis) => {
      const metric = find(metricArray, m => m.id === analysis.semanticId);
      acc[`${analysis.name}:${metric.metricName}:${analysis.type}`] = analysis;
      return acc;
    }, {});
  }

  createAnalysis(semanticId, type) {
    return this._analyzeService.createAnalysis(semanticId, type);
  }

  updateAnalysis(
    analysis: Analysis | AnalysisDSL
  ): Promise<Analysis | AnalysisDSL> {
    return this._analyzeService.updateAnalysis(analysis);
  }
}
