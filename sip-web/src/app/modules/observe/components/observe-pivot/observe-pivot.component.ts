import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Analysis, Sort, AnalysisDSL } from './../../../analyze/types';
import { GridsterItem } from 'angular-gridster2';
import { BehaviorSubject } from 'rxjs';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';

import {
  AnalyzeService,
  EXECUTION_MODES
} from '../../../analyze/services/analyze.service';
import { flattenPivotData } from '../../../../common/utils/dataFlattener';
import { isDSLAnalysis } from 'src/app/common/types';

@Component({
  selector: 'observe-pivot',
  templateUrl: './observe-pivot.component.html',
  styleUrls: ['./observe-pivot.component.scss']
})
export class ObservePivotComponent implements OnInit {
  public artifactColumns: Array<any> = [];
  public data: any;
  public sorts: Sort;

  @Input() analysis: any;
  @Input() item: GridsterItem;
  @Output() onRefresh = new EventEmitter();
  @Input() updater: BehaviorSubject<any>;

  constructor(public analyzeService: AnalyzeService) {}

  ngOnInit() {
    this.artifactColumns = this.configureArtifacts(
      (<AnalysisDSL>this.analysis).sipQuery.artifacts[0].fields
    );
    this.sorts = isDSLAnalysis(this.analysis)
      ? this.analysis.sipQuery.sorts
      : this.analysis.sqlBuilder.sorts;
    if (this.analysis._executeTile === false) {
      return;
    }
    this.analyzeService
      .getDataBySettings(this.analysis, EXECUTION_MODES.LIVE, {})
      .then(
        ({ data }) => {
          this.data = flattenPivotData(
            data,
            (<AnalysisDSL>this.analysis).sipQuery ||
              (<Analysis>this.analysis).sqlBuilder
          );
        },
        err => {
          throw err;
        }
      );
  }

  configureArtifacts(fields) {
    return fpPipe(
      fpMap(value => {
        value.checked = true;
        return value;
      })
    )(fields);
  }
}
