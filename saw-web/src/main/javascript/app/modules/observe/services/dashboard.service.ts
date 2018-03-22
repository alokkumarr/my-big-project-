import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class DashboardService {

  public dashboardWidgets = new BehaviorSubject([]);

  constructor() { }
}
