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
  @Input('auxSettings') set setAuxSettings(auxSettings: any) {
    this.auxSettings = auxSettings;
    this.limitByAxis = this.auxSettings.limitByAxis || 'dimension';
  };
  @Input() chartTitle: string;
  @Input('sipQuery') set setArtifacts(sipQuery: QueryDSL) {
    this.sipQuery = sipQuery;
    this.selectedColumns = flatMap(this.sipQuery.artifacts, x => x.fields);
  }

  public sipQuery: QueryDSL;
  public auxSettings;
  public selectedColumns: ArtifactColumn[];
  public config: PerfectScrollbarConfigInterface = {};
  public limitByAxis: string;

  selectedColsTrackByFn(_, column) {
    return column.columnName;
  }
}
