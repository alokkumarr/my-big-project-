import * as forEach from 'lodash/forEach';
import * as fpFilter from 'lodash/fp/filter';
import * as fpSortBy from 'lodash/fp/sortBy';
import * as fpGroupBy from 'lodash/fp/groupBy';
import * as fpMap from 'lodash/fp/map';
import * as fpGet from 'lodash/fp/get';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMapValues from 'lodash/fp/mapValues';
import * as isEmpty from 'lodash/isEmpty';
import * as cloneDeep from 'lodash/cloneDeep';
import * as compact from 'lodash/compact';
import * as isFunction from 'lodash/isFunction';
import * as unset from 'lodash/unset';
import * as toLower from 'lodash/toLower';
import * as toUpper from 'lodash/toUpper';
import * as some from 'lodash/some';
import * as split from 'lodash/split';
import * as set from 'lodash/set';
import * as flatMap from 'lodash/flatMap';

import { Injectable } from '@angular/core';
import { AnalyzeService } from '../services/analyze.service';
import { AnalysisType, Analysis, Artifact } from '../types';
import {
  AnalysisDSL,
  AnalysisPivotDSL,
  QueryDSL,
  ArtifactColumnDSL
} from '../../../models';

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
  AGGREGATE_TYPES_OBJ,
  DEFAULT_DATE_INTERVAL,
  DEFAULT_PIVOT_DATE_FORMAT,
  CHART_DEFAULT_DATE_FORMAT
} from '../consts';
import { AggregateChooserComponent } from 'src/app/common/components/aggregate-chooser';
import { DATA_AXIS } from './consts';
import { CHART_COLORS } from 'src/app/common/consts';

const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

const onReorder = (columns: ArtifactColumns) => {
  forEach(columns, (column, index) => {
    column.areaIndex = index;
  });
};

const canAcceptNumberType = ({ type, formula }: ArtifactColumnChart) =>
  NUMBER_TYPES.includes(type) || !!formula;
const canAcceptStringType = ({ type }: ArtifactColumnChart) =>
  type === 'string';
const canAcceptDateType = ({ type }: ArtifactColumnChart) =>
  DATE_TYPES.includes(type);
const canAcceptGeoType = ({ geoType }: ArtifactColumnChart) =>
  geoType !== 'lngLat' && GEO_TYPES.includes(geoType);
const canAcceptLngLat = ({ geoType }: ArtifactColumnChart) =>
  geoType === 'lngLat';
const canAcceptAnyType = ({ formula }: ArtifactColumnChart) => !formula;

@Injectable()
export class DesignerService {
  static analysisSubType(analysis: AnalysisDSL): string {
    const subTypePath =
      analysis.type === 'chart'
        ? 'chartOptions.chartType'
        : 'mapOptions.mapType';
    return fpGet(subTypePath, analysis);
  }

  static displayNameFor(column: ArtifactColumnDSL): string {
    const match = column.displayName.match(/\((.+)\)/);
    if (['z', 'y', 'data'].includes(column.area) && column.aggregate) {
      return match
        ? `${toUpper(column.aggregate)}(${match[1]})`
        : `${toUpper(column.aggregate)}(${column.displayName})`;
    }

    return match ? match[1] : column.displayName;
  }

  /**
   * Generates dataField value for column. Uses
   * aggregate and column name to generate unique value.
   * We don't support having more than one columns with
   * exact same dataField.
   *
   * @param {ArtifactColumnDSL} column
   * @returns {string}
   * @memberof DesignerService
   */
  static dataFieldFor(column: ArtifactColumnDSL): string {
    if (!column.columnName && !column.name) {
      throw new Error(
        `Cannot calculate data field for invalid column: ${JSON.stringify(
          column,
          null,
          2
        )}.`
      );
    }

    const columnName = column.columnName || column.name;
    const [trimmedColumnName] = split(columnName, '.');
    if (!!column.aggregate) {
      return `${toLower(column.aggregate)}@@${toLower(trimmedColumnName)}`;
    }

    return columnName;
  }

  /**
   * Checks if column has unique data field in list of columns.
   *
   * @param {ArtifactColumnDSL} column
   * @param {ArtifactColumnDSL[]} columns
   * @returns {boolean}
   * @memberof DesignerService
   */
  static isUniqueDataField(
    column: ArtifactColumnDSL,
    columns: ArtifactColumnDSL[]
  ): boolean {
    const dataField = DesignerService.dataFieldFor(column);
    return !some(
      columns,
      (col: ArtifactColumnDSL) => this.dataFieldFor(col) === dataField
    );
  }

  static unusedAggregates(
    column: ArtifactColumnDSL,
    columns: ArtifactColumnDSL[],
    analysisType: string,
    analysisSubtype: string
  ): string[] {
    // const alreadyUsedAggregates = compact(
    //   (columns || [])
    //     .filter(col => col.columnName === column.columnName)
    //     .map(col => toLower(col.aggregate))
    // );

    // return difference(AGGREGATE_VALUES, alreadyUsedAggregates);

    let aggregates = [];

    if (NUMBER_TYPES.includes(column.type)) {
      aggregates = AggregateChooserComponent.isAggregateEligible(analysisType);
    } else if (column.type === 'string') {
      aggregates = [
        AGGREGATE_TYPES_OBJ['count'],
        AGGREGATE_TYPES_OBJ['distinctcount']
      ];
    }

    return fpPipe(
      fpFilter(agg =>
        AggregateChooserComponent.isAggregateValid(
          agg.value,
          column,
          columns,
          analysisType,
          analysisSubtype
        )
      ),
      fpMap(agg => agg.value)
    )(aggregates);
  }

  static canAddColumn(
    column: ArtifactColumnDSL,
    columns: ArtifactColumnDSL[],
    analysisType: string,
    analysisSubtype: string
  ) {
    const notNumberType = !NUMBER_TYPES.includes(column.type);
    const notStringType = column.type !== 'string';
    const hasExpression = Boolean(column.expression);
    if ((notNumberType && notStringType) || hasExpression) {
      return !some(columns, col => col.columnName === column.columnName);
    }
    return (
      DesignerService.unusedAggregates(
        column,
        columns,
        analysisType,
        analysisSubtype
      ).length > 0
    );
  }

  /**
   *  Adjusting the default series color to newly selected column.
   * Added a part of SIP-10225.
   */
  static setSeriesColorForColumns(sortedArtifacts) {
    const selectedColumns = flatMap(sortedArtifacts, x => x.fields);
    const dataColumn = fpFilter(
      col => DATA_AXIS.includes(col.area) && !col.colorSetFromPicker
    )(selectedColumns);

    forEach(dataColumn, (col, index) => {
      set(col, 'seriesColor', CHART_COLORS[index]);
      set(col, 'colorSetFromPicker', false);
    });

    return sortedArtifacts;
  }

  constructor(private _analyzeService: AnalyzeService) {}

  createAnalysis(
    semanticId: string,
    type: AnalysisType
  ): Promise<Partial<Analysis | AnalysisDSL | AnalysisPivotDSL>> {
    return this._analyzeService.newAnalysisModel(semanticId, type);
  }

  generateReportPayload(analysis) {
    forEach(analysis.artifacts, cols => {
      forEach(cols.columns, col => {
        delete col.checked;
      });
    });

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
    return this._analyzeService.saveAnalysis(analysisRequest);
  }

  public getPivotGroupAdapters(
    artifactColumns
  ): IDEsignerSettingGroupAdapter[] {
    const pivotReverseTransform = (artifactColumn: ArtifactColumnPivot) => {
      artifactColumn.area = null;
      artifactColumn.areaIndex = null;
      artifactColumn.checked = false;
      unset(artifactColumn, 'dataField');
      unset(artifactColumn, 'aggregate');
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

    const applyDataFieldDefaults = (
      artifactColumn,
      columns,
      { analysisType, analysisSubType }
    ) => {
      if (!artifactColumn.formula) {
        const unusedAggregates =
          DesignerService.unusedAggregates(
            artifactColumn,
            columns,
            analysisType,
            analysisSubType
          ) || [];
        artifactColumn.aggregate =
          unusedAggregates[0] || DEFAULT_AGGREGATE_TYPE.value;
        artifactColumn.dataField = DesignerService.dataFieldFor(<
          ArtifactColumnDSL
        >artifactColumn);
      }
    };

    const applyNonDatafieldDefaults = artifactColumn => {
      artifactColumn.groupInterval = DEFAULT_DATE_INTERVAL.value;
    };

    const canAcceptDataType = (column: ArtifactColumnChart) =>
      canAcceptNumberType(column) || canAcceptStringType(column);
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
        transform(
          artifactColumn: ArtifactColumnPivot,
          columns: ArtifactColumn[] = [],
          options = {}
        ) {
          artifactColumn.area = 'data';
          artifactColumn.checked = true;
          applyDataFieldDefaults(artifactColumn, columns, options);
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
      unset(artifactColumn, 'dataField');
      unset(artifactColumn, 'geoRegion');
      unset(artifactColumn, 'aggregate');
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

    const applyDataFieldDefaults = (
      artifactColumn,
      columns,
      { analysisType, analysisSubType }
    ) => {
      if (!artifactColumn.formula) {
        const unusedAggregates =
          DesignerService.unusedAggregates(
            artifactColumn,
            columns,
            analysisType,
            analysisSubType
          ) || [];
        artifactColumn.aggregate =
          unusedAggregates[0] || DEFAULT_AGGREGATE_TYPE.value;
        artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
        artifactColumn.dataField = DesignerService.dataFieldFor(<
          ArtifactColumnDSL
        >artifactColumn);
      }
    };

    const canAcceptMetricType = canAcceptNumberType;
    const canAcceptInMetric = maxAllowedDecorator(canAcceptMetricType);
    const metricTitle = subType === 'map' ? 'Data' : 'Metric';

    const metricAdapter: IDEsignerSettingGroupAdapter = {
      title: metricTitle,
      type: 'map',
      marker: 'y',
      maxAllowed: () => (subType === 'map' ? Infinity : 1),
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptMetricType,
      canAcceptArtifactColumn: canAcceptInMetric,
      transform(
        artifactColumn: ArtifactColumnChart,
        columns: ArtifactColumn[],
        options = {}
      ) {
        artifactColumn.area = 'y';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn, columns, options);
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
      artifactColumn['checked'] = false;
      artifactColumn.alias = '';
      unset(artifactColumn, 'dataField');
      unset(artifactColumn, 'aggregate');
      unset(artifactColumn, 'comboType');
      unset(artifactColumn, 'limitType');
      unset(artifactColumn, 'limitValue');
    };

    let metricTitle;
    switch (chartType) {
      case 'packedbubble':
        metricTitle = 'Size';
        break;
      case 'pie':
        metricTitle = 'Angle';
        break;
      default:
        metricTitle = 'Metrics';
        break;
    }
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
      const isMoreThanOneGroupField = groupByGroupAdapter
        ? groupByGroupAdapter.artifactColumns.length > 0
        : true;
      return (
        isMoreThanOneGroupField && groupAdapter.artifactColumns.length === 1
      );
    };

    const canAcceptMetricType = (column: ArtifactColumnChart) =>
      canAcceptNumberType(column) || canAcceptStringType(column);
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
      : chartType === 'packedbubble'
      ? canAcceptStringType
      : canAcceptAnyType;
    const canAcceptInDimension = maxAllowedDecorator(canAcceptDimensionType);

    const applyDataFieldDefaults = (
      artifactColumn,
      columns,
      { analysisType, analysisSubType }
    ) => {
      if (!artifactColumn.formula) {
        const unusedAggregates =
          DesignerService.unusedAggregates(
            artifactColumn,
            columns,
            analysisType,
            analysisSubType
          ) || [];
        artifactColumn.aggregate =
          unusedAggregates[0] || DEFAULT_AGGREGATE_TYPE.value;

        artifactColumn.dataField = DesignerService.dataFieldFor(<
          ArtifactColumnDSL
        >artifactColumn);
      }
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
        artifactColumn.format = CHART_DEFAULT_DATE_FORMAT.value;
      }
    };

    const metricAdapter: IDEsignerSettingGroupAdapter = {
      title: metricTitle,
      type: 'chart',
      marker: 'y',
      maxAllowed: () =>
        ['pie', 'bubble', 'stack', 'packedbubble'].includes(chartType)
          ? 1
          : Infinity,
      artifactColumns: [],
      canAcceptArtifactColumnOfType: canAcceptMetricType,
      canAcceptArtifactColumn: canAcceptInMetric,
      transform(
        artifactColumn: ArtifactColumnChart,
        columns: ArtifactColumn[],
        options = {}
      ) {
        artifactColumn.area = 'y';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn, columns, options);
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
      transform(
        artifactColumn: ArtifactColumnChart,
        columns: ArtifactColumn[],
        options = {}
      ) {
        artifactColumn.area = 'z';
        artifactColumn.checked = true;
        applyDataFieldDefaults(artifactColumn, columns, options);
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

  getGroupsThatCanRecieve(
    artifactColumn: ArtifactColumn,
    groupAdapters: IDEsignerSettingGroupAdapter[]
  ): IDEsignerSettingGroupAdapter[] {
    return filter(groupAdapters, (adapter: IDEsignerSettingGroupAdapter) =>
      adapter.canAcceptArtifactColumn(adapter, groupAdapters)(artifactColumn)
    );
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
                artifactColumn.format || DEFAULT_PIVOT_DATE_FORMAT.value
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
            alias: artifactColumn.alias,
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
            geoRegion: artifactColumn.geoRegion,
            // the name propert is needed for the elastic search
            /* prettier-ignore */
            ...(isDateType ? {
              dateFormat:
                artifactColumn.format || CHART_DEFAULT_DATE_FORMAT.value
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

  /**
   * Adds derived metrics for analysis to the artifacts for access in settings
   *
   * @param {Artifact[]} artifacts
   * @param {QueryDSL} sipQuery
   * @returns
   * @memberof DesignerService
   */
  addDerivedMetricsToArtifacts(artifacts: Artifact[], sipQuery: QueryDSL) {
    if (
      isEmpty(fpGet('artifacts.0.fields', sipQuery)) ||
      !artifacts ||
      !fpGet('0.columns', artifacts)
    ) {
      return artifacts;
    }

    const existingDerivedMetrics: string[] = fpMap(
      col => col.columnName,
      fpFilter(col => !!col.expression, artifacts[0].columns)
    );

    const derivedMetrics: any[] = filter(
      sipQuery.artifacts[0].fields,
      field =>
        !!field.expression && !existingDerivedMetrics.includes(field.columnName)
    );

    return [
      {
        ...artifacts[0],
        columns: [...artifacts[0].columns, ...derivedMetrics]
      },
      ...artifacts.slice(1)
    ];
  }
}
