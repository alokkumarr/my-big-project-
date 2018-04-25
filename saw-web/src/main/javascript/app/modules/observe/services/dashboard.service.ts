import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class DashboardService {
  public dashboardWidgets = new BehaviorSubject({});
  public onEditItem = new Subject(); // use for signalling start of editing an item
  public onUpdateItem = new BehaviorSubject<any>({}); // use for signalling finishing editing of item
  public onFilterKPI = new Subject(); // used for communication between kpi filter and individual KPI fields

  constructor() {}
}
