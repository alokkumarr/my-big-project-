import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/interval';
import 'rxjs/add/operator/map';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';
import { Subscription } from 'rxjs/Subscription';
import { Dashboard } from '../models/dashboard.interface';

@Injectable()
export class DashboardService {
  public dashboardWidgets = new BehaviorSubject({});
  public onEditItem = new Subject(); // use for signalling start of editing an item
  public onUpdateItem = new BehaviorSubject<any>({}); // use for signalling finishing editing of item
  public onFilterKPI = new Subject(); // used for communication between kpi filter and individual KPI fields

  private autoRefreshListeners: {
    [key: string]: Subject<{ dashboardId: string }>;
  } = {};

  constructor() {}

  getAutoRefreshSubject(dashboardId: string): Subject<{ dashboardId: string }> {
    return this.autoRefreshListeners[dashboardId];
  }
  /**
   * setAutoRefresh
   * Clears out existing auto refresh and sets a new one for dashboard id
   *
   * @param {Dashboard} dashboard
   * @returns {undefined}
   */
  setAutoRefresh(dashboard: Dashboard) {
    if (this.autoRefreshListeners[dashboard.entityId]) {
      this.autoRefreshListeners[dashboard.entityId].unsubscribe();
      delete this.autoRefreshListeners[dashboard.entityId];
    }

    if (dashboard.autoRefreshEnabled) {
      const observable = Observable.interval(
        dashboard.refreshIntervalSeconds * 1000
      ).map(() => ({
        dashboardId: dashboard.entityId
      }));
      const sub: Subject<{ dashboardId: string }> = new Subject();
      observable.subscribe(sub);
      this.autoRefreshListeners[dashboard.entityId] = sub;
    }
  }

  unsetAutoRefresh(dashboardId: string) {
    if (this.autoRefreshListeners[dashboardId]) {
      this.autoRefreshListeners[dashboardId].unsubscribe();
      delete this.autoRefreshListeners[dashboardId];
    }
  }
}
