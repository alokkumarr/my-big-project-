import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpGroupBy from 'lodash/fp/groupBy';
import { Injectable } from '@angular/core';
import { AnalyzeService } from '../../services/analyze.service'
import {
  AnalysisType,
  ChartType,
  Analysis
} from '../../types';
import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnPivot
} from './types';
import {
  NUMBER_TYPES
} from '../../consts';

@Injectable()
export class DesignerService {
  constructor(private _analyzeService: AnalyzeService) { }

  createAnalysis(semanticId: string, type: AnalysisType): Promise<Analysis> {
    return this._analyzeService.createAnalysis(semanticId, type);
  }

  getDataForAnalysis(analysis) {
    return this._analyzeService.getDataBySettings(analysis);
  }

  public getPivotGroupAdapters(artifactColumns): IDEsignerSettingGroupAdapter[] {

    const pivotReverseTransform = (artifactColumn: ArtifactColumnPivot) => {
      artifactColumn.area = null;
    }

    const pivotGroupAdapters =  [{
      title: 'Data',
      marker: 'data',
      artifactColumns: [],
      canAcceptArtifactColumn(artifactColumn: ArtifactColumnPivot) {
        return NUMBER_TYPES.includes(artifactColumn.type);
      },
      transform(artifactColumn: ArtifactColumnPivot) {
        artifactColumn.area = 'data';
        artifactColumn.checked = true;
      },
      reverseTransform: pivotReverseTransform
    }, {
      title: 'Row',
      marker: 'row',
      artifactColumns: [],
      canAcceptArtifactColumn(artifactColumn: ArtifactColumnPivot) {
        return !NUMBER_TYPES.includes(artifactColumn.type);
      },
      transform(artifactColumn: ArtifactColumnPivot) {
        artifactColumn.area = 'row';
        artifactColumn.checked = true;
      },
      reverseTransform: pivotReverseTransform
    }, {
      title: 'Column',
      marker: 'column',
      artifactColumns: [],
      canAcceptArtifactColumn(artifactColumn: ArtifactColumnPivot) {
        return !NUMBER_TYPES.includes(artifactColumn.type);
      },
      transform(artifactColumn: ArtifactColumnPivot) {
        artifactColumn.area = 'column';
        artifactColumn.checked = true;
      },
      reverseTransform: pivotReverseTransform
    }];

    this._distributeArtifactColumnsIntoGroups(
      artifactColumns,
      pivotGroupAdapters,
      'pivot'
    );

    return pivotGroupAdapters;
  }

  // getChartGroupAdapters(chartType: ChartType) {

  // }

  private _distributeArtifactColumnsIntoGroups(
    artifactColumns: ArtifactColumns,
    pivotGroupAdapters: IDEsignerSettingGroupAdapter[],
    analysisType: 'chart' | 'pivot'
  ) {
    const groupByProps = {
      chart: 'checked',
      pivot: 'area'
    }
    fpPipe(
      fpFilter('checked'),
      fpGroupBy(groupByProps[analysisType]),
      groupedColumns => {
        forEach(pivotGroupAdapters, adapter => {
          adapter.artifactColumns = groupedColumns[adapter.marker] || [];
        });
      }
    )(artifactColumns);

    return pivotGroupAdapters;
  }

  addArtifactColumnIntoGroup(
    artifactColumn: ArtifactColumn,
    pivotGroupAdapters: IDEsignerSettingGroupAdapter[]): boolean {

    let addedSuccessfully = false;

    forEach(pivotGroupAdapters, (adapter: IDEsignerSettingGroupAdapter) => {
      if (adapter.canAcceptArtifactColumn(artifactColumn)) {
        adapter.transform(artifactColumn);
        adapter.artifactColumns = [...adapter.artifactColumns, artifactColumn];
        addedSuccessfully = true;
        return false;
      }
    });
    return addedSuccessfully;
  }

  removeArtifactColumnFromGroup(
    artifactColumn: ArtifactColumn,
    pivotGroupAdapter: IDEsignerSettingGroupAdapter) {
    pivotGroupAdapter.reverseTransform(artifactColumn);
    remove(pivotGroupAdapter.artifactColumns, ({columnName}) => artifactColumn.columnName === columnName);
  }
}
