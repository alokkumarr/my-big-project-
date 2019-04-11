import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';
import * as flatMap from 'lodash/flatMap';
import * as values from 'lodash/values';
// import { setAutoFreeze } from 'immer';
// import produce from 'immer';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import {
  DesignerStateModel,
  AnalysisDSL,
  DSLChartOptionsModel
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
  DesignerUpdateAnalysisChartType,
  DesignerUpdateSorts,
  DesignerUpdateFilters,
  DesignerUpdatebooleanCriteria,
  DesignerUpdateAnalysisChartTitle,
  DesignerUpdateAnalysisChartInversion,
  DesignerUpdateAnalysisChartLegend,
  DesignerUpdateAnalysisChartLabelOptions,
  DesignerUpdateAnalysisChartXAxis,
  DesignerUpdateAnalysisChartYAxis,
  DesignerUpdateFieldFormat
} from '../actions/designer.actions';
import { DesignerService } from '../designer.service';
import { CUSTOM_DATE_PRESET_VALUE } from './../../../analyze/consts';

import moment from 'moment';

// setAutoFreeze(false);

const defaultDesignerState: DesignerStateModel = {
  groupAdapters: [],
  analysis: null
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

@State<DesignerStateModel>({
  name: 'designerState',
  defaults: <DesignerStateModel>cloneDeep(defaultDesignerState)
})
export class DesignerState {
  constructor(private _designerService: DesignerService) {}

  @Selector()
  static groupAdapters(state: DesignerStateModel) {
    return state.groupAdapters;
  }

  @Selector()
  static dslAnalysis(state: DesignerStateModel): AnalysisDSL {
    const fields: Array<any> = flatMap(
      state.groupAdapters,
      adapter => adapter.artifactColumns
    );
    const artifacts = {};
    fields.forEach(field => {
      artifacts[field.table] = artifacts[field.table] || {
        artifactsName: field.table,
        fields: []
      };
      artifacts[field.table].fields.push({
        aggregate: field.aggregate,
        alias: field.alias || field.aliasName,
        area: field.area,
        columnName: field.columnName,
        comboType: field.comboType,
        dataField: field.name || field.columnName,
        displayName: field.displayName,
        dateFormat: field.dateFormat,
        groupInterval: field.groupInterval,
        name: field.name,
        type: field.type,
        table: field.table || field.tableName
      });
    });

    const sipQuery = {
      ...state.analysis.sipQuery,
      artifacts: values(artifacts)
    };
    return { ...state.analysis, sipQuery };
  }

  @Action(DesignerUpdateAnalysisMetadata)
  updateCategoryId(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { metadata }: DesignerUpdateAnalysisMetadata
  ) {
    const analysis = getState().analysis;
    return patchState({
      analysis: { ...analysis, ...metadata }
    });
  }

  @Action(DesignerUpdateFieldFormat)
  updateFieldFormat({
    patchState,
    getState
  }: StateContext<DesignerStateModel>) {
    // not doing much right now. This placeholder makes sure the store
    // is notified of change in analysis, and refreshes its observables.

    const analysis = getState().analysis;
    return patchState({
      analysis
    });
  }

  @Action(DesignerUpdateAnalysisChartType)
  updateChartType(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { chartType }: DesignerUpdateAnalysisChartType
  ) {
    const analysis = getState().analysis;
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
    return patchState({
      analysis: {
        ...analysis,
        chartOptions: { ...chartOptions, chartType }
      }
    });
  }

  @Action(DesignerUpdateAnalysisChartInversion)
  updateChartInversion(
    { patchState, getState }: StateContext<DesignerStateModel>,
    { isInverted }: DesignerUpdateAnalysisChartInversion
  ) {
    const analysis = getState().analysis;
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
    const chartOptions = analysis.chartOptions || defaultDSLChartOptions;
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
  initGroupAdapter(
    { patchState }: StateContext<DesignerStateModel>,
    {
      artifactColumns,
      analysisType,
      analysisSubType
    }: DesignerInitGroupAdapters
  ) {
    let groupAdapters;
    switch (analysisType) {
      case 'pivot':
        groupAdapters = this._designerService.getPivotGroupAdapters(
          artifactColumns
        );
        break;
      case 'chart':
        groupAdapters = this._designerService.getChartGroupAdapters(
          artifactColumns,
          analysisSubType
        );
        break;
      case 'map':
        groupAdapters = this._designerService.getMapGroupAdapters(
          artifactColumns,
          analysisSubType
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
    { patchState, getState }: StateContext<DesignerStateModel>,
    {
      artifactColumn,
      columnIndex,
      adapterIndex
    }: DesignerAddColumnToGroupAdapter
  ) {
    const groupAdapters = getState().groupAdapters;
    groupAdapters[adapterIndex].artifactColumns.splice(
      columnIndex,
      0,
      artifactColumn
    );
    // disabled immer because having immutability for groupAdapters causes conflicts in the designer
    // so it will stay disabled until a refactoring of the whole designer to ngxs
    // const groupAdapters = produce(getState().groupAdapters, draft => {
    //   draft[adapterIndex].artifactColumns.splice(
    //     columnIndex,
    //     0,
    //     artifactColumn
    //   );
    // });
    const adapter = groupAdapters[adapterIndex];
    adapter.transform(artifactColumn);
    adapter.onReorder(adapter.artifactColumns);
    return patchState({ groupAdapters: [...groupAdapters] });
  }

  @Action(DesignerClearGroupAdapters)
  clearGroupAdapters(
    { patchState, getState }: StateContext<DesignerStateModel>,
    {  }: DesignerClearGroupAdapters
  ) {
    const groupAdapters = getState().groupAdapters;

    forEach(groupAdapters, adapter => {
      forEach(adapter.artifactColumns, column => {
        adapter.reverseTransform(column);
      });

      adapter.artifactColumns = [];
    });
    return patchState({ groupAdapters: [...groupAdapters] });
  }

  @Action(DesignerRemoveColumnFromGroupAdapter)
  removeColumnFromGroupAdapter(
    { patchState, getState }: StateContext<DesignerStateModel>,
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
    return patchState({ groupAdapters: [...groupAdapters] });
  }

  @Action(DesignerMoveColumnInGroupAdapter)
  moveColumnInGroupAdapter(
    { patchState, getState }: StateContext<DesignerStateModel>,
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
    return patchState({ groupAdapters: [...groupAdapters] });
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
      if (filter.type === 'date' && filter.model.preset === CUSTOM_DATE_PRESET_VALUE) {
        filter.model = {
          operator: 'BTW',
          otherValue: filter.model.lte ? moment(filter.model.lte).valueOf() : null,
          value: filter.model.gte ? moment(filter.model.gte).valueOf() : null,
          format: 'epoch_millis'
        };
      }
    });
    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, filters }}
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
      analysis: { ...analysis, sipQuery: { ...sipQuery, booleanCriteria }}
    });
  }
}
