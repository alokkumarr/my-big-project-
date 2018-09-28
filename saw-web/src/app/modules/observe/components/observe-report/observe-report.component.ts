import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnDestroy
} from '@angular/core';
import { GridsterItem } from 'angular-gridster2';
import { AnalysisReport } from '../../../analyze/types';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';
import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../../analyze/services/analyze.service';

const style = require('./observe-report.component.scss');

@Component({
  selector: 'observe-report',
  templateUrl: './observe-report.component.html',
  styles: [
    `:host {
      display: flex;
      flex-direction: column;
      height: 100%;
    }`,
    style
  ]
})
export class ObserveReportComponent implements OnDestroy {
  @Input() item: GridsterItem;
  @Input() analysis: AnalysisReport;
  @Input() updater: BehaviorSubject<any>;

  @Output() onRefresh = new EventEmitter();

  data: Array<any> = [];
  executionId: string;

  listeners: Array<Subscription> = [];

  dataLoader = this.loadData.bind(this);

  constructor(
    private analyzeService: AnalyzeService
  ) {}

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  loadData(options = {}) {
    if ((this.analysis as any)._executeTile) {
      if (this.executionId) {
        return this.analyzeService
          .getExecutionData(this.analysis.id, this.executionId, {
            ...options,
            executionType: 'onetime',
            analysisType: this.analysis.type
          })
          .then(({ data, count }) => ({ data, totalCount: count }));
      } else {
        return this.analyzeService
          .getDataBySettings(this.analysis, EXECUTION_MODES.LIVE, options)
          .then(
            ({ data, executionId, count }) => {
              this.executionId = executionId;
              return { data, totalCount: count };
            },
            err => {
              throw err;
            }
          );
      }
    } else {
      return new Promise(resolve => {
        resolve({ data: [], totalCount: 0 });
      });
    }
  }
}
