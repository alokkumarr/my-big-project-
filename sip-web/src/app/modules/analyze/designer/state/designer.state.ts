import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import * as get from 'lodash/get';
import * as unset from 'lodash/unset';
import * as findIndex from 'lodash/findIndex';
import * as forEach from 'lodash/forEach';
import * as set from 'lodash/set';
import * as remove from 'lodash/remove';
import * as toLower from 'lodash/toLower';
import * as isEmpty from 'lodash/isEmpty';
import * as map from 'lodash/map';
import * as find from 'lodash/find';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpReduce from 'lodash/fp/reduce';
import * as fpFilter from 'lodash/fp/filter';
// import { setAutoFreeze } from 'immer';
// import produce from 'immer';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import {
  DesignerStateModel,
  DSLChartOptionsModel,
  AnalysisChartDSL,
  AnalysisMapDSL
} from '../types';
import {
  DesignerInitGroupAdapters,
  DesignerAddColumnToGroupAdapter,
  DesignerMoveColumnInGroupAdapter,
  DesignerRemoveColumnFromGroupAdapter,
  DesignerClearGroupAdapters,
  DesignerInitEditAnalysis,
  DesignerInitForkAnalysis,
  DesignerInitNewAnalysis,
  DesignerUpdateAnalysisMetadata,
  DesignerUpdateAnalysisSubType,
  DesignerUpdateSorts,
  DesignerUpdateFilters,
  DesignerUpdatebooleanCriteria,
  DesignerUpdateAnalysisChartTitle,
  DesignerUpdateAnalysisChartInversion,
  DesignerUpdateAnalysisChartLegend,
  DesignerUpdateAnalysisChartLabelOptions,
  DesignerUpdateAnalysisChartXAxis,
  DesignerUpdateAnalysisChartYAxis,
  DesignerAddArtifactColumn,
  DesignerRemoveArtifactColumn,
  DesignerUpdateArtifactColumn,
  DesignerMergeMetricColumns,
  DesignerMergeSupportsIntoAnalysis,
  DesignerApplyChangesToArtifactColumns,
  DesignerRemoveAllArtifactColumns,
  DesignerLoadMetric,
  DesignerResetState,
  DesignerSetData,
  DesignerUpdateEditMode,
  DesignerUpdateQuery,
  DesignerJoinsArray,
  ConstructDesignerJoins,
  DesignerUpdateAggregateInSorts
} from '../actions/designer.actions';
import { DesignerService } from '../designer.service';
import { AnalyzeService } from '../../services/analyze.service';
import {
  DATE_TYPES,
  DEFAULT_DATE_FORMAT,
  CUSTOM_DATE_PRESET_VALUE,
  CHART_DATE_FORMATS_OBJ
} from '../../consts';
import { AnalysisDSL, ArtifactColumnDSL } from 'src/app/models';
import { CommonDesignerJoinsArray } from 'src/app/common/actions/common.actions';

// setAutoFreeze(false);

export const defaultDesignerState: DesignerStateModel = {
  groupAdapters: [],
  analysis: null,
  metric: null,
  data: null
};

const defaultDSLChartOptions: DSLChartOptionsModel = {
  chartTitle: null,
  chartType: null,
  isInverted: false,
  legend: {
    align: '',
    layout: ''
  },
  labelOptions: {
    enabled: false,
    value: ''
  },
  xAxis: {
    title: null
  },
  yAxis: {
    title: null
  }
};

const MAX_PACKED_BUBBLE_CHART_DATA = 20;

@State<DesignerStateModel>({
  name: 'designerState',
  defaults: <DesignerStateModel>cloneDeep(defaultDesignerState)
})
export class DesignerState {
  constructor(
    private _designerService: DesignerService,
    private _analyzeService: AnalyzeService
  ) {}

  @Selector()
  static groupAdapters(state: DesignerStateModel) {
    return state.groupAdapters;
  }

  @Selector()
  static canRequestData(state: DesignerStateModel): boolean {
    const analysis: AnalysisDSL = state.analysis;
    const sipQuery = analysis && analysis.sipQuery;
    if (!sipQuery) {
      return false;
    }

    /* If this is a report, and query is present, we can request data */
    if (analysis.type === 'report' && !!sipQuery.query) {
      return true;
    } else {
      /* Else, there should be at least one field selected in artifacts */
      return (
        (
          fpFlatMap(
            artifact => artifact.fields,
            get(state, 'analysis.sipQuery.artifacts')
          ) || []
        ).length > 0
      );
    }

    return false;
  }

  @Selector()
  static allSelectedFields(state: DesignerStateModel) {
    return fpFlatMap(
      artifact => artifact.fields,
      get(state, 'analysis.sipQuery.artifacts')
    );
  }

  @Selector()
  static selectedNonDataFields(state: DesignerStateModel) {
    return fpPipe(
      fpFlatMap(artifact => artifact.fields),
      fpFilter(field => !['data', 'y', 'z'].includes(field.area))
    )(get(state, 'analysis.sipQuery.artifacts'));
  }

  @Selector()
  static selectedDataFields(state: DesignerStateModel) {
    return fpPipe(
      fpFlatMap(artifact => artifact.fields),
      fpFilter(field => ['data', 'y', 'z'].includes(field.area))
    )(get(state, 'analysis.sipQuery.artifacts'));
  }

  @Selector()
  static analysis(state: DesignerStateModel) {
    return state.analysis;
  }

  @Selector()
  static data(state: DesignerStateModel) {
    return state.data;
  }

  @Selector()
  static metricName(state: DesignerStateModel) {
    return state.metric.metricName;
  }

  @Selector()
  static isDataTooMuchForChart(state: DesignerStateModel) {
    const chartType = get(state, 'analysis.chartOptions.chartType');
    return (
      chartType === 'packedbubble' &&
      state.data.length > MAX_PACKED_BUBBLE_CHART_DATA
    );
  }

  @Action(DesignerSetData)
  setData(
    { patchState }: StateContext<DesignerStateModel>,
    { data }: DesignerSetData
  ) {
    return patchState({
      data
    });
  }

  @Action(DesignerMergeSupportsIntoAnalysis)
  mergeSupportsIntoAnalysis(
    { getState, patchState }: StateContext<DesignerStateModel>,
    { supports }: DesignerMergeSupportsIntoAnalysis
  ) {
    const analysis = getState().analysis;

    set(analysis, 'supports', supports);

    return patchState({
      analysis: { ...analysis }
    });
  }

  @Action(DesignerMergeMetricColumns)
  mergeMetricArtifactColumnWithAnalysisArtifactColumns(
    { getState, patchState }: StateContext<DesignerStateModel>,
    { metricArtifactColumns }: DesignerMergeMetricColumns
  ) {
    const analysis = getState().analysis;
    const sipQuery = this._analyzeService.copyGeoTypeFromMetric(
      metricArtifactColumns,
      analysis.sipQuery
    );

    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery } }
    });
  }

  @Action(DesignerLoadMetric)
  async loadMetrics(
    { patchState }: StateContext<DesignerStateModel>,
    { metric }: DesignerLoadMetric
  ) {
    patchState({
      metric: {
        metricName: metric.metricName,
        artifacts: metric.artifacts
      }
    });
  }

  @Action(DesignerRemoveArtifactColumn)
  removeArtifactColumn(
    { getState, patchState, dispatch }: StateContext<DesignerStateModel>,
    { artifactColumn, fieldArea }: DesignerRemoveArtifactColumn
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;

    /* Find the artifact inside sipQuery of analysis stored in state */
    const artifactsName =
      artifactColumn.table || (<any>artifactColumn).tableName;
    const artifactIndex = artifacts.findIndex(
      artifact => toLower(artifact.artifactsName) === toLower(artifactsName)
    );

    if (artifactIndex < 0) {
      return patchState({});
    }

    const artifactColumnIndex = findIndex(
      artifacts[artifactIndex].fields,
      ({ columnName, dataField, area }) =>
        dataField
          ? dataField === artifactColumn.dataField
          : columnName === artifactColumn.columnName
    );

    artifacts[artifactIndex].fields.splice(artifactColumnIndex, 1);
    const sortedArtifacts = filter(
      artifacts,
      artifact => !isEmpty(artifact.fields)
    );

    // if sort is applied for the field that is removed, remove sort from SIPQUERY
    const sorts = filter(sipQuery.sorts, sort =>
      sort.columnName !== artifactColumn.columnName
    );
    patchState({
      analysis: {
        ...analysis,
        sipQuery: { ...sipQuery, artifacts: sortedArtifacts, sorts }
      }
    });
    return dispatch(new DesignerApplyChangesToArtifactColumns());
  }

  @Action(DesignerUpdateArtifactColumn)
  updateArtifactColumn(
    { getState, patchState }: StateContext<DesignerStateModel>,
    { artifactColumn }: DesignerUpdateArtifactColumn
  ) {
    const { analysis, groupAdapters } = getState();
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;
    const fillMissingDataWithZeros =
      analysis.type === 'chart' && artifactColumn.type === 'date';

    const identifier = artifactColumn.dataField
      ? artifactColumn.dataField
      : artifactColumn.columnName;

    /* Find the artifact inside sipQuery of analysis stored in state */
    const artifactsName =
      artifactColumn.table || (<any>artifactColumn).tableName;
    const artifactIndex = artifacts.findIndex(
      artifact => artifact.artifactsName === artifactsName
    );

    if (artifactIndex < 0) {
      return patchState({});
    }

    const artifactColumnIndex = artifacts[artifactIndex].fields.findIndex(
      field => {
        const fieldName = artifactColumn.dataField ? 'dataField' : 'columnName';
        return field[fieldName] === identifier;
      }
    );

    /* If artifact column had a data field, make sure it's updated with latest aggregate.
       If no data field exists, it is a non-data field. Do nothing */
    if (artifactColumn.dataField) {
      artifactColumn.dataField = DesignerService.dataFieldFor({
        columnName: artifactColumn.columnName,
        aggregate:
          artifactColumn.aggregate ||
          artifacts[artifactIndex].fields[artifactColumnIndex].aggregate
      } as ArtifactColumnDSL);
    }

    artifactColumn.displayName = DesignerService.displayNameFor({
      displayName:
        artifactColumn.displayName ||
        artifacts[artifactIndex].fields[artifactColumnIndex].displayName,
      aggregate:
        artifactColumn.aggregate ||
        artifacts[artifactIndex].fields[artifactColumnIndex].aggregate,
      area: artifactColumn.hasOwnProperty('area')
        ? artifactColumn.area
        : artifacts[artifactIndex].fields[artifactColumnIndex].area
    } as any);

    artifacts[artifactIndex].fields[artifactColumnIndex] = {
      ...artifacts[artifactIndex].fields[artifactColumnIndex],
      ...artifactColumn,
      ...(fillMissingDataWithZeros ? { min_doc_count: 0 } : {})
    };

    const targetAdapterIndex = findIndex(
      groupAdapters,
      adapter =>
        adapter.marker ===
        artifacts[artifactIndex].fields[artifactColumnIndex].area
    );

    // In case of reports, there's no concept of group adapters. Check for that here.
    if (targetAdapterIndex >= 0) {
      const targetAdapter = groupAdapters[targetAdapterIndex];
      const adapterColumnIndex = findIndex(
        targetAdapter.artifactColumns,
        col => {
          const fieldName = artifactColumn.dataField
            ? 'dataField'
            : 'columnName';
          return col[fieldName] === identifier;
        }
      );
      const adapterColumn = targetAdapter.artifactColumns[adapterColumnIndex];

      forEach(artifactColumn, (value, prop) => {
        adapterColumn[prop] = value;
      });
    }
    return patchState({
      analysis: {
        ...analysis,
        sipQuery: { ...sipQuery, artifacts }
      },
      groupAdapters: [...groupAdapters]
    });
  }

  @Action(DesignerApplyChangesToArtifactColumns)
  reorderArtifactColumns({
    getState,
    patchState
  }: StateContext<DesignerStateModel>) {
    const { analysis, groupAdapters } = getState();
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;

    // reorder artifactColumns
    const areaIndexMap = fpPipe(
      fpFlatMap(adapter => adapter.artifactColumns),
      fpReduce((accumulator, artifactColumn) => {
        accumulator[artifactColumn.columnName] = artifactColumn.areaIndex;
        return accumulator;
      }, {})
    )(groupAdapters);

    forEach(artifacts, artifact => {
      forEach(artifact.fields, field => {
        field.areaIndex = areaIndexMap[field.columnName];
      });
    });

    // unset fetch limit if there are more thatn 1 y fields

    const dataFields = fpPipe(
      fpFlatMap(artifact => artifact.fields),
      fpFilter(field => field.area === 'y')
    )(artifacts);

    if (dataFields.length === 2) {
      forEach(dataFields, field => {
        unset(field, 'limitType');
        unset(field, 'limitValue');
      });
    }
  }

  @Action(DesignerRemoveAllArtifactColumns)
  removeAllArtifactColumns({
    patchState,
    getState
  }: StateContext<DesignerStateModel>) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    const artifacts = (sipQuery.artifacts || []).map(artifact => ({
      ...artifact,
      fields: []
    }));

    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, artifacts } }
    });
  }

  @Action(DesignerUpdateAnalysisMetadata)
  updateCategoryId(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { metadata }: DesignerUpdateAnalysisMetadata
  ) {
    const analysis = getState().analysis;
    return patchState({
      analysis: { ...analysis, ...metadata } as AnalysisDSL
    });
  }

  @Action(DesignerUpdateEditMode)
  updateDesignerEdit(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { designerEdit }: DesignerUpdateEditMode
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    if (designerEdit) {
      sipQuery.filters = [];
      sipQuery.sorts = [];
    }
    return patchState({
      analysis: { ...analysis, designerEdit, sipQuery: { ...sipQuery } }
    });
  }

  @Action(DesignerUpdateQuery)
  updateQuery(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { query }: DesignerUpdateQuery
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;

    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, query } }
    });
  }

  @Action(DesignerUpdateAnalysisSubType)
  updateChartType(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { subType }: DesignerUpdateAnalysisSubType
  ) {
    const analysis = getState().analysis;
    switch (analysis.type) {
      case 'chart':
        const chartOptions =
          (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
        return patchState({
          analysis: {
            ...analysis,
            chartType: subType,
            chartOptions: { ...chartOptions, chartType: subType }
          }
        });
      case 'map':
        const mapOptions = (<AnalysisMapDSL>analysis).mapOptions;
        return patchState({
          analysis: {
            ...analysis,
            mapOptions: { ...mapOptions, mapType: subType }
          }
        });
    }
  }

  @Action(DesignerUpdateAnalysisChartInversion)
  updateChartInversion(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { isInverted }: DesignerUpdateAnalysisChartInversion
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, isInverted }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartTitle)
  updateChartTitle(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { chartTitle }: DesignerUpdateAnalysisChartTitle
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, chartTitle }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartLegend)
  updateChartLegend(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { legend }: DesignerUpdateAnalysisChartLegend
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, legend }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartLabelOptions)
  updateChartLabelOptions(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { labelOptions }: DesignerUpdateAnalysisChartLabelOptions
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, labelOptions }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartXAxis)
  updateChartXAxis(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { xAxis }: DesignerUpdateAnalysisChartXAxis
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, xAxis }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartYAxis)
  updateChartYAxis(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { yAxis }: DesignerUpdateAnalysisChartYAxis
  ) {
    const analysis = getState().analysis;
    const chartOptions =
      (<AnalysisChartDSL>analysis).chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, yAxis }
      }
    });
  }

  @Action(DesignerUpdateSorts)
  updateSorts(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { sorts }: DesignerUpdateSorts
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, sorts } }
    });
  }

  @Action(DesignerInitEditAnalysis)
  @Action(DesignerInitForkAnalysis)
  @Action(DesignerInitNewAnalysis)
  initAnalysis(
    { patchState }: StateContext<DesignerStateModel>,
    {
      analysis
    }:
      | DesignerInitNewAnalysis
      | DesignerInitEditAnalysis
      | DesignerInitForkAnalysis
  ) {
    return patchState({ analysis });
  }

  @Action(DesignerInitGroupAdapters)
  initGroupAdapter({ patchState, getState }: StateContext<DesignerStateModel>) {
    const analysis = getState().analysis;
    const { type } = analysis;
    const fields = get(analysis, 'sipQuery.artifacts.0.fields', []);
    let groupAdapters;
    switch (type) {
      case 'pivot':
        groupAdapters = this._designerService.getPivotGroupAdapters(fields);
        break;
      case 'chart':
        const { chartOptions } = <AnalysisChartDSL>analysis;
        groupAdapters = this._designerService.getChartGroupAdapters(
          fields,
          chartOptions.chartType
        );
        break;
      case 'map':
        const { mapOptions } = <AnalysisMapDSL>analysis;
        groupAdapters = this._designerService.getMapGroupAdapters(
          fields,
          mapOptions.mapType
        );
        break;
      default:
        groupAdapters = [];
        break;
    }
    return patchState({ groupAdapters });
  }

  @Action(DesignerAddColumnToGroupAdapter)
  addColumnToGroupAdapter(
    { patchState, getState, dispatch }: StateContext<DesignerStateModel>,
    {
      artifactColumn,
      columnIndex,
      adapterIndex
    }: DesignerAddColumnToGroupAdapter
  ) {

    const groupAdapters = getState().groupAdapters;
    const analysis = getState().analysis;
    const adapter = groupAdapters[adapterIndex];
    const allAdapterFields = fpFlatMap(ad => ad.artifactColumns, groupAdapters);

    // disabled immer because having immutability for groupAdapters causes conflicts in the designer
    // so it will stay disabled until a refactoring of the whole designer to ngxs
    // const groupAdapters = produce(getState().groupAdapters, draft => {
    //   draft[adapterIndex].artifactColumns.splice(
    //     columnIndex,
    //     0,
    //     artifactColumn
    //   );
    // });

    adapter.transform(artifactColumn, allAdapterFields, {
      analysisType: analysis.type,
      analysisSubtype: DesignerService.analysisSubType(analysis)
    });

    adapter.artifactColumns.splice(columnIndex, 0, artifactColumn);
    adapter.onReorder(adapter.artifactColumns);
    if (get(artifactColumn, 'area') !== 'data') {
      delete artifactColumn.aggregate;
    }
    patchState({ groupAdapters: [...groupAdapters] });
    return dispatch(new DesignerAddArtifactColumn(artifactColumn));
  }

  @Action(DesignerAddArtifactColumn)
  addArtifactColumn(
    { getState, patchState, dispatch }: StateContext<DesignerStateModel>,
    { artifactColumn }: DesignerAddArtifactColumn
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    let artifacts = sipQuery.artifacts;
    const isDateType = DATE_TYPES.includes(artifactColumn.type);
    const fillMissingDataWithZeros =
      analysis.type === 'chart' && artifactColumn.type === 'date';

    /* If analysis is chart and this is a date field, assign a default
      groupInterval. For pivots, use dateInterval if available */
    const groupInterval = { groupInterval: null };

    if (artifactColumn.type === 'date') {
      switch (analysis.type) {
        case 'chart':
          groupInterval.groupInterval =
            CHART_DATE_FORMATS_OBJ[
              artifactColumn.dateFormat || <string>artifactColumn.format
            ].groupInterval;
          break;
        case 'pivot':
          groupInterval.groupInterval = 'day';
          break;
        default:
          break;
      }
    }

    const artifactsName =
      artifactColumn.table || (<any>artifactColumn).tableName;

    /* Find the artifact inside sipQuery of analysis stored in state */
    const artifactIndex = artifacts.findIndex(
      artifact => artifact.artifactsName === artifactsName
    );
    const artifactColumnToBeAdded = {
      aggregate: artifactColumn.aggregate,
      alias: artifactColumn.alias,
      area: artifactColumn.area,
      columnName: artifactColumn.columnName,
      displayType:
        artifactColumn.displayType || (<any>artifactColumn).comboType,
      ...(artifactColumn.dataField
        ? { dataField: artifactColumn.dataField }
        : {}),
      displayName: DesignerService.displayNameFor(
        artifactColumn as ArtifactColumnDSL
      ),
      ...(artifactColumn.formula
        ? {
            formula: artifactColumn.formula,
            expression: artifactColumn.expression
          }
        : {}),
      ...groupInterval,
      ...(fillMissingDataWithZeros ? { min_doc_count: 0 } : {}),
      name: artifactColumn.name,
      type: artifactColumn.type,
      geoType: artifactColumn.geoType,
      table: artifactColumn.table || (<any>artifactColumn).tableName,
      ...(isDateType
        ? {
            dateFormat:
              <string>artifactColumn.format || DEFAULT_DATE_FORMAT.value
          }
        : { format: artifactColumn.format })
    };
    if (artifactIndex < 0) {
      artifacts = [
        ...artifacts,
        { artifactsName, fields: [artifactColumnToBeAdded] }
      ];
    } else {
      artifacts[artifactIndex].fields = [
        ...artifacts[artifactIndex].fields,
        artifactColumnToBeAdded
      ];
    }
    // cleanup empty artifacts
    remove(sipQuery.artifacts, artifact => {
      return isEmpty(artifact.fields);
    });
    patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, artifacts } }
    });
    return dispatch(new DesignerApplyChangesToArtifactColumns());
  }

  @Action(DesignerClearGroupAdapters)
  clearGroupAdapters(
    { patchState, getState, dispatch }: StateContext<DesignerStateModel>,
    {  }: DesignerClearGroupAdapters
  ) {
    const groupAdapters = getState().groupAdapters;

    forEach(groupAdapters, adapter => {
      forEach(adapter.artifactColumns, column => {
        adapter.reverseTransform(column);
      });

      adapter.artifactColumns = [];
    });
    patchState({ groupAdapters: [...groupAdapters] });
    return dispatch(new DesignerRemoveAllArtifactColumns());
  }

  @Action(DesignerRemoveColumnFromGroupAdapter)
  removeColumnFromGroupAdapter(
    { patchState, getState, dispatch }: StateContext<DesignerStateModel>,
    { columnIndex, adapterIndex }: DesignerRemoveColumnFromGroupAdapter
  ) {
    const groupAdapters = getState().groupAdapters;
    const adapter = groupAdapters[adapterIndex];
    const column = adapter.artifactColumns[columnIndex];
    adapter.reverseTransform(column);
    groupAdapters[adapterIndex].artifactColumns.splice(columnIndex, 1);
    // const updatedGroupAdapters = produce(groupAdapters, draft => {
    //   draft[adapterIndex].artifactColumns.splice(columnIndex, 1);
    // });
    const updatedAdapter = groupAdapters[adapterIndex];
    adapter.onReorder(updatedAdapter.artifactColumns);
    patchState({ groupAdapters: [...groupAdapters] });
    return dispatch(new DesignerRemoveArtifactColumn(column, adapter.marker));
  }

  @Action(DesignerMoveColumnInGroupAdapter)
  moveColumnInGroupAdapter(
    { patchState, getState, dispatch }: StateContext<DesignerStateModel>,
    {
      previousColumnIndex,
      currentColumnIndex,
      adapterIndex
    }: DesignerMoveColumnInGroupAdapter
  ) {
    const groupAdapters = getState().groupAdapters;
    const adapter = groupAdapters[adapterIndex];
    const columns = adapter.artifactColumns;
    moveItemInArray(columns, previousColumnIndex, currentColumnIndex);
    // const groupAdapters = produce(getState().groupAdapters, draft => {
    //   const adapter = draft[adapterIndex];
    //   const columns = adapter.artifactColumns;
    //   moveItemInArray(columns, previousColumnIndex, currentColumnIndex);
    // });
    adapter.onReorder(adapter.artifactColumns);
    patchState({ groupAdapters: [...groupAdapters] });
    return dispatch(new DesignerApplyChangesToArtifactColumns());
  }

  @Action(DesignerUpdateFilters)
  updateFilters(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { filters }: DesignerUpdateFilters
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    filters.forEach(filter => {
      filter.artifactsName = filter.tableName;
      if (
        filter.type === 'date' &&
        !filter.isRuntimeFilter &&
        !filter.isGlobalFilter &&
        filter.model.preset === CUSTOM_DATE_PRESET_VALUE
      ) {
        filter.model = {
          gte: filter.model.gte,
          lte: filter.model.lte,
          format: 'yyyy-MM-dd HH:mm:ss',
          preset: CUSTOM_DATE_PRESET_VALUE
        };
      }
    });
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, filters } }
    });
  }

  @Action(DesignerUpdatebooleanCriteria)
  updatebooleanCriteria(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { booleanCriteria }: DesignerUpdatebooleanCriteria
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, booleanCriteria } }
    });
  }

  @Action(DesignerResetState)
  resetState({ patchState }: StateContext<DesignerStateModel>) {
    patchState(cloneDeep(defaultDesignerState));
  }

  @Action(CommonDesignerJoinsArray)
  @Action(DesignerJoinsArray)
  updateJoins(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { joins }: DesignerJoinsArray
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    const sipJoins = map(joins, join => {
      const leftCriteria = find(join.criteria, crt => crt.side === 'left');
      const rightCriteria = find(join.criteria, crt => crt.side === 'right');
      const leftJoin = {
        artifactsName: leftCriteria.tableName,
        columnName: leftCriteria.columnName
      };
      const rightJoin = {
        artifactsName: rightCriteria.tableName,
        columnName: rightCriteria.columnName
      };
      const joinCondition = {
        operator: 'EQ',
        left: leftJoin,
        right: rightJoin
      };
      return {
        join: join.type,
        criteria: [{ joinCondition }]
      };
    });
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, joins: sipJoins } }
    });
  }

  @Action(ConstructDesignerJoins)
  updateDesignerJoins(
    { patchState }: StateContext<ConstructDesignerJoins>,
    { analysis }
  ) {
    const sipQuery = analysis.sipQuery;
    const analysisJoins = [];
    if (isEmpty(analysis.sipQuery.joins)) {
      return;
    }
    analysis.sipQuery.joins.forEach(join => {
      const DSLCriteria = [];
      join.criteria.forEach(dslCRT => {
        DSLCriteria.push({
          tableName: dslCRT.joinCondition['left'].artifactsName,
          columnName: dslCRT.joinCondition['left'].columnName,
          side: 'left'
        });
        DSLCriteria.push({
          tableName: dslCRT.joinCondition['right'].artifactsName,
          columnName: dslCRT.joinCondition['right'].columnName,
          side: 'right'
        });
      });
      const dslJoin = {
        type: join.join,
        criteria: DSLCriteria
      };
      analysisJoins.push(dslJoin);
    });
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, joins: analysisJoins } }
    });
  }

  @Action(DesignerUpdateAggregateInSorts)
  updateAggregateInSorts(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { column }: DesignerUpdateAggregateInSorts
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    if (isEmpty(analysis.sipQuery.sorts)) {
      return;
    }
    sipQuery.sorts.forEach(sort => {
      if (sort.columnName === column.columnName) {
        sort.aggregate = column.aggregate;
      }
    });
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery } }
    });
  }
}
