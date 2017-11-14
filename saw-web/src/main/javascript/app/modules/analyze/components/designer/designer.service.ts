import { Injectable } from '@angular/core';
import { AnalyzeService } from '../../services/analyze.service'
import {AnalysisType} from '../../constsTS';

@Injectable()
export default class DesignerService {
  constructor(private _analyzeService: AnalyzeService) { }

  createAnalysis(semanticId: string, type: AnalysisType) {
    this._analyzeService.createAnalysis(semanticId, type).then(data => {
      console.log('createAnalysis', data);
    });
  }

  getDataForAnalysis(analysis) {
    this._analyzeService.getDataBySettings(analysis).then(data => {
      console.log('getDataBySettings: ', data);
    })
  }

}
