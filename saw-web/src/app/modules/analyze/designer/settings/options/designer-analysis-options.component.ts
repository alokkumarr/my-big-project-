import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { SqlBuilder, Artifact, ArtifactColumn, DesignerChangeEvent } from '../../types';

import * as filter from 'lodash/filter';
import * as get from 'lodash/get';

@Component({
  selector: 'designer-analysis-options',
  templateUrl: 'designer-analysis-options.component.html',
  styleUrls: ['designer-analysis-options.component.scss']
})

export class DesignerAnalysisOptionsComponent implements OnInit {

  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() sqlBuilder: SqlBuilder;
  @Input('artifacts') set setArtifacts(artifacts: Artifact[]) {
    const cols = get(artifacts, '0.columns');
    this.selectedColumns = filter(cols, 'checked');
  }

  public selectedColumns: ArtifactColumn[];
  constructor() { }

  ngOnInit() { }
}
