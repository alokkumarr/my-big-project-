import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnDestroy
} from '@angular/core';
import { GridsterItem } from 'angular-gridster2';
import {
  AnalysisReport,
  AnalysisDSL,
  isDSLAnalysis
} from '../../../analyze/types';

import { BehaviorSubject, Subscription } from 'rxjs';
import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../../analyze/services/analyze.service';

@Component({
  selector: 'observe-report',
  templateUrl: './observe-report.component.html',
  styleUrls: ['./observe-report.component.scss']
})
export class ObserveReportComponent implements OnDestroy {
  @Input() item: GridsterItem;
  @Input() analysis: AnalysisReport | AnalysisDSL;
  @Input() updater: BehaviorSubject<any>;

  @Output() onRefresh = new EventEmitter();

  data: Array<any> = [];
  executionId: string;

  listeners: Array<Subscription> = [];

  dataLoader = this.loadData.bind(this);

  constructor(public analyzeService: AnalyzeService) {}

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  get analysisArtifacts() {
    if (this.analysis.edit) {
      return null;
    }

    if (isDSLAnalysis(this.analysis)) {
      return this.analysis.sipQuery.artifacts;
    } else {
      return this.analysis.artifacts;
    }
  }

  get analysisSorts() {
    if (isDSLAnalysis(this.analysis)) {
      return this.analysis.sipQuery.sorts;
    } else {
      return this.analysis.sqlBuilder.sorts;
    }
  }

  loadData(options = {}) {
    if ((this.analysis as any)._executeTile) {
      if (this.executionId) {
        return this.analyzeService
          .getExecutionData(this.analysis.id, this.executionId, {
            ...options,
            executionType: 'onetime',
            analysisType: this.analysis.type,
            isDSL: isDSLAnalysis(this.analysis)
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
