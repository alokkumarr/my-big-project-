import { Injectable } from '@angular/core';
import { AnalyzeService } from '../../services/analyze.service'
import {AnalysisType} from '../../types';
import Analysis from '../../models/analysis.model';

@Injectable()
export default class DesignerService {
  constructor(private _analyzeService: AnalyzeService) { }

  createAnalysis(semanticId: string, type: AnalysisType): Promise<Analysis> {
    return this._analyzeService.createAnalysis(semanticId, type);
  }

  getDataForAnalysis(analysis) {
    return this._analyzeService.getDataBySettings(analysis);
  }

}
