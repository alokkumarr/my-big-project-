import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

import {
  ChannelType,
  ChannelForJobs,
  RouteForJobs
} from '../../../models/workbench.interface';
import {
  LoadChannelList,
  LoadRouteList
} from '../../../state/workbench.actions';
const baseUrl = 'workbench/datasource/jobs?channelTypeId=';

@Component({
  selector: 'job-filters',
  templateUrl: 'job-filters.component.html',
  styleUrls: ['job-filters.component.scss']
})
export class JobFiltersComponent implements OnInit {
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

  private selectedChannelTypeId;
  private selectedChannelId;
  private selectedRouteId;

  constructor(private _router: Router, private _store: Store) {
    this.selectedChannelTypeId$.subscribe(selectedChannelTypeId => {
      this.selectedChannelTypeId = selectedChannelTypeId;
    });
    this.selectedChannelId$.subscribe(selectedChannelId => {
      this.selectedChannelId = selectedChannelId;
    });
    this.selectedRouteId$.subscribe(selectedRouteId => {
      this.selectedRouteId = selectedRouteId;
    });
  }

  ngOnInit() {
    this._store.dispatch(new LoadChannelList());

    const { selectedChannelId } = this._store.snapshot().workbench;

    if (selectedChannelId) {
      this._store.dispatch(new LoadRouteList(selectedChannelId));
    }
  }

  onChannelTypeSelected(selectedChannelTypeId) {
    this.selectedChannelTypeId = selectedChannelTypeId;
    // TODO when other channel types are supported load channels here
  }

  onChannelSelected(selectedChannelId) {
    this.selectedChannelId = selectedChannelId;
    this._store.dispatch(new LoadRouteList(selectedChannelId));
  }

  onRouteSelected(selectedRouteId) {
    this.selectedRouteId = selectedRouteId;
  }

  onApply() {
    const channelIdQueryParam = this.selectedChannelId
      ? `&channelId=${this.selectedChannelId}`
      : '';
    const routeIdIdQueryParam = this.selectedRouteId
      ? `&routeId=${this.selectedRouteId}`
      : '';
    const url = `${baseUrl}${
      this.selectedChannelTypeId
    }${channelIdQueryParam}${routeIdIdQueryParam}`;
    this._router.navigateByUrl(url);
  }

  onReset() {
    const url = `${baseUrl}${this.selectedChannelTypeId}`;
    this._router.navigateByUrl(url);
  }
}
