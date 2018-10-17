import {
  Observable,
  BehaviorSubject,
  Subject,
  Subscription,
  interval
} from 'rxjs';
import { Injectable } from '@angular/core';
import { Dashboard } from '../models/dashboard.interface';

import { map } from 'rxjs/operators';

@Injectable()
export class DashboardService {
  public dashboardWidgets = new BehaviorSubject({});
  public onEditItem = new Subject(); // use for signalling start of editing an item
  public onUpdateItem = new BehaviorSubject<any>({}); // use for signalling finishing editing of item
  public autoRefreshListeners: {
    [key: string]: {
      sub: Subject<{ dashboardId: string }>;
      interval: Subscription;
    };
  } = {};

  constructor() {}

  getAutoRefreshSubject(dashboardId: string): Subject<{ dashboardId: string }> {
    return this.autoRefreshListeners[dashboardId]
      ? this.autoRefreshListeners[dashboardId].sub
      : null;
  }
  /**
   * setAutoRefresh
   * Clears out existing auto refresh and sets a new one for dashboard id
   *
   * @param {Dashboard} dashboard
   * @returns {undefined}
   */
  setAutoRefresh(dashboard: Dashboard) {
    this.unsetAutoRefresh(dashboard.entityId);

    if (dashboard.autoRefreshEnabled) {
      const observable = interval(dashboard.refreshIntervalSeconds * 1000).pipe(
        map(() => ({
          dashboardId: dashboard.entityId
        }))
      );
      const sub: Subject<{ dashboardId: string }> = new Subject();

      /* We save both - the interval subscription and subject to clean them up correctly */
      this.autoRefreshListeners[dashboard.entityId] = {
        interval: observable.subscribe(sub),
        sub
      };
    }
  }

  unsetAutoRefresh(dashboardId: string) {
    if (this.autoRefreshListeners[dashboardId]) {
      this.autoRefreshListeners[dashboardId].sub.unsubscribe();
      this.autoRefreshListeners[dashboardId].interval.unsubscribe();
      delete this.autoRefreshListeners[dashboardId];
    }
  }
}
