import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class DashboardService {
  public dashboardWidgets = new BehaviorSubject({});
  public onEditItem = new BehaviorSubject<any>({}); // use for signalling start of editing an item
  public onUpdateItem = new BehaviorSubject<any>({}); // use for signalling finishing editing of item

  constructor() {}
}
