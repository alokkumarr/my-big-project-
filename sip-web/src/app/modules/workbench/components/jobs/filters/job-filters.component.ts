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
import { WorkbenchState } from '../../../state/workbench.state';
const baseUrl = 'workbench/datasource/jobs?channelTypeId=';

@Component({
  selector: 'job-filters',
  templateUrl: 'job-filters.component.html',
  styleUrls: ['job-filters.component.scss']
})
export class JobFiltersComponent implements OnInit {
  @Select(WorkbenchState.channelTypeList)
  channelTypeList$: Observable<ChannelType[]>;
  @Select(WorkbenchState.channelList) channelList$: Observable<
    ChannelForJobs[]
  >;
  @Select(WorkbenchState.routeList) routeList$: Observable<RouteForJobs[]>;

  @Select(WorkbenchState.selectedChannelTypeId)
  selectedChannelTypeId$: Observable<string>;
  @Select(WorkbenchState.selectedChannelId)
  selectedChannelId$: Observable<number>;
  @Select(WorkbenchState.selectedRouteId)
  selectedRouteId$: Observable<number>;

  private selectedChannelTypeId;
  private selectedChannelId;
  private selectedRouteId;

  public isApplyDisabled = false;
  public isResetDisabled = false;

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
    this.onFilterChange();
  }

  onChannelTypeSelected(selectedChannelTypeId) {
    this.selectedChannelTypeId = selectedChannelTypeId;
    this.onFilterChange();
    // TODO when other channel types are supported load channels here
  }

  onChannelSelected(selectedChannelId) {
    this.selectedChannelId = selectedChannelId;
    this._store.dispatch(new LoadRouteList(selectedChannelId));
    this.onFilterChange();
  }

  onRouteSelected(selectedRouteId) {
    this.selectedRouteId = selectedRouteId;
    this.onFilterChange();
  }

  onFilterChange() {
    const {
      selectedChannelTypeId,
      selectedChannelId,
      selectedRouteId
    } = this._store.snapshot().workbench;
    this.isApplyDisabled =
      selectedChannelTypeId === this.selectedChannelTypeId &&
      selectedChannelId === this.selectedChannelId &&
      selectedRouteId === this.selectedRouteId;
    this.isResetDisabled = !(selectedChannelId || selectedRouteId);
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
    setTimeout(() => this.onFilterChange());
    this._router.navigateByUrl(url);
  }

  onReset() {
    const url = `${baseUrl}${this.selectedChannelTypeId}`;
    setTimeout(() => this.onFilterChange());
    this._router.navigateByUrl(url);
  }
}
