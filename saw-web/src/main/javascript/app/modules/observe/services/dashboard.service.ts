import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class DashboardService {

  public dashboardWidgets = new Subject();

  constructor() { }
}
