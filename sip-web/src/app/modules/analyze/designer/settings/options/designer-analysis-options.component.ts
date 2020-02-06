import { Component, Input, Output, EventEmitter } from '@angular/core';
import { PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';

import * as flatMap from 'lodash/flatMap';
import * as set from 'lodash/set';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';

import * as fpFilter from 'lodash/fp/filter';

import { ArtifactColumn, DesignerChangeEvent } from '../../types';
import { QueryDSL } from 'src/app/models';
import { CHART_COLORS } from 'src/app/common/consts';
import { DATA_AXIS } from '../../consts';

const DEFAULT_CHART_COLORS = cloneDeep(CHART_COLORS);

@Component({
  selector: 'designer-analysis-options',
  templateUrl: 'designer-analysis-options.component.html',
  styleUrls: ['designer-analysis-options.component.scss']
})
export class DesignerAnalysisOptionsComponent {
  @Output() change: EventEmitter<DesignerChangeEvent> = new EventEmitter();
  @Input() analysisType: string;
  @Input() analysisSubtype: string;
  @Input() auxSettings: any;
  @Input() chartTitle: string;
  @Input('sipQuery') set setArtifacts(sipQuery: QueryDSL) {
    this.sipQuery = sipQuery;
    this.selectedColumns = [];
    setTimeout(() => {
      this.selectedColumns = flatMap(this.sipQuery.artifacts, x => x.fields);
      this.setSeriesColorToEachDataOption();
    }, 0);
  }

  public sipQuery: QueryDSL;
  public selectedColumns: ArtifactColumn[];
  public config: PerfectScrollbarConfigInterface = {};

  selectedColsTrackByFn(_, column) {
    return column.columnName;
  }

  /**
   * This function takes care of assigning default color for input element of color picker.
   * Also it updates the default color when any series color is changed from custom color picker.
   * Added as part of SIP-9766 (A fix for SIP-10095).
   */
  setSeriesColorToEachDataOption() {
    const dataColumn = fpFilter(
      col => DATA_AXIS.includes(col.area) && !col.colorSetFromPicker
    )(this.selectedColumns);

    forEach(dataColumn, (col, index) => {
      set(col, 'seriesColor', DEFAULT_CHART_COLORS[index]);
      set(col, 'colorSetFromPicker', false);
    });
  }
}
