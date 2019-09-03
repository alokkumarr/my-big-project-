import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';

import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import { AnalyzeService } from '../../services/analyze.service';
import { ArtifactColumn, Sort, AnalysisDSL, Artifact } from '../../types';

import * as get from 'lodash/get';
import * as keys from 'lodash/keys';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as forEach from 'lodash/forEach';
import { isDSLAnalysis } from 'src/app/common/types';

@Component({
  selector: 'executed-pivot-view',
  templateUrl: 'executed-pivot-view.component.html',
  styleUrls: ['./executed-pivot-view.component.scss']
})
export class ExecutedPivotViewComponent {
  @Input('analysis')
  set setAnalysis(analysis: AnalysisDSL) {
    if (!analysis) {
      return;
    }
    this.analysis = analysis;
    this.artifactColumns = this.getArtifactColumns(analysis);
    this.sorts = analysis.sipQuery.sorts;
  }

  @Input() set artifacts(value: Artifact[]) {
    this.nameMap = this.analyzeService.calcNameMap(value);
    if (this.analysis) {
      this.artifactColumns = this.getArtifactColumns(this.analysis);
    }
  }

  @Input() data: any[];
  @Input() updater: Subject<IPivotGridUpdate>;

  analysis: AnalysisDSL;
  artifactColumns: ArtifactColumn[];
  sorts: Sort[];
  nameMap;

  constructor(private analyzeService: AnalyzeService) {}

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
    const artifactName =
      get(<AnalysisDSL>analysis, 'sipQuery.artifacts[0].artifactsName') ||
      keys(this.nameMap)[0];
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
        const tableName =
          artifactColumn.table ||
          artifactColumn.tableName ||
          artifactColumn.artifactName ||
          artifactColumn.artifactsName ||
          artifactName;

        const alias = artifactColumn.alias || artifactColumn.aliasName;
        const displayName =
          alias ||
          get(
            this.nameMap,
            [`${tableName}`, `${artifactColumn.columnName}`],
            artifactColumn.displayName
          );
        /* If column wasn't selected in any area, mark it unselected and return */
        if (!isRow && !isColumn && !isData) {
          return {
            ...artifactColumn,
            displayName,
            caption: displayName,
            checked: false,
            area: null,
            areaIndex: null
          };
        }

        /* Otherwise, update area related fields accordingly */
        if (isRow) {
          return {
            ...artifactColumn,
            displayName,
            caption: displayName,
            checked: true,
            area: 'row',
            areaIndex: isRow.areaIndex || rowId++
          };
        } else if (isColumn) {
          return {
            ...artifactColumn,
            displayName,
            caption: displayName,
            checked: true,
            area: 'column',
            areaIndex: isColumn.areaIndex || colId++
          };
        } else if (isData) {
          return {
            ...artifactColumn,
            displayName,
            caption: displayName,
            checked: true,
            area: 'data',
            areaIndex: isData.areaIndex || dataId++
          };
        }
      }
    );
  }
}
