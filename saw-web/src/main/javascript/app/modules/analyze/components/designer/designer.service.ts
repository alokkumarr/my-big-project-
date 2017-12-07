import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as fpFilter from 'lodash/fp/filter';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpMap from 'lodash/fp/map';
import * as map from 'lodash/map';
import * as filter from 'lodash/filter';
import * as keys from 'lodash/keys';
import * as find from 'lodash/find';
import * as concat from 'lodash/concat';
import * as flatMap from 'lodash/flatMap';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpOmit from 'lodash/fp/omit';
import * as fpMapValues from 'lodash/fp/mapValues';
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
  NUMBER_TYPES,
  DATE_TYPES
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
      artifactColumn.checked = false;
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
    groupAdapters: IDEsignerSettingGroupAdapter[]): boolean {

    let addedSuccessfully = false;

    forEach(groupAdapters, (adapter: IDEsignerSettingGroupAdapter) => {
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
    groupAdapter: IDEsignerSettingGroupAdapter) {
    groupAdapter.reverseTransform(artifactColumn);
    remove(groupAdapter.artifactColumns, ({columnName}) => artifactColumn.columnName === columnName);
  }

  getPartialSqlBuilder(artifactColumns: ArtifactColumns, type: AnalysisType) {
    const pivotFields = fpPipe(
      fpFilter((artifactColumn: ArtifactColumnPivot) => artifactColumn.checked && artifactColumn.area),
      fpSortBy('areaIndex'),
      fpGroupBy('area'),
      fpMapValues(
        fpMap((artifactColumn: ArtifactColumnPivot) => {
          const isDataArea = artifactColumn.area === 'data'
          const isDateType = DATE_TYPES.includes(artifactColumn.type);
          return {
            type:         artifactColumn.type,
            columnName:   artifactColumn.columnName,
            aggregate:    isDataArea ? artifactColumn.aggregate : null,
            // the name propertie is needed for the elastic search
            name:         isDataArea ? artifactColumn.columnName : null,
            dateInterval: isDateType ? artifactColumn.dateInterval : null
          };
        })
      )
    )(artifactColumns);

    switch (type) {
    case 'pivot':
      return {
        rowFields: pivotFields.row || [],
        columnFields: pivotFields.column || [],
        // the data field must be non-empty
        dataFields: pivotFields.data
      };
    case 'chart':
    case 'report':
      return {};
    }
  }


  parseData(data, sqlBuilder) {
    const nodeFieldMap = this.getNodeFieldMap(sqlBuilder);

    return this.parseNode(data, {}, nodeFieldMap, 0);
  }

     /** Map the tree level to the columnName of the field
   * Example:
   * row_field_1: 0 -> SOURCE_OS
   * row_field_2: 1 -> SOURCE_MANUFACTURER
   * column_field_1: 2 -> TARGET_OS
   */
  getNodeFieldMap(sqlBuilder) {
    const rowFieldMap = map(sqlBuilder.rowFields, 'columnName');
    const columnFieldMap = map(sqlBuilder.columnFields, 'columnName');

    return concat(rowFieldMap, columnFieldMap);
  }

  parseNode(node, dataObj, nodeFieldMap, level) {
    if (node.key) {
      const columnName = this.getColumnName(nodeFieldMap, level);
      dataObj[columnName] = node.key_as_string || node.key;
    }

    const nodeName = this.getChildNodeName(node);
    if (nodeName && node[nodeName]) {
      const data = flatMap(node[nodeName].buckets, bucket => this.parseNode(bucket, dataObj, nodeFieldMap, level + 1));
      return data;
    }
    const datum = this.parseLeaf(node, dataObj);

    return datum;
  }

  parseLeaf(node, dataObj) {
    const dataFields = fpPipe(
      fpOmit(['doc_count', 'key', 'key_as_string']),
      fpMapValues('value')
    )(node);

    return {
      ...dataFields,
      ...dataObj
    };
  }

  getColumnName(fieldMap, level) {
    // take out the .keyword form the columnName
    // if there is one
    const columnName = fieldMap[level - 1];
    const split = columnName.split('.');
    if (split[1]) {
      return split[0];
    }
    return columnName;
  }

  getChildNodeName(node) {
    const nodeKeys = keys(node);
    const childNodeName = find(nodeKeys, key => {
      const isRow = key.indexOf('row_level') > -1;
      const isColumn = key.indexOf('column_level') > -1;
      return isRow || isColumn;
    });

    return childNodeName;
  }
}
