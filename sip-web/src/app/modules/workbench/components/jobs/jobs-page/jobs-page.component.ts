import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { DEFAULT_CHANNEL_TYPE } from '../../../wb-comp-configs';
import {
  LoadJobs,
  LoadChannelList,
  LoadRouteList,
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId
} from '../../../state/workbench.actions';
import {
  Job,
  ChannelType,
  ChannelForJobs,
  RouteForJobs
} from '../../../models/workbench.interface';

const baseUrl = 'workbench/datasource/jobs?channelTypeId=';
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

  @Select(state => state.workbench.selectedChannelTypeId)
  selectedChannelTypeId$: Observable<string>;
  @Select(state => state.workbench.selectedChannelId)
  selectedChannelId$: Observable<number>;
  @Select(state => state.workbench.selectedRouteId)
  selectedRouteId$: Observable<number>;

  @Select(state => state.workbench.jobs) jobs$: Observable<Job[]>;

  constructor(
    private _store: Store,
    private _route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.reRouteToDefaultChannelTypeIfNeeded();
    this._store.dispatch(new LoadChannelList());
    this._route.queryParams.subscribe(params => {
      const { channelTypeId, channelId, routeId } = params;
      const paramChannelId = channelId ? parseInt(channelId, 10) : channelId;
      const paramRouteId = routeId ? parseInt(routeId, 10) : routeId;
      const {
        selectedChannelTypeId,
        selectedChannelId,
        selectedRouteId
      } = this._store.snapshot().workbench;

      if (selectedChannelTypeId !== channelTypeId) {
        this._store.dispatch(new SelectChannelTypeId(channelTypeId));
        this._store.dispatch(new LoadChannelList());
      }
      if (selectedChannelId !== channelId) {
        this._store.dispatch(new SelectChannelId(paramChannelId));
        this._store.dispatch(new LoadRouteList(paramChannelId));
      }
      if (selectedRouteId !== routeId) {
        this._store.dispatch(new SelectRouteId(paramRouteId));
      }

      this._store.dispatch(new LoadJobs());
    });
  }

  onChannelTypeSelected(channelTypeId) {
    const { channelId, routeId } = this._route.snapshot.queryParams;
    const channelIdQueryParam = channelId ? `&channelId=${channelId}` : '';
    const routeIdIdQueryParam = routeId ? `&routeId=${routeId}` : '';
    const url = `${baseUrl}${channelTypeId}${channelIdQueryParam}${routeIdIdQueryParam}`;
    this._router.navigateByUrl(url);
  }

  onChannelSelected(channelId) {
    const { channelTypeId, routeId } = this._route.snapshot.queryParams;
    const routeIdIdQueryParam = routeId ? `&routeId=${routeId}` : '';
    const url = `${baseUrl}${channelTypeId}&channelId=${channelId}${routeIdIdQueryParam}`;
    this._router.navigateByUrl(url);
  }

  onRouteSelected(routeId) {
    const { channelTypeId, channelId } = this._route.snapshot.queryParams;
    const channelIdQueryParam = channelId ? `&channelId=${channelId}` : '';
    const url = `${baseUrl}${channelTypeId}${channelIdQueryParam}&routeId=${routeId}`;
    this._router.navigateByUrl(url);
  }

  reRouteToDefaultChannelTypeIfNeeded() {
    if (!this._route.snapshot.queryParams.channelTypeId) {
      const { channelId, routeId } = this._route.snapshot.queryParams;
      const channelIdQueryParam = channelId ? `&channelId=${channelId}` : '';
      const routeIdIdQueryParam = routeId ? `&routeId=${routeId}` : '';
      const defaultChannelTypeId = DEFAULT_CHANNEL_TYPE.uid;
      const url = `${baseUrl}${defaultChannelTypeId}${channelIdQueryParam}${routeIdIdQueryParam}`;
      this._router.navigateByUrl(url);
    }
  }
}
