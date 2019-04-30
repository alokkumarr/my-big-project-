import { Component, OnInit, Input } from '@angular/core';
import * as isFunction from 'lodash/isFunction';
import CustomStore from 'devextreme/data/custom_store';

@Component({
  selector: 'alerts-grid',
  templateUrl: './alerts-grid.component.html',
  styleUrls: ['./alerts-grid.component.scss']
})
export class AlertsGridComponent implements OnInit {
  public remoteOperations = {};
  public paging;
  public pager = {
    showNavigationButtons: true,
    allowedPageSizes: [25, 50, 75, 100],
    showPageSizeSelector: true
  };
  data: any = {};
  alertsDataLoader: (options: {}) => Promise<{
    data: any[];
    totalCount: number;
  }>;

  constructor() {}

  @Input('alertsDataLoader')
  set setAlertsDataLoader(
    alertsDataLoader: (options: {}) => Promise<{
      data: any[];
      totalCount: number;
    }>
  ) {
    if (isFunction(alertsDataLoader)) {
      this.alertsDataLoader = alertsDataLoader;
      this.data = new CustomStore({
        load: options => this.alertsDataLoader(options),
        key: ['alertTriggerSysId']
      });
    } else {
      throw new Error('alertsDataLoader should be a function');
    }
  }

  ngOnInit() {}
}
