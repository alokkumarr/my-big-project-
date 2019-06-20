import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

import { DEFAULT_CHANNEL_TYPE } from '../../../wb-comp-configs';
import {
  LoadJobs,
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId
} from '../../../state/workbench.actions';
import { Job } from '../../../models/workbench.interface';

const baseUrl = 'workbench/datasource/jobs?channelTypeId=';
@Component({
  selector: 'jobs-page',
  templateUrl: 'jobs-page.component.html',
  styleUrls: ['jobs-page.component.scss']
})
export class JobsPageComponent implements OnInit {
  @Select(state => state.workbench.jobs) jobs$: Observable<Job[]>;

  constructor(
    private _store: Store,
    private _route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.reRouteToDefaultChannelTypeIfNeeded();
    this._route.queryParams.subscribe(params => {
      const { channelTypeId, channelId, routeId } = params;
      const paramChannelId = channelId ? parseInt(channelId, 10) : null;
      const paramRouteId = routeId ? parseInt(routeId, 10) : null;
      const {
        selectedChannelTypeId,
        selectedChannelId,
        selectedRouteId
      } = this._store.snapshot().workbench;

      if (selectedChannelTypeId !== channelTypeId) {
        this._store.dispatch(new SelectChannelTypeId(channelTypeId));
      }
      if (selectedChannelId !== paramChannelId) {
        this._store.dispatch(new SelectChannelId(paramChannelId));
      }
      if (selectedRouteId !== paramRouteId) {
        this._store.dispatch(new SelectRouteId(paramRouteId));
      }

      this._store.dispatch(new LoadJobs());
    });
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
