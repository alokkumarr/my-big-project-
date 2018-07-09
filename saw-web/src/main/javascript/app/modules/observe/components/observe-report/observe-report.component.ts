import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnDestroy
} from '@angular/core';
import { GridsterItem } from 'angular-gridster2';
import { AnalysisReport } from '../../../analyze/types';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subscription } from 'rxjs/Subscription';
import { AnalyzeService } from '../../../analyze/services/analyze.service';

const template = require('./observe-report.component.html');
require('./observe-report.component.scss');

@Component({
  selector: 'observe-report',
  template
})
export class ObserveReportComponent implements OnDestroy {
  @Input() item: GridsterItem;
  @Input() analysis: AnalysisReport;
  @Input() updater: BehaviorSubject<any>;

  @Output() onRefresh = new EventEmitter();

  data: Array<any> = [];

  listeners: Array<Subscription> = [];

  dataLoader = this.loadData.bind(this);

  constructor(private analyzeService: AnalyzeService) {}

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }

  loadData(options = {}) {
    if ((this.analysis as any)._executeTile) {
      return this.analyzeService
        .previewExecution(this.analysis, options)
        .then(({ data, count }) => ({ data, totalCount: count }));
    } else {
      return new Promise(resolve => {
        resolve({ data: [], totalCount: 0 });
      });
    }
  }
}