import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Select, Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import CustomStore from 'devextreme/data/custom_store';

import { DEFAULT_CHANNEL_TYPE } from '../../../wb-comp-configs';
import {
  SelectChannelTypeId,
  SelectChannelId,
  SelectRouteId,
  SetJobs,
  SetLastJobsPath
} from '../../../state/workbench.actions';
import { DatasourceService } from '../../../services/datasource.service';
import { WorkbenchState } from '../../../state/workbench.state';

const DEFAULT_PAGE_SIZE = 25;
const baseUrl = 'workbench/datasource/jobs?channelTypeId=';
@Component({
  selector: 'jobs-page',
  templateUrl: 'jobs-page.component.html',
  styleUrls: ['jobs-page.component.scss']
})
export class JobsPageComponent implements OnInit {
  @Select(WorkbenchState.jobFilters) jobFilters$: Observable<string[]>;
  @Select(WorkbenchState.jobsPath) jobsPath$: Observable<string>;
  @ViewChild('sidenav') sidenav: MatSidenav;

  public pager = {
    showNavigationButtons: true,
    allowedPageSizes: [DEFAULT_PAGE_SIZE, 50, 75, 100],
    showPageSizeSelector: true,
    visible: true
  };
  public paging = { enabled: true, pageSize: DEFAULT_PAGE_SIZE, pageIndex: 0 };
  public remoteOperations = { paging: true };

  public data;

  constructor(
    private _store: Store,
    private _route: ActivatedRoute,
    private _router: Router,
    private _datasourceService: DatasourceService
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

      this.sidenav.close();
    });

    this.jobsPath$.subscribe(path => {
      this.data = new CustomStore({
        load: ({ skip, take }) => {
          const pagination = `offset=${skip}&size=${take}`;
          const jobsRequestPath = `${path}?${pagination}`;
          return this._datasourceService
            .getJobs(jobsRequestPath)
            .then(({ jobDetails, totalRows }) => {
              this._store.dispatch(new SetLastJobsPath(jobsRequestPath));
              this._store.dispatch(new SetJobs(jobDetails));
              return { data: jobDetails, totalCount: totalRows };
            });
        }
      });
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
