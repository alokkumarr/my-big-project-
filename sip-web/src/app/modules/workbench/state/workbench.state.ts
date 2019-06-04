import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import {
  LoadJobs,
  LoadJobLogs,
  LoadChannelList,
  LoadRouteList,
  SelectChannelType,
  SelectChannel,
  SelectRoute
} from './workbench.actions';
import { DatasourceService } from '../services/datasource.service';
import { WorkbenchStateModel } from '../models/workbench.interface';
import { CHANNEL_TYPES } from '../wb-comp-configs';

const defaultChannelType = CHANNEL_TYPES[0];

const defaultWorkbenchState: WorkbenchStateModel = {
  channelTypeList: CHANNEL_TYPES,
  selectedChannelType: defaultChannelType,
  channelList: [],
  selectedChannel: null,
  routeList: [],
  selectedRoute: null,
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
  static channelIdList(state: WorkbenchStateModel) {
    return state.channelList;
  }
  @Selector()
  static routeNameList(state: WorkbenchStateModel) {
    return state.routeList;
  }

  @Selector()
  static selectedChannelType(state: WorkbenchStateModel) {
    return state.selectedChannelType;
  }
  @Selector()
  static selectedChannelId(state: WorkbenchStateModel) {
    return state.selectedChannel;
  }
  @Selector()
  static selectedRoutName(state: WorkbenchStateModel) {
    return state.selectedRoute;
  }

  @Action(SelectChannelType)
  selectChannelType(
    { patchState }: StateContext<WorkbenchStateModel>,
    { channelType }: SelectChannelType
  ) {
    return patchState({
      selectedChannelType: channelType,
      selectedChannel: null,
      selectedRoute: null
    });
  }

  @Action(SelectChannel)
  selectChannel(
    { patchState }: StateContext<WorkbenchStateModel>,
    { channel }: SelectChannel
  ) {
    return patchState({ selectedChannel: channel, selectedRoute: null });
  }

  @Action(SelectRoute)
  SelectRoute(
    { patchState }: StateContext<WorkbenchStateModel>,
    { route }: SelectRoute
  ) {
    return patchState({ selectedRoute: route });
  }

  @Action(LoadJobs)
  loadJobs({ getState, patchState }: StateContext<WorkbenchStateModel>) {
    const { selectedChannelType, selectedChannel, selectedRoute } = getState();

    if (selectedRoute && selectedChannel) {
      return this._datasourceService
        .getJobsForRoute(selectedChannel.id, selectedRoute.id)
        .then(jobs => patchState({ jobs }));
    }
    if (selectedChannel) {
      return this._datasourceService
        .getJobsForChannel(selectedChannel.id)
        .then(jobs => patchState({ jobs }));
    }
    if (selectedChannelType) {
      return this._datasourceService
        .getJobsForChannelType(selectedChannelType.uid)
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
