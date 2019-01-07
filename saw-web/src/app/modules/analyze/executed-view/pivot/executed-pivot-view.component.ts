import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';

import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import { Analysis, ArtifactColumn, Sort } from '../../types';
import { SqlBuilderPivot } from '../../models';

import * as map from 'lodash/map';
import * as find from 'lodash/find';

@Component({
  selector: 'executed-pivot-view',
  templateUrl: 'executed-pivot-view.component.html',
  styleUrls: ['./executed-pivot-view.component.scss']
})
export class ExecutedPivotViewComponent {
  @Input('analysis')
  set setAnalysis(analysis: Analysis) {
    this.analysis = analysis;
    this.artifactColumns = this.getArtifactColumns(analysis);
    this.sorts = analysis.sqlBuilder.sorts;
  }
  @Input() data: any[];
  @Input() updater: Subject<IPivotGridUpdate>;

  analysis: Analysis;
  artifactColumns: ArtifactColumn[];
  sorts: Sort[];

  constructor() {}

  /* Use sqlBuilder to update selected fields in artifacts */
  getArtifactColumns(analysis: Analysis) {
    const row = (analysis.sqlBuilder as SqlBuilderPivot).rowFields;
    const column = (analysis.sqlBuilder as SqlBuilderPivot).columnFields;
    const data = (analysis.sqlBuilder as SqlBuilderPivot).dataFields;

    /* These counters are for legacy purpose. If areaIndex is not saved
     * in a field in sqlBuilder, then these will be used to get an area
     * index instead. This may result in re-ordering of columns */
    let rowId = 0,
      colId = 0,
      dataId = 0;

    return map(analysis.artifacts[0].columns, artifactColumn => {
      /* Find out if this column has been selected in row, column or data area */
      const isRow = find(row, c => c.columnName === artifactColumn.columnName);
      const isColumn = find(
        column,
        c => c.columnName === artifactColumn.columnName
      );
      const isData = find(
        data,
        c => c.columnName === artifactColumn.columnName
      );

      /* If column wasn't selected in any area, mark it unselected and return */
      if (!isRow && !isColumn && !isData) {
        return {
          ...artifactColumn,
          checked: false,
          area: null,
          areaIndex: null
        };
      }

      /* Otherwise, update area related fields accordingly */
      if (isRow) {
        return {
          ...artifactColumn,
          checked: true,
          area: 'row',
          areaIndex: isRow.areaIndex || rowId++
        };
      } else if (isColumn) {
        return {
          ...artifactColumn,
          checked: true,
          area: 'column',
          areaIndex: isColumn.areaIndex || colId++
        };
      } else if (isData) {
        return {
          ...artifactColumn,
          checked: true,
          area: 'data',
          areaIndex: isData.areaIndex || dataId++
        };
      }
    });
  }
}
