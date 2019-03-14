import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/clone';
// import { setAutoFreeze } from 'immer';
// import produce from 'immer';
import { moveItemInArray } from '@angular/cdk/drag-drop';
import { DesignerStateModel } from '../types';
import {
  DesignerInitGroupAdapters,
  DesignerAddColumnToGroupAdapter,
  DesignerMoveColumnInGroupAdapter,
  DesignerRemoveColumnFromGroupAdapter
} from '../actions/designer.actions';
import { DesignerService } from '../designer.service';

// setAutoFreeze(false);

const defaultDesignerState: DesignerStateModel = {
  groupAdapters: []
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
}
