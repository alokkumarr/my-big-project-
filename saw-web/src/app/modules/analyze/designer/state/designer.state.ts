import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFlatMap from 'lodash/fp/flatMap';
import * as fpReduce from 'lodash/fp/reduce';
// import { setAutoFreeze } from 'immer';
// import produce from 'immer';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { DesignerStateModel, DSLChartOptionsModel } from '../types';
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
  DesignerAddArtifactColumn,
  DesignerRemoveArtifactColumn,
  DesignerUpdateArtifactColumn,
  DesignerReorderArtifactColumns
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

  @Action(DesignerAddArtifactColumn)
  addArtifactColumn(
    { getState, patchState, dispatch }: StateContext<DesignerStateModel>,
    { artifactColumn }: DesignerAddArtifactColumn
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    let artifacts = sipQuery.artifacts;

    const artifactsName =
      artifactColumn.table || (<any>artifactColumn).tableName;

    /* Find the artifact inside sipQuery of analysis stored in state */
    const artifactIndex = artifacts.findIndex(
      artifact => artifact.artifactsName === artifactsName
    );

    const artifactColumnToBeAdded = {
      aggregate: artifactColumn.aggregate,
      alias: artifactColumn.alias || (<any>artifactColumn).aliasName,
      area: artifactColumn.area,
      columnName: artifactColumn.columnName,
      displayType:
        artifactColumn.displayType || (<any>artifactColumn).comboType,
      dataField: artifactColumn.name || artifactColumn.columnName,
      displayName: artifactColumn.displayName,
      dateFormat: artifactColumn.dateFormat,
      groupInterval: artifactColumn.groupInterval,
      name: artifactColumn.name,
      type: artifactColumn.type,
      table: artifactColumn.table || (<any>artifactColumn).tableName
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

    patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, artifacts } }
    });
    return dispatch(new DesignerReorderArtifactColumns());
  }

  @Action(DesignerRemoveArtifactColumn)
  removeArtifactColumn(
    { getState, patchState, dispatch }: StateContext<DesignerStateModel>,
    { artifactColumn }: DesignerRemoveArtifactColumn
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;

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
      field => field.columnName === artifactColumn.columnName
    );

    artifacts[artifactIndex].fields.splice(artifactColumnIndex, 1);

    patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, artifacts } }
    });
    return dispatch(new DesignerReorderArtifactColumns());
  }

  @Action(DesignerUpdateArtifactColumn)
  updateArtifactColumn(
    { getState, patchState }: StateContext<DesignerStateModel>,
    { artifactColumn }: DesignerUpdateArtifactColumn
  ) {
    const analysis = getState().analysis;
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;

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
      field => field.columnName === artifactColumn.columnName
    );

    artifacts[artifactIndex].fields[artifactColumnIndex] = {
      ...artifacts[artifactIndex].fields[artifactColumnIndex],
      ...artifactColumn
    };

    return patchState({
      analysis: { ...analysis, sipQuery: { ...sipQuery, artifacts } }
    });
  }

  @Action(DesignerReorderArtifactColumns)
  reorderArtifactColumns({
    getState,
    patchState
  }: StateContext<DesignerStateModel>) {
    const { analysis, groupAdapters } = getState();
    const sipQuery = analysis.sipQuery;
    const artifacts = sipQuery.artifacts;

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
      analysis: { ...analysis, ...metadata }
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
    { patchState, getState, dispatch }: StateContext<DesignerStateModel>,
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
    patchState({ groupAdapters: [...groupAdapters] });
    return dispatch(new DesignerAddArtifactColumn(artifactColumn));
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
    return dispatch(new DesignerRemoveArtifactColumn(column));
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
    return dispatch(new DesignerReorderArtifactColumns());
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
        filter.model.preset === CUSTOM_DATE_PRESET_VALUE
      ) {
        filter.model = {
          operator: 'BTW',
          otherValue: filter.model.lte
            ? moment(filter.model.lte).valueOf()
            : null,
          value: filter.model.gte ? moment(filter.model.gte).valueOf() : null,
          format: 'epoch_millis'
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
}
