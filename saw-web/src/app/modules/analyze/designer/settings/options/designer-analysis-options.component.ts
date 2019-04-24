import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import * as flatMap from 'lodash/flatMap';

import { ArtifactColumn, DesignerChangeEvent } from '../../types';
import { QueryDSL } from 'src/app/models';

@Component({
  selector: 'designer-analysis-options',
  templateUrl: 'designer-analysis-options.component.html',
  styleUrls: ['designer-analysis-options.component.scss']
})
export class DesignerAnalysisOptionsComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() fieldCount: number;
  @Input() auxSettings: any;
  @Input() chartTitle: string;
  @Input('sipQuery') set setArtifacts(sipQuery: QueryDSL) {
    this.sipQuery = sipQuery;
    this.selectedColumns = flatMap(sipQuery.artifacts, x => x.fields);
  }

  public sipQuery: QueryDSL;
  public selectedColumns: ArtifactColumn[];
  public config: PerfectScrollbarConfigInterface = {};

  selectedColsTrackByFn(_, column) {
    return column.columnName;
  }
}
