import { State, Action, StateContext, Selector } from '@ngxs/store';
import * as cloneDeep from 'lodash/cloneDeep';
import * as find from 'lodash/find';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpMap from 'lodash/fp/map';
import {
  LoadChannelList,
  LoadRouteList,
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId,
  SetJobs,
  LoadJobs,
  SetJobLogs,
  SetLastJobsPath,
  LoadJobByJobId
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
  lastJobsPath: null,
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
  static jobsPath(state: WorkbenchStateModel) {
    const { selectedChannelTypeId, selectedChannelId, selectedRouteId } = state;

    if (selectedRouteId && selectedChannelId) {
      return `${selectedChannelId}/${selectedRouteId}`;
    }
    if (selectedChannelId) {
      return `channels/${selectedChannelId}`;
    }
    if (selectedChannelTypeId) {
      return `channelTypes/${selectedChannelTypeId}`;
    }
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
      ['Source type', targetChannelType],
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

  @Action(SetJobLogs)
  setJobLogs(
    { patchState }: StateContext<WorkbenchStateModel>,
    { jobLogs }: SetJobLogs
  ) {
    return patchState({ jobLogs });
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

  @Action(LoadJobs)
  loadJobs({ getState, patchState }: StateContext<WorkbenchStateModel>) {
    const { lastJobsPath } = getState();
    return this._datasourceService
      .getJobs(lastJobsPath)
      .then(({ jobDetails }) => patchState({ jobs: jobDetails }));
  }

  @Action(LoadJobByJobId)
  loadJobByJobId(
    { patchState }: StateContext<WorkbenchStateModel>,
    { jobId }: LoadJobByJobId
  ) {
    return this._datasourceService
      .getJobById(jobId)
      .then(job => patchState({ jobs: [job] }));
  }

  @Action(SetJobs)
  setJobs(
    { patchState }: StateContext<WorkbenchStateModel>,
    { jobs }: SetJobs
  ) {
    return patchState({ jobs });
  }

  @Action(SetLastJobsPath)
  setLastJobsPath(
    { patchState }: StateContext<WorkbenchStateModel>,
    { lastJobsPath }: SetLastJobsPath
  ) {
    return patchState({ lastJobsPath });
  }
}
