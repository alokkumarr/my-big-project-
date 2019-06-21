import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import * as find from 'lodash/find';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';
import {
  LoadJobs,
  LoadJobLogs,
  LoadChannelList,
  LoadRouteList,
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId
} from './workbench.actions';
import { DatasourceService } from '../services/datasource.service';
import { WorkbenchStateModel } from '../models/workbench.interface';
import { CHANNEL_TYPES, DEFAULT_CHANNEL_TYPE } from '../wb-comp-configs';

const defaultWorkbenchState: WorkbenchStateModel = {
  channelTypeList: CHANNEL_TYPES,
  selectedChannelTypeId: DEFAULT_CHANNEL_TYPE.uid,
  channelList: [],
  selectedChannelId: null,
  routeList: [],
  selectedRouteId: null,
  jobs: [],
  jobLogs: []
};

@State<WorkbenchStateModel>({
  name: 'workbench',
  defaults: <WorkbenchStateModel>cloneDeep(defaultWorkbenchState)
})
export class WorkbenchState {
  constructor(private _datasourceService: DatasourceService) {}

  @Selector()
  static jobs(state: WorkbenchStateModel) {
    return state.jobs;
  }

  @Selector()
  static channelTypeList(state: WorkbenchStateModel) {
    return state.channelTypeList;
  }
  @Selector()
  static channelList(state: WorkbenchStateModel) {
    return state.channelList;
  }
  @Selector()
  static routeList(state: WorkbenchStateModel) {
    return state.routeList;
  }

  @Selector()
  static selectedChannelTypeId(state: WorkbenchStateModel) {
    return state.selectedChannelTypeId;
  }
  @Selector()
  static selectedChannelId(state: WorkbenchStateModel) {
    return state.selectedChannelId;
  }
  @Selector()
  static selectedRouteId(state: WorkbenchStateModel) {
    return state.selectedRouteId;
  }

  @Selector()
  static jobFilters(state: WorkbenchStateModel) {
    const {
      selectedChannelTypeId,
      channelTypeList,
      selectedChannelId,
      channelList,
      selectedRouteId,
      routeList
    } = state;
    const targetChannelType = find(
      channelTypeList,
      ({ uid }) => selectedChannelTypeId === uid
    );
    const targetChannel = find(
      channelList,
      ({ id }) => selectedChannelId === id
    );
    const targetRoute = find(routeList, ({ id }) => selectedRouteId === id);

    return fpPipe(
      fpFilter(([, value]) => Boolean(value)),
      fpMap(([key, value]) => `${key}: ${value.name}`)
    )([
      ['Channel type', targetChannelType],
      ['Channel name', targetChannel],
      ['Route name', targetRoute]
    ]);
  }

  @Action(SelectChannelTypeId)
  selectChannelTypeId(
    { patchState }: StateContext<WorkbenchStateModel>,
    { channelType }: SelectChannelTypeId
  ) {
    return patchState({
      selectedChannelTypeId: channelType,
      selectedChannelId: null,
      selectedRouteId: null
    });
  }

  @Action(SelectChannelId)
  selectChannelId(
    { patchState }: StateContext<WorkbenchStateModel>,
    { channelId }: SelectChannelId
  ) {
    return patchState({ selectedChannelId: channelId, selectedRouteId: null });
  }

  @Action(SelectRouteId)
  SelectRouteId(
    { patchState }: StateContext<WorkbenchStateModel>,
    { routeId }: SelectRouteId
  ) {
    return patchState({ selectedRouteId: routeId });
  }

  @Action(LoadJobs)
  loadJobs({ getState, patchState }: StateContext<WorkbenchStateModel>) {
    const {
      selectedChannelTypeId,
      selectedChannelId,
      selectedRouteId
    } = getState();

    if (selectedRouteId && selectedChannelId) {
      return this._datasourceService
        .getJobsForRoute(selectedChannelId, selectedRouteId)
        .then(jobs => patchState({ jobs }));
    }
    if (selectedChannelId) {
      return this._datasourceService
        .getJobsForChannel(selectedChannelId)
        .then(jobs => patchState({ jobs }));
    }
    if (selectedChannelTypeId) {
      return this._datasourceService
        .getJobsForChannelType(selectedChannelTypeId)
        .then(jobs => patchState({ jobs }));
    }
  }

  @Action(LoadJobLogs)
  loadJobLogs(
    { patchState }: StateContext<WorkbenchStateModel>,
    { jobId }: LoadJobLogs
  ) {
    return this._datasourceService
      .getJobLogs(jobId)
      .toPromise()
      .then(jobLogs => patchState({ jobLogs }));
  }

  @Action(LoadChannelList)
  loadChannelList({ patchState }: StateContext<WorkbenchStateModel>) {
    return this._datasourceService
      .getChannelListForJobs()
      .toPromise()
      .then(channelList => patchState({ channelList }));
  }

  @Action(LoadRouteList)
  loadRouteList(
    { patchState }: StateContext<WorkbenchStateModel>,
    { channelId }: LoadRouteList
  ) {
    return this._datasourceService
      .getRouteListForJobs(channelId)
      .toPromise()
      .then(routeList => patchState({ routeList }));
  }
}
