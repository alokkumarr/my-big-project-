import { Injectable } from '@angular/core';

import { AnalysisDSL } from '../../../models';
import { AnalyzeService, EXECUTION_MODES } from './analyze.service';
import { ExecuteService } from './execute.service';
import { JwtService } from 'src/app/common/services';

import * as find from 'lodash/find';

@Injectable()
export class PublishService {
  constructor(
    public _analyzeService: AnalyzeService,
    public _executeService: ExecuteService,
    public _jwtService: JwtService
  ) {}

  async publishAnalysis(
    analysis: AnalysisDSL,
    execute = false,
    type,
    lastCategoryId: number | string = null
  ): Promise<AnalysisDSL> {
    if (execute) {
      this._executeService.executeAnalysis(analysis, EXECUTION_MODES.PUBLISH);
    }
    if (type === 'schedule') {
      return this._analyzeService.changeSchedule(analysis).then(() => analysis);
    } else {
      try {
        await this.updateScheduleBeforePublishing(analysis, lastCategoryId);
        return <Promise<AnalysisDSL>>(
          this._analyzeService.updateAnalysis(analysis)
        );
      } catch (error) {
        throw new Error(`Cannot publish analysis. Error: ${error.message}`);
      }
    }
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
    analysis.schedule = job.jobDetails;

    return this._analyzeService.changeSchedule(analysis);
  }
}
