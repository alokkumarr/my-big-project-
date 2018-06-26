import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs/Subject';

import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import {
  Analysis,
  ArtifactColumn,
  Sort
} from '../../types';

const template = require('./executed-pivot-view.component.html');

@Component({
  selector: 'executed-pivot-view',
  template
})

export class ExecutedPivotViewComponent {
  @Input('analysis') set setAnalysis(analysis: Analysis) {
    this.analysis = analysis;
    this.artifactColumns = [...analysis.artifacts[0].columns];
    this.sorts = analysis.sqlBuilder.sorts;
  };
  @Input() data: any[];
  @Input() updater: Subject<IPivotGridUpdate>;

  analysis: Analysis
  artifactColumns: ArtifactColumn[];
  sorts: Sort[];

  constructor() { }

}
