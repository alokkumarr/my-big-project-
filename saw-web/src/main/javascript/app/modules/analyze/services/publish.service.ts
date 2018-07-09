import { Injectable } from '@angular/core';

import { AnalyzeService } from './analyze.service';
import { ExecuteService } from './execute.service';

@Injectable()
export class PublishService {

  constructor(
    private _analyzeService: AnalyzeService,
    private _executeService: ExecuteService
  ) { }

  publishAnalysis(model, execute = false) {
    this._analyzeService.changeSchedule(model);

    return this._analyzeService.updateAnalysis(model).then(analysis => {
      if (execute) {
        this._executeService.executeAnalysis(model);
      }
      return analysis;
    });
  }
}
