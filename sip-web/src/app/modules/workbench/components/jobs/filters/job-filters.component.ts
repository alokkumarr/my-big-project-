import { Component, OnInit } from '@angular/core';
import { Select, Store } from '@ngxs/store';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
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
import { CHANNEL_UID } from '../../../wb-comp-configs';
const baseUrl = 'workbench/datasource/jobs?channelTypeId=';

@Component({
  selector: 'job-filters',
  templateUrl: 'job-filters.component.html',
  styleUrls: ['job-filters.component.scss']
})
export class JobFiltersComponent implements OnInit {
  @Select(WorkbenchState.channelTypeList)
  channelTypeList$: Observable<ChannelType[]>;

  @Select(WorkbenchState.channelList) _stateChannelList$: Observable<
    ChannelForJobs[]
  >;
  refilterChannels$ = new BehaviorSubject(null);
  channelList$ = combineLatest(
    this._stateChannelList$,
    this.refilterChannels$
  ).pipe(
    map(([channels]) =>
      channels.filter(c => c.channelType === this.selectedChannelTypeId)
    )
  );
  @Select(WorkbenchState.routeList) routeList$: Observable<RouteForJobs[]>;

  @Select(WorkbenchState.selectedChannelTypeId)
  selectedChannelTypeId$: Observable<string>;
  @Select(WorkbenchState.selectedChannelId)
  selectedChannelId$: Observable<number>;
  @Select(WorkbenchState.selectedRouteId)
  selectedRouteId$: Observable<number>;

  public selectedChannelTypeId: CHANNEL_UID;
  public selectedChannelId;
  public selectedRouteId;

  public isApplyDisabled = false;
  public isResetDisabled = false;

  constructor(private _router: Router, private _store: Store) {
    this.selectedChannelTypeId$.subscribe(selectedChannelTypeId => {
      this.selectedChannelTypeId = selectedChannelTypeId as CHANNEL_UID;
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
    this.selectedChannelId = null;
    this.selectedRouteId = null;
    this.refilterChannels$.next(null); // signal for channel refilter
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

  clearChannel() {
    const isChannelChanged =
      this._store.snapshot().workbench.selectedChannelId !==
      this.selectedChannelId;
    if (isChannelChanged) {
      this.selectedChannelId = null;
    } else {
      this.selectedChannelId = null;
      this.onFilterChange();
    }
    this.clearRoute();
  }

  clearRoute() {
    const isRouteChanged =
      this._store.snapshot().workbench.selectedRouteId !== this.selectedRouteId;
    if (isRouteChanged) {
      this.selectedRouteId = null;
    } else {
      this.selectedRouteId = null;
      this.onFilterChange();
    }
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
    const url = `${baseUrl}${this.selectedChannelTypeId}${channelIdQueryParam}${routeIdIdQueryParam}`;
    setTimeout(() => this.onFilterChange());
    this._router.navigateByUrl(url);
  }

  onReset() {
    const url = `${baseUrl}${this.selectedChannelTypeId}`;
    setTimeout(() => this.onFilterChange());
    this._router.navigateByUrl(url);
  }
}
