import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';

import { IPivotGridUpdate } from '../../../../common/components/pivot-grid/pivot-grid.component';
import { AnalyzeService } from '../../services/analyze.service';
import { ArtifactColumn, Sort, AnalysisDSL, Artifact } from '../../types';

import * as get from 'lodash/get';
import * as keys from 'lodash/keys';
import * as map from 'lodash/map';
import * as groupBy from 'lodash/groupBy';
import * as sortBy from 'lodash/sortBy';
import * as mapValues from 'lodash/mapValues';

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

  @Input() set artifacts(artifacts: Artifact[]) {
    this.nameMap = this.analyzeService.calcNameMap(artifacts);
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
    const artifact = analysis.sipQuery.artifacts[0];
    const groupedFields = groupBy(artifact.fields, 'area');
    const sortedGroupedFields = mapValues(groupedFields, group =>
      sortBy(group, 'areaIndex')
    );
    const { row = [], data = [], column = [] } = sortedGroupedFields;

    const allSortedFields = [...row, ...column, ...data];
    return map(allSortedFields, artifactColumn => {
      const artifactName =
        get(artifact, 'artifactsName') || keys(this.nameMap)[0];
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

      return {
        ...artifactColumn,
        displayName,
        caption: displayName,
        checked: true
      };
    });
  }
}
