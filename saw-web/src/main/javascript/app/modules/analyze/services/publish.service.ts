import { Injectable } from '@angular/core';

import { AnalyzeService, EXECUTION_MODES } from './analyze.service';
import { ExecuteService } from './execute.service';

@Injectable()
export class PublishService {
  constructor(
    private _analyzeService: AnalyzeService,
    private _executeService: ExecuteService
  ) {}

  publishAnalysis(model, execute = false, type) {
    console.log(model, execute, type);
    if (type === 'schedule') {
      this._analyzeService.changeSchedule(model);  
    }

    return this._analyzeService.updateAnalysis(model).then(analysis => {
      if (execute) {
        this._executeService.executeAnalysis(model, EXECUTION_MODES.PUBLISH);
      }
      return analysis;
    });
  }
}
