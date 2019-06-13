import { Injectable } from '@angular/core';

import { Analysis, isDSLAnalysis } from '../types';
import { AnalysisDSL } from '../../../models';
import { AnalyzeService, EXECUTION_MODES } from './analyze.service';
import { ExecuteService } from './execute.service';

@Injectable()
export class PublishService {
  constructor(
    public _analyzeService: AnalyzeService,
    public _executeService: ExecuteService
  ) {}

  publishAnalysis(
    analysis: Analysis | AnalysisDSL,
    execute = false,
    type
  ): Promise<Analysis | AnalysisDSL> {
    if (isDSLAnalysis(analysis)) {
      return this.publishAnalysisDSL(analysis, execute, type);
    } else {
      return this.publishAnalysisNonDSL(analysis, execute, type);
    }
  }

  publishAnalysisNonDSL(
    model: Analysis,
    execute = false,
    type
  ): Promise<Analysis> {
    if (type === 'schedule') {
      this._analyzeService.changeSchedule(model);
    }

    return <Promise<Analysis>>(
      this._analyzeService.updateAnalysis(model).then(analysis => {
        if (execute) {
          this._executeService.executeAnalysis(model, EXECUTION_MODES.PUBLISH);
        }
        return analysis;
      })
    );
  }

  publishAnalysisDSL(
    model: AnalysisDSL,
    execute = false,
    type
  ): Promise<AnalysisDSL> {
    if (execute) {
      this._executeService.executeAnalysis(model, EXECUTION_MODES.PUBLISH);
    }
    if (type === 'schedule') {
      return this._analyzeService.changeSchedule(model).then(() => model);
    } else {
      return <Promise<AnalysisDSL>>this._analyzeService.updateAnalysis(model);
    }
  }
}
