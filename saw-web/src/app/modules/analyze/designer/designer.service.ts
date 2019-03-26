import * as forEach from 'lodash/forEach';
import * as remove from 'lodash/remove';
import * as fpFilter from 'lodash/fp/filter';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpMap from 'lodash/fp/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as take from 'lodash/take';
import * as takeRight from 'lodash/takeRight';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';
import * as compact from 'lodash/compact';
import * as isFunction from 'lodash/isFunction';

import { Injectable } from '@angular/core';
import { AnalyzeService } from '../services/analyze.service';
import { AnalysisType, Analysis } from '../types';
import { AnalysisDSL } from '../../../models';

import {
  IDEsignerSettingGroupAdapter,
  ArtifactColumn,
  ArtifactColumns,
  ArtifactColumnPivot,
  ArtifactColumnChart,
  SqlBuilderPivot,
  SqlBuilderEsReport,
  SqlBuilderChart
} from './types';
import {
  NUMBER_TYPES,
  DATE_TYPES,
  GEO_TYPES,
  DEFAULT_AGGREGATE_TYPE,
  DEFAULT_DATE_INTERVAL,
  DEFAULT_DATE_FORMAT,
  CHART_DEFAULT_DATE_FORMAT
} from '../consts';

const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

const onReorder = (columns: ArtifactColumns) => {
  forEach(columns, (column, index) => {
    column.areaIndex = index;
  });
};

const canAcceptNumberType = ({ type }: ArtifactColumnChart) =>
  NUMBER_TYPES.includes(type);
const canAcceptDateType = ({ type }: ArtifactColumnChart) =>
  DATE_TYPES.includes(type);
const canAcceptGeoType = ({ geoType }: ArtifactColumnChart) =>
  geoType !== 'lngLat' && GEO_TYPES.includes(geoType);
const canAcceptLngLat = ({ geoType }: ArtifactColumnChart) =>
  geoType === 'lngLat';
const canAcceptAnyType = () => true;

@Injectable()
export class DesignerService {
  constructor(private _analyzeService: AnalyzeService) {}

  createAnalysis(
    semanticId: string,
    type: AnalysisType
  ): Promise<Analysis | AnalysisDSL> {
    return this._analyzeService.createAnalysis(semanticId, type);
  }

  generateReportPayload(analysis) {
    forEach(analysis.artifacts, cols => {
      forEach(cols.columns, col => {
        delete col.checked;
      });
    });

    /* Remove analysis queryManual if not being saved from query mode */
    if (!analysis.edit) {
      delete analysis.queryManual;
    }

    return analysis;
  }

  getDataForAnalysis(analysis) {
    const analysisRequest =
      analysis.type === 'report'
        ? this.generateReportPayload(cloneDeep(analysis))
        : analysis;
    return this._analyzeService.getDataBySettings(analysisRequest);
  }

  getDataForAnalysisPreview(analysis, options) {
    return this._analyzeService.previewExecution(analysis, options);
  }

  getDataForExecution(analysisId, executionId, options) {
    return this._analyzeService.getExecutionData(
      analysisId,
      executionId,
      options
    );
  }

  getCategories(privilege) {
    return this._analyzeService.getCategories(privilege);
  }

  saveAnalysis(analysis) {
    const analysisRequest =
      analysis.type === 'report'
        ? this.generateReportPayload(cloneDeep(analysis))
        : analysis;
    return this._analyzeService.saveReport(analysisRequest);
  }

  public getPivotGroupAdapters(
    artifactColumns
  ): IDEsignerSettingGroupAdapter[] {
    const pivotReverseTransform = (artifactColumn: ArtifactColumnPivot) => {
      artifactColumn.area = null;
      artifactColumn.areaIndex = null;
      artifactColumn.checked = false;
    };

    const areMoreThenMaxFields = (columns: ArtifactColumns): boolean => {
      return columns.length >= MAX_POSSIBLE_FIELDS_OF_SAME_AREA;
    };

    const maxAllowedDecorator = (
      typeFn: (column: ArtifactColumnChart) => boolean
    ) => (groupAdapter: IDEsignerSettingGroupAdapter, _) => (
      column: ArtifactColumnChart
    ) => {
      if (areMoreThenMaxFields(groupAdapter.artifactColumns)) {
        return false;
      }
      return typeFn(column);
    };

    const applyDataFieldDefaults = artifactColumn => {
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
    };

    const applyNonDatafieldDefaults = artifactColumn => {
      artifactColumn.dateInterval = DEFAULT_DATE_INTERVAL.value;
    };

    const canAcceptDataType = canAcceptNumberType;
    const canAcceptInData = () => canAcceptDataType;
    const canAcceptRowType = canAcceptAnyType;
    const canAcceptInRow = maxAllowedDecorator(canAcceptRowType);
    const canAcceptColumnType = canAcceptAnyType;
    const canAcceptInColumn = maxAllowedDecorator(canAcceptColumnType);

    const pivotGroupAdapters: Array<IDEsignerSettingGroupAdapter> = [
      {
        title: 'Data',
        type: 'pivot',
        marker: 'data',
        artifactColumns: [],
        canAcceptArtifactColumnOfType: canAcceptDataType,
        canAcceptArtifactColumn: canAcceptInData,
        transform(artifactColumn: ArtifactColumnPivot) {
          artifactColumn.area = 'data';
          artifactColumn.checked = true;
          applyDataFieldDefaults(artifactColumn);
        },
        reverseTransform: pivotReverseTransform,
        onReorder
      },
      {
        title: 'Row',
        type: 'pivot',
        marker: 'row',
        artifactColumns: [],
        canAcceptArtifactColumnOfType: canAcceptRowType,
        canAcceptArtifactColumn: canAcceptInRow,
        transform(artifactColumn: ArtifactColumnPivot) {
          artifactColumn.area = 'row';
          artifactColumn.checked = true;
          applyNonDatafieldDefaults(artifactColumn);
        },
        reverseTransform: pivotReverseTransform,
        onReorder
      },
      {
        title: 'Column',
        type: 'pivot',
        marker: 'column',
        artifactColumns: [],
        canAcceptArtifactColumnOfType: canAcceptColumnType,
        canAcceptArtifactColumn: canAcceptInColumn,
        transform(artifactColumn: ArtifactColumnPivot) {
          artifactColumn.area = 'column';
          artifactColumn.checked = true;
          applyNonDatafieldDefaults(artifactColumn);
        },
        reverseTransform: pivotReverseTransform,
        onReorder
      }
    ];

    this._distributeArtifactColumnsIntoGroups(
      artifactColumns,
      pivotGroupAdapters,
      'pivot'
    );

    return pivotGroupAdapters;
  }

  getMapGroupAdapters(
    artifactColumns,
    subType
  ): IDEsignerSettingGroupAdapter[] {
    const mapReverseTransform = (artifactColumn: ArtifactColumnPivot) => {
      artifactColumn.area = null;
      artifactColumn.areaIndex = null;
      artifactColumn.checked = false;
    };

    const maxAllowedDecorator = (
      typeFn: (column: ArtifactColumnChart) => boolean
    ) => {
      return (
        groupAdapter: IDEsignerSettingGroupAdapter,
        groupAdapters: Array<IDEsignerSettingGroupAdapter>
      ) => (column: ArtifactColumnChart) => {
        const maxAllowed = groupAdapter.maxAllowed(groupAdapter, groupAdapters);
        if (groupAdapter.artifactColumns.length >= maxAllowed) {
          return false;
        }
        return typeFn(column);
      };
    };

    const applyDataFieldDefaults = artifactColumn => {
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
    };

    const canAcceptMetricType = canAcceptNumberType;
    const canAcceptInMetric = maxAllowedDecorator(canAcceptMetricType);
    const metricTitle = subType === 'map' ? 'Data' : 'Metric';

    const metricAdapter: IDEsignerSettingGroupAdapter = {
      title: metricTitle,
      type: 'map',
      marker: 'y',
      maxAllowed: () => Infinity,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptMetricType,
      canAcceptArtifactColumn: canAcceptInMetric,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'y';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn);
      },
      reverseTransform: mapReverseTransform,
      onReorder
    };

    const canAcceptDimensionType =
      subType === 'map' ? canAcceptLngLat : canAcceptGeoType;
    const canAcceptInDimension = maxAllowedDecorator(canAcceptDimensionType);
    const dimensionTitle = subType === 'map' ? 'Coordinates' : 'Dimension';

    const dimensionAdapter: IDEsignerSettingGroupAdapter = {
      title: dimensionTitle,
      type: 'map',
      marker: 'x',
      maxAllowed: () => 1,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptDimensionType,
      canAcceptArtifactColumn: canAcceptInDimension,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'x';
        artifactColumn.checked = true;
      },
      reverseTransform: mapReverseTransform,
      onReorder
    };

    const mapGroupAdapters: Array<IDEsignerSettingGroupAdapter> = compact([
      metricAdapter,
      dimensionAdapter
    ]);

    this._distributeArtifactColumnsIntoGroups(
      artifactColumns,
      mapGroupAdapters,
      'map'
    );

    return mapGroupAdapters;
  }

  getChartGroupAdapters(
    artifactColumns,
    chartType
  ): IDEsignerSettingGroupAdapter[] {
    const isStockChart = chartType.substring(0, 2) === 'ts';
    const chartReverseTransform = (artifactColumn: ArtifactColumnChart) => {
      artifactColumn.area = null;
      artifactColumn.checked = false;
    };

    const metricTitle = chartType === 'pie' ? 'Angle' : 'Metrics';
    const groupByTitle = chartType === 'bubble' ? 'Color By' : 'Group By';
    const dimensionTitle = chartType === 'pie' ? 'Color By' : 'Dimension';

    const maxAllowedDecorator = (
      typeFn: (column: ArtifactColumnChart) => boolean,
      rejectFn?: (
        groupAdapter: IDEsignerSettingGroupAdapter,
        groupAdapters: Array<IDEsignerSettingGroupAdapter>
      ) => boolean
    ) => {
      return (
        groupAdapter: IDEsignerSettingGroupAdapter,
        groupAdapters: Array<IDEsignerSettingGroupAdapter>
      ) => (column: ArtifactColumnChart) => {
        const maxAllowed = groupAdapter.maxAllowed(groupAdapter, groupAdapters);
        if (groupAdapter.artifactColumns.length >= maxAllowed) {
          return false;
        }
        if (isFunction(rejectFn) && rejectFn(groupAdapter, groupAdapters)) {
          return false;
        }
        if (groupAdapter.title === groupByTitle) {
        }
        if (groupAdapter.title === metricTitle) {
        }
        return typeFn(column);
      };
    };

    const groupByRejectFn = (
      _,
      groupAdapters: Array<IDEsignerSettingGroupAdapter>
    ) => {
      const metricGroupAdapter = find(
        groupAdapters,
        adapter => adapter.title === metricTitle
      );
      return metricGroupAdapter.artifactColumns.length > 1;
    };
    const metricRejectFn = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      groupAdapters: Array<IDEsignerSettingGroupAdapter>
    ) => {
      const groupByGroupAdapter = find(
        groupAdapters,
        adapter => adapter.title === groupByTitle
      );
      return (
        groupByGroupAdapter.artifactColumns.length > 0 &&
        groupAdapter.artifactColumns.length === 1
      );
    };

    const canAcceptMetricType = canAcceptNumberType;
    const canAcceptInMetric = maxAllowedDecorator(
      canAcceptMetricType,
      metricRejectFn
    );

    const canAcceptSizeType = canAcceptNumberType;
    const canAcceptInSize = maxAllowedDecorator(canAcceptSizeType);

    const canAcceptGroupByType = canAcceptAnyType;
    const canAcceptInGroupBy = maxAllowedDecorator(
      canAcceptGroupByType,
      groupByRejectFn
    );

    const canAcceptDimensionType = isStockChart
      ? canAcceptDateType
      : canAcceptAnyType;
    const canAcceptInDimension = maxAllowedDecorator(canAcceptDimensionType);

    const applyDataFieldDefaults = artifactColumn => {
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
      if (['column', 'line', 'area'].includes(chartType)) {
        artifactColumn.comboType = chartType;
      } else if (['tsspline', 'tsPane'].includes(chartType)) {
        artifactColumn.comboType = 'line';
      } else if (['combo', 'bar'].includes(chartType)) {
        artifactColumn.comboType = 'column';
      }
    };

    const applyNonDatafieldDefaults = artifactColumn => {
      if (DATE_TYPES.includes(artifactColumn.type)) {
        artifactColumn.dateFormat = CHART_DEFAULT_DATE_FORMAT.value;
      }
    };

    const metricAdapter: IDEsignerSettingGroupAdapter = {
      title: metricTitle,
      type: 'chart',
      marker: 'y',
      maxAllowed: () =>
        ['pie', 'bubble', 'stack'].includes(chartType) ? 1 : Infinity,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptMetricType,
      canAcceptArtifactColumn: canAcceptInMetric,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'y';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn);
      },
      reverseTransform: chartReverseTransform,
      onReorder
    };

    const sizeAdapter: IDEsignerSettingGroupAdapter = {
      title: 'Size',
      type: 'chart' as AnalysisType,
      marker: 'z',
      maxAllowed: () => 1,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptSizeType,
      canAcceptArtifactColumn: canAcceptInSize,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'z';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn);
      },
      reverseTransform: chartReverseTransform,
      onReorder
    };

    const groupByAdapter: IDEsignerSettingGroupAdapter = {
      title: groupByTitle,
      type: 'chart',
      marker: 'g',
      maxAllowed: (_, groupAdapters) => {
        /* Don't allow columns in 'group by' if more than one column on y axis */
        return find(groupAdapters, ad => ad.marker === 'y').artifactColumns
          .length > 1
          ? 0
          : 1;
      },
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptGroupByType,
      canAcceptArtifactColumn: canAcceptInGroupBy,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'g';
        artifactColumn.checked = true;
        applyNonDatafieldDefaults(artifactColumn);
      },
      reverseTransform: chartReverseTransform,
      onReorder
    };

    const dimensionAdapter: IDEsignerSettingGroupAdapter = {
      title: dimensionTitle,
      type: 'chart',
      marker: 'x',
      maxAllowed: () => 1,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptDimensionType,
      canAcceptArtifactColumn: canAcceptInDimension,
      transform(artifactColumn: ArtifactColumnChart) {
        artifactColumn.area = 'x';
        artifactColumn.checked = true;
        applyNonDatafieldDefaults(artifactColumn);
      },
      reverseTransform: chartReverseTransform,
      onReorder
    };

    const chartGroupAdapters: Array<IDEsignerSettingGroupAdapter> = compact([
      metricAdapter,
      dimensionAdapter,
      /* prettier-ignore */
      chartType === 'bubble' ?
        sizeAdapter : null,
      groupByAdapter
    ]);

    this._distributeArtifactColumnsIntoGroups(
      artifactColumns,
      chartGroupAdapters,
      'chart'
    );

    return chartGroupAdapters;
  }

  public _distributeArtifactColumnsIntoGroups(
    artifactColumns: ArtifactColumns,
    groupAdapters: IDEsignerSettingGroupAdapter[],
    analysisType: 'chart' | 'pivot' | 'map'
  ) {
    const groupByProps = {
      chart: 'area',
      pivot: 'area',
      map: 'area'
    };
    fpPipe(
      fpFilter('checked'),
      fpSortBy('areaIndex'),
      fpGroupBy(groupByProps[analysisType]),
      groupedColumns => {
        forEach(groupAdapters, adapter => {
          adapter.artifactColumns = groupedColumns[adapter.marker] || [];
        });
      }
    )(artifactColumns);

    return groupAdapters;
  }

  addArtifactColumnIntoAGroup(
    artifactColumn: ArtifactColumn,
    groupAdapters: IDEsignerSettingGroupAdapter[]
  ): boolean {
    let addedSuccessfully = false;

    forEach(groupAdapters, (adapter: IDEsignerSettingGroupAdapter) => {
      if (
        adapter.canAcceptArtifactColumn(adapter, groupAdapters)(artifactColumn)
      ) {
        this.addArtifactColumnIntoGroup(artifactColumn, adapter, 0);
        addedSuccessfully = true;
        return false;
      }
    });
    return addedSuccessfully;
  }

  getGroupsThatCanRecieve(
    artifactColumn: ArtifactColumn,
    groupAdapters: IDEsignerSettingGroupAdapter[]
  ): IDEsignerSettingGroupAdapter[] {
    return filter(groupAdapters, (adapter: IDEsignerSettingGroupAdapter) =>
      adapter.canAcceptArtifactColumn(adapter, groupAdapters)(artifactColumn)
    );
  }

  addArtifactColumnIntoGroup(
    artifactColumn: ArtifactColumn,
    adapter: IDEsignerSettingGroupAdapter,
    index: number
  ) {
    const array = adapter.artifactColumns;
    adapter.transform(artifactColumn);

    const firstN = take(array, index);
    const lastN = takeRight(array, array.length - index);
    adapter.artifactColumns = [...firstN, artifactColumn, ...lastN];
    adapter.onReorder(adapter.artifactColumns);
  }

  removeArtifactColumnFromGroup(
    artifactColumn: ArtifactColumn,
    adapter: IDEsignerSettingGroupAdapter
  ) {
    adapter.reverseTransform(artifactColumn);
    remove(
      adapter.artifactColumns,
      ({ columnName }) => artifactColumn.columnName === columnName
    );
    adapter.onReorder(adapter.artifactColumns);
  }

  removeAllArtifactColumnsFromGroup(adapter: IDEsignerSettingGroupAdapter) {
    const cols = adapter.artifactColumns;
    forEach(cols, col => adapter.reverseTransform(col));
    adapter.artifactColumns = [];
  }

  getPartialPivotSqlBuilder(
    artifactColumns: ArtifactColumns
  ): Partial<SqlBuilderPivot> {
    const pivotFields = fpPipe(
      fpFilter(
        (artifactColumn: ArtifactColumnPivot) =>
          artifactColumn.checked && artifactColumn.area
      ),
      fpSortBy('areaIndex'),
      fpGroupBy('area'),
      fpMapValues(
        fpMap((artifactColumn: ArtifactColumnPivot) => {
          const isDataArea = artifactColumn.area === 'data';
          const isDateType = DATE_TYPES.includes(artifactColumn.type);
          return {
            type: artifactColumn.type,
            columnName: artifactColumn.columnName,
            area: artifactColumn.area,
            areaIndex: artifactColumn.areaIndex,
            aggregate: isDataArea ? artifactColumn.aggregate : null,
            // the name propertie is needed for the elastic search
            name: isDataArea ? artifactColumn.columnName : null,
            dateInterval: isDateType ? artifactColumn.dateInterval : null,
            // the name propert is needed for the elastic search
            /* prettier-ignore */
            ...(isDateType ? {
              dateFormat:
                artifactColumn.dateFormat || DEFAULT_DATE_FORMAT.value
            } : {format: artifactColumn.format})
          };
        })
      )
    )(artifactColumns);
    return {
      rowFields: pivotFields.row || [],
      columnFields: pivotFields.column || [],
      // the data field must be non-empty
      dataFields: pivotFields.data
    };
  }

  getPartialChartSqlBuilder(
    artifactColumns: ArtifactColumns
  ): Partial<SqlBuilderChart> {
    const chartFields = fpPipe(
      fpFilter(
        (artifactColumn: ArtifactColumnChart) =>
          artifactColumn.checked && artifactColumn.area
      ),
      fpSortBy('areaIndex'),
      fpGroupBy('area'),
      fpMapValues(
        fpMap((artifactColumn: ArtifactColumnChart) => {
          const isDataArea = ['y', 'z'].includes(artifactColumn.area);
          const isDateType = DATE_TYPES.includes(artifactColumn.type);
          return {
            ...(isDataArea ? { aggregate: artifactColumn.aggregate } : {}),
            alias: artifactColumn.aliasName,
            checked: artifactColumn.area,
            columnName: artifactColumn.columnName,
            name: artifactColumn.columnName,
            comboType: artifactColumn.comboType,
            displayName: artifactColumn.displayName,
            table: artifactColumn.table,
            tableName: artifactColumn.table,
            type: artifactColumn.type,
            limitValue: artifactColumn.limitValue,
            limitType: artifactColumn.limitType,
            geoType: artifactColumn.geoType,
            region: artifactColumn.region,
            // the name propert is needed for the elastic search
            /* prettier-ignore */
            ...(isDateType ? {
              dateFormat:
                artifactColumn.dateFormat || CHART_DEFAULT_DATE_FORMAT.value
            } : {})
          };
        })
      )
    )(artifactColumns);

    return {
      dataFields: [...(chartFields.y || []), ...(chartFields.z || [])],
      nodeFields: [...(chartFields.x || []), ...(chartFields.g || [])]
    };
  }

  getPartialESReportSqlBuilder(
    artifactColumns: ArtifactColumns
  ): Partial<SqlBuilderEsReport> {
    return {
      dataFields: filter(artifactColumns, 'checked')
    };
  }

  generateReportDataField(columns) {
    const dataFields = [];
    forEach(columns, cols => {
      const checkedRows = this.getPartialReportSqlBuilder(cols.columns);
      if (!isEmpty(checkedRows)) {
        dataFields.push({
          tableName: cols.artifactName,
          columns: checkedRows
        });
      }
    });
    return dataFields;
  }

  getPartialReportSqlBuilder(
    artifactColumns: ArtifactColumns
  ): Partial<SqlBuilderEsReport> {
    return filter(artifactColumns, 'checked');
  }
}
