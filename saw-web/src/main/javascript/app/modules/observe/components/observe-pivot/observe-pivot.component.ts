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
import { HeaderProgressService } from '../../../../common/services/header-progress.service';
import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../../analyze/services/analyze.service';
import { flattenPivotData } from '../../../../common/utils/dataFlattener';

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
    private progressService: HeaderProgressService
  ) {}

  ngOnInit() {
    this.artifactColumns = [...this.analysis.artifacts[0].columns];
    if (this.analysis._executeTile === false) return;
    this.progressService.show();
    this.analyzeService
      .getDataBySettings(this.analysis, EXECUTION_MODES.LIVE, {})
      .then(
        ({ data }) => {
          this.progressService.hide();
          this.data = flattenPivotData(data, this.analysis.sqlBuilder);
        },
        err => {
          this.progressService.hide();
          throw err;
        }
      );
  }
  ngOnDestroy() {}
}
