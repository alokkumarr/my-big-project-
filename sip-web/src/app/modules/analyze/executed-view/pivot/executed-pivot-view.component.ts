import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';

import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import { ArtifactColumn, Sort, isDSLAnalysis, AnalysisDSL } from '../../types';

import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';

@Component({
  selector: 'executed-pivot-view',
  templateUrl: 'executed-pivot-view.component.html',
  styleUrls: ['./executed-pivot-view.component.scss']
})
export class ExecutedPivotViewComponent {
  @Input('analysis')
  set setAnalysis(analysis: AnalysisDSL) {
    this.analysis = analysis;
    this.artifactColumns = this.getArtifactColumns(analysis);
    this.sorts = analysis.sipQuery.sorts;
  }
  @Input() data: any[];
  @Input() updater: Subject<IPivotGridUpdate>;

  analysis: AnalysisDSL;
  artifactColumns: ArtifactColumn[];
  sorts: Sort[];

  constructor() {}

  /* Use sqlBuilder to update selected fields in artifacts */
  getArtifactColumns(analysis: AnalysisDSL) {
    const row = [];
    const data = [];
    const column = [];
    if (isDSLAnalysis(analysis)) {
      forEach(analysis.sipQuery.artifacts, table => {
        forEach(table.fields, field => {
          if (field.area === 'row') {
            row.push(field);
          }
          if (field.area === 'data') {
            data.push(field);
          }
          if (field.area === 'column') {
            column.push(field);
          }
        });
      });
    }

    /* These counters are for legacy purpose. If areaIndex is not saved
     * in a field in sqlBuilder, then these will be used to get an area
     * index instead. This may result in re-ordering of columns */
    let rowId = 0,
      colId = 0,
      dataId = 0;
    return map(
      (<AnalysisDSL>analysis).sipQuery.artifacts[0].fields,
      artifactColumn => {
        /* Find out if this column has been selected in row, column or data area */
        const isRow = find(
          row,
          c => c.columnName === artifactColumn.columnName
        );
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
      }
    );
  }
}
