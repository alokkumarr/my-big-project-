import {
  Component,
  OnInit,
  OnDestroy,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { GridsterItem } from 'angular-gridster2';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import { AnalyzeService } from '../../../analyze/services/analyze.service';
import { DesignerService } from '../../../analyze/components/designer/designer.service';

const template = require('./observe-pivot.component.html');
require('./observe-pivot.component.scss');

@Component({
  selector: 'observe-pivot',
  template
})
export class ObservePivotComponent implements OnInit, OnDestroy {
  public artifactColumns: Array<any> = [];
  public data: any;

  @Input() analysis: any;
  @Input() item: GridsterItem;
  @Output() onRefresh = new EventEmitter();
  @Input() updater: BehaviorSubject<any>;

  constructor(
    private analyzeService: AnalyzeService,
    private designerService: DesignerService
  ) {}

  ngOnInit() {
    this.artifactColumns = [...this.analysis.artifacts[0].columns];
    this.analyzeService.previewExecution(this.analysis).then(({ data }) => {
      this.data = this.designerService.parseData(
        data,
        this.analysis.sqlBuilder
      );
    });
  }
  ngOnDestroy() {}
}