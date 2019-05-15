import { Pipe, PipeTransform } from '@angular/core';
import * as filter from 'lodash/filter';
import * as find from 'lodash/find';
import { Analysis } from './types';

@Pipe({
  name: 'analysesFilter'
})

export class AnalysesFilterPipe implements PipeTransform {
  transform(analyses: Analysis[], type, cronJobs): Analysis[] {
    if (type === 'all') {
      return analyses;
    }
    return filter(analyses, analysis => {
      switch (type) {
      case 'scheduled':
        return this.isInCronJobs(cronJobs, analysis.id);
      case 'report':
        return analysis.type === 'esReport' || analysis.type === type;
      default:
        return type === analysis.type;
      }
    });
  }

  isInCronJobs(cronJobs, id) {
    const cronJob = find(cronJobs, cron => cron.jobDetails.analysisID === id);
    return Boolean(cronJob);
  }
}
