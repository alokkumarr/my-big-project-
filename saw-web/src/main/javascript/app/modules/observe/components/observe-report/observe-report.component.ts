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
export class ObserveReportComponent implements OnInit, OnDestroy {
  @Input() item: GridsterItem;
  @Input() analysis: AnalysisReport;
  @Input() updater: BehaviorSubject<any>;

  @Output() onRefresh = new EventEmitter();

  data: Array<any> = [];

  listeners: Array<Subscription> = [];

  constructor(private analyzeService: AnalyzeService) {}

  ngOnInit() {
    this.analyzeService.previewExecution(this.analysis).then(({ data }) => {
      this.data = data;
    });
  }

  ngOnDestroy() {
    this.listeners.forEach(sub => sub.unsubscribe());
  }
}
