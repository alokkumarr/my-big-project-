import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';

import {
  LoadJobs,
  LoadChannelList,
  LoadRouteList,
  SelectChannelType,
  SelectChannel,
  SelectRoute
} from '../../../state/workbench.actions';
import {
  Job,
  ChannelType,
  ChannelForJobs,
  RouteForJobs
} from '../../../models/workbench.interface';

@Component({
  selector: 'jobs-page',
  templateUrl: 'jobs-page.component.html',
  styleUrls: ['jobs-page.component.scss']
})
export class JobsPageComponent implements OnInit {
  @Select(state => state.workbench.channelTypeList)
  channelTypeList$: Observable<ChannelType[]>;
  @Select(state => state.workbench.channelList) channelList$: Observable<
    ChannelForJobs[]
  >;
  @Select(state => state.workbench.routeList) routeList$: Observable<
    RouteForJobs[]
  >;

  @Select(state => state.workbench.selectedChannelType)
  selectedChannelType$: Observable<ChannelType>;
  @Select(state => state.workbench.selectedChannel)
  selectedChannel$: Observable<ChannelForJobs>;
  @Select(state => state.workbench.selectedRoute)
  selectedRoute$: Observable<RouteForJobs>;

  @Select(state => state.workbench.jobs) jobs$: Observable<Job[]>;

  constructor(private _store: Store) {}

  ngOnInit() {
    this._store.dispatch(new LoadChannelList());
    this._store.dispatch(new LoadJobs());
  }

  onChannelTypeSelected(channelType) {
    this._store.dispatch(new SelectChannelType(channelType));
    this._store.dispatch(new LoadChannelList());
    this._store.dispatch(new LoadJobs());
  }

  onChannelSelected(channel) {
    this._store.dispatch(new SelectChannel(channel));
    this._store.dispatch(new LoadRouteList(channel.id));
    this._store.dispatch(new LoadJobs());
  }

  onRouteSelected(route) {
    this._store.dispatch(new SelectRoute(route));
    this._store.dispatch(new LoadJobs());
  }
}
