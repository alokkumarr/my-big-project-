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
import { Injectable } from '@angular/core';
import { AnalyzeService } from '../../services/analyze.service';
import { AnalysisType, Analysis } from '../../types';
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
  DEFAULT_AGGREGATE_TYPE,
  DEFAULT_DATE_INTERVAL,
  CHART_DEFAULT_DATE_FORMAT
} from '../../consts';

const MAX_POSSIBLE_FIELDS_OF_SAME_AREA = 5;

@Injectable()
export class DesignerService {
  constructor(private _analyzeService: AnalyzeService) {}

  createAnalysis(semanticId: string, type: AnalysisType): Promise<Analysis> {
    return this._analyzeService.createAnalysis(semanticId, type);
  }

  getDataForAnalysis(analysis) {
    return this._analyzeService.getDataBySettings(analysis);
  }

  getDataForAnalysisPreview(analysis, options) {
    return this._analyzeService.previewExecution(analysis, options);
  }

  getCategories(privilege) {
    return this._analyzeService.getCategories(privilege);
  }

  saveAnalysis(analysis) {
    return this._analyzeService.saveReport(analysis);
  }

  public getPivotGroupAdapters(
    artifactColumns
  ): IDEsignerSettingGroupAdapter[] {
    const pivotReverseTransform = (artifactColumn: ArtifactColumnPivot) => {
      artifactColumn.area = null;
      artifactColumn.areaIndex = null;
      artifactColumn.checked = false;
    };

    const onReorder = (artifactColumns: ArtifactColumns) => {
      forEach(artifactColumns, (column, index) => {
        column.areaIndex = index;
      });
    };

    const areLessThenMaxFields = (
      artifactColumns: ArtifactColumns
    ): boolean => {
      return artifactColumns.length < MAX_POSSIBLE_FIELDS_OF_SAME_AREA;
    };

    const canAcceptNumberType = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      _
    ) => ({ type }: ArtifactColumnPivot) =>
      areLessThenMaxFields(groupAdapter.artifactColumns) &&
      NUMBER_TYPES.includes(type);

    const canAcceptAnyType = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      _
    ) => () => areLessThenMaxFields(groupAdapter.artifactColumns);

    const applyDataFieldDefaults = artifactColumn => {
      artifactColumn.aggregate = DEFAULT_AGGREGATE_TYPE.value;
    };

    const applyNonDatafieldDefaults = artifactColumn => {
      artifactColumn.dateInterval = DEFAULT_DATE_INTERVAL.value;
    };

    const pivotGroupAdapters: Array<IDEsignerSettingGroupAdapter> = [
      {
        title: 'Data',
        type: 'pivot',
        marker: 'data',
        artifactColumns: [],
        canAcceptArtifactColumn: canAcceptNumberType,
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
        canAcceptArtifactColumn: canAcceptAnyType,
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
        canAcceptArtifactColumn: canAcceptAnyType,
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

  getChartGroupAdapters(
    artifactColumns,
    chartType
  ): IDEsignerSettingGroupAdapter[] {
    const isStockChart = chartType.substring(0, 2) === 'ts';
    const chartReverseTransform = (artifactColumn: ArtifactColumnChart) => {
      artifactColumn.area = null;
      artifactColumn.checked = false;
    };

    const onReorder = (artifactColumns: ArtifactColumns) => {
      forEach(artifactColumns, (column, index) => {
        column.areaIndex = index;
      });
    };

    const canAcceptNumberType = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      groupAdapters: Array<IDEsignerSettingGroupAdapter>
    ) => ({ type }: ArtifactColumnChart) => {
      const maxAllowed = groupAdapter.maxAllowed(groupAdapter, groupAdapters);
      if (groupAdapter.artifactColumns.length >= maxAllowed) return false;
      return NUMBER_TYPES.includes(type);
    };

    const canAcceptDateType = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      groupAdapters: Array<IDEsignerSettingGroupAdapter>
    ) => ({ type }: ArtifactColumnChart) => {
      const maxAllowed = groupAdapter.maxAllowed(groupAdapter, groupAdapters);
      if (groupAdapter.artifactColumns.length >= maxAllowed) return false;
      return DATE_TYPES.includes(type);
    };

    const canAcceptAnyType = (
      groupAdapter: IDEsignerSettingGroupAdapter,
      groupAdapters: Array<IDEsignerSettingGroupAdapter>
    ) => () => {
      const maxAllowed = groupAdapter.maxAllowed(groupAdapter, groupAdapters);
      if (groupAdapter.artifactColumns.length >= maxAllowed) return false;
      return true;
    };

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

    const chartGroupAdapters: Array<IDEsignerSettingGroupAdapter> = [
      {
        title: chartType === 'pie' ? 'Angle' : 'Metrics',
        type: 'chart',
        marker: 'y',
        maxAllowed: () =>
          ['pie', 'bubble', 'stack'].includes(chartType) ? 1 : Infinity,
        artifactColumns: [],
        canAcceptArtifactColumn: canAcceptNumberType,
        transform(artifactColumn: ArtifactColumnChart) {
          artifactColumn.area = 'y';
          artifactColumn.checked = true;
          applyDataFieldDefaults(artifactColumn);
        },
        reverseTransform: chartReverseTransform,
        onReorder
      },
      {
        title: chartType === 'pie' ? 'Color By' : 'Dimension',
        type: 'chart',
        marker: 'x',
        maxAllowed: () => 1,
        artifactColumns: [],
        canAcceptArtifactColumn: isStockChart
          ? canAcceptDateType
          : canAcceptAnyType,
        transform(artifactColumn: ArtifactColumnChart) {
          artifactColumn.area = 'x';
          artifactColumn.checked = true;
          applyNonDatafieldDefaults(artifactColumn);
        },
        reverseTransform: chartReverseTransform,
        onReorder
      },
      /* prettier-ignore */
      ...(chartType === 'bubble' ? [
        {
          title: 'Size',
          type: 'chart' as AnalysisType,
          marker: 'z',
          maxAllowed: () => 1,
          artifactColumns: [],
          canAcceptArtifactColumn: canAcceptNumberType,
          transform(artifactColumn: ArtifactColumnChart) {
            artifactColumn.area = 'z';
            artifactColumn.checked = true;
            applyDataFieldDefaults(artifactColumn);
          },
          reverseTransform: chartReverseTransform,
          onReorder
        }
      ] : []),
      {
        title: chartType === 'bubble' ? 'Color By' : 'Group By',
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
        canAcceptArtifactColumn: canAcceptAnyType,
        transform(artifactColumn: ArtifactColumnChart) {
          artifactColumn.area = 'g';
          artifactColumn.checked = true;
          applyNonDatafieldDefaults(artifactColumn);
        },
        reverseTransform: chartReverseTransform,
        onReorder
      }
    ];

    this._distributeArtifactColumnsIntoGroups(
      artifactColumns,
      chartGroupAdapters,
      'chart'
    );

    return chartGroupAdapters;
  }

  private _distributeArtifactColumnsIntoGroups(
    artifactColumns: ArtifactColumns,
    groupAdapters: IDEsignerSettingGroupAdapter[],
    analysisType: 'chart' | 'pivot'
  ) {
    const groupByProps = {
      chart: 'area',
      pivot: 'area'
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
            aggregate: isDataArea ? artifactColumn.aggregate : null,
            // the name propertie is needed for the elastic search
            name: isDataArea ? artifactColumn.columnName : null,
            dateInterval: isDateType ? artifactColumn.dateInterval : null
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
            comboType: artifactColumn.comboType,
            displayName: artifactColumn.displayName,
            table: artifactColumn.table,
            tableName: artifactColumn.table,
            name: artifactColumn.columnName,
            type: artifactColumn.type,
            // the name propertie is needed for the elastic search
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

  getPartialEsReportSqlBuilder(
    artifactColumns: ArtifactColumns
  ): Partial<SqlBuilderEsReport> {
    return {
      dataFields: filter(artifactColumns, 'checked')
    };
  }
}
