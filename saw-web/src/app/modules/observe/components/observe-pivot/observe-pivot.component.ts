import {
  Component,
  OnInit,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { GridsterItem } from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../../analyze/services/analyze.service';
import { flattenPivotData } from '../../../../common/utils/dataFlattener';

const style = require('./observe-pivot.component.scss');

@Component({
  selector: 'observe-pivot',
  templateUrl: './observe-pivot.component.html',
  styles: [
    `:host {
      display: block;
      height: 100%;
    }`,
    style
  ]
})
export class ObservePivotComponent implements OnInit {
  public artifactColumns: Array<any> = [];
  public data: any;

  @Input() analysis: any;
  @Input() item: GridsterItem;
  @Output() onRefresh = new EventEmitter();
  @Input() updater: BehaviorSubject<any>;

  constructor(
    public analyzeService: AnalyzeService
  ) {}

  ngOnInit() {
    this.artifactColumns = [...this.analysis.artifacts[0].columns];
    if (this.analysis._executeTile === false) { return; }
    this.analyzeService
      .getDataBySettings(this.analysis, EXECUTION_MODES.LIVE, {})
      .then(
        ({ data }) => {
          this.data = flattenPivotData(data, this.analysis.sqlBuilder);
        },
        err => {
          throw err;
        }
      );
  }
}
