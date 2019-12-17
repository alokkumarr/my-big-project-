import { Injectable } from '@angular/core';
import * as find from 'lodash/find';

import { AnalysisDSL, Schedule } from '../../../models';
import { AnalyzeService, EXECUTION_MODES } from './analyze.service';
import { ExecuteService } from './execute.service';
import { JwtService } from 'src/app/common/services';

@Injectable()
export class PublishService {
  constructor(
    public _analyzeService: AnalyzeService,
    public _executeService: ExecuteService,
    public _jwtService: JwtService
  ) {}

  async publishAnalysis(
    analysis: AnalysisDSL,
    lastCategoryId: number | string = null
  ): Promise<AnalysisDSL> {
    await this.updateScheduleBeforePublishing(analysis, lastCategoryId);
    await this._executeService.executeAnalysis(
      analysis,
      EXECUTION_MODES.PUBLISH
    );
    return analysis;
  }

  async scheduleAnalysis(schedule: Schedule): Promise<AnalysisDSL> {
    return this._analyzeService.changeSchedule(schedule);
  }

  async updateScheduleBeforePublishing(
    analysis: AnalysisDSL,
    lastCategoryId
  ): Promise<any> {
    const requestModel = {
      categoryId: lastCategoryId,
      groupkey: this._jwtService.customerCode
    };
    const jobs = await this._analyzeService.getAllCronJobs(requestModel);
    const job = find(jobs.data, j => j.jobDetails.analysisID === analysis.id);

    if (!job) {
      return;
    }

    job.jobDetails.scheduleState = 'exist';
    job.jobDetails.categoryID = analysis.category;
    const schedule = job.jobDetails;

    return this._analyzeService.changeSchedule(schedule);
  }
}
