import { Component, OnInit, Input, ViewChild } from '@angular/core';
import * as isFunction from 'lodash/isFunction';
import CustomStore from 'devextreme/data/custom_store';
import { DxDataGridComponent } from 'devextreme-angular';
import { GridData, AlertIds } from '../../../alerts.interface';

const DEFAULT_PAGE_SIZE = 10;

@Component({
  selector: 'alerts-grid',
  templateUrl: './alerts-grid.component.html',
  styleUrls: ['./alerts-grid.component.scss']
})
export class AlertsGridComponent implements OnInit {
  public remoteOperations = {};
  data: any = {};
  alertsDataLoader: (options: {}) => Promise<{
    data: any[];
    totalCount: number;
  }>;
  selectedAlertIds: AlertIds;
  public DEFAULT_PAGE_SIZE = DEFAULT_PAGE_SIZE;
  public enablePaging = false;

  @ViewChild(DxDataGridComponent, { static: true }) dataGrid: DxDataGridComponent;

  constructor() {}

  @Input('alertsDataLoader')
  set setAlertsDataLoader(
    alertsDataLoader: (options: {}) => Promise<GridData>
  ) {
    if (isFunction(alertsDataLoader)) {
      this.alertsDataLoader = alertsDataLoader;
      this.data = new CustomStore({
        load: options =>
          this.alertsDataLoader(options).then(result => {
            this.enablePaging = result.totalCount > DEFAULT_PAGE_SIZE;
            return result;
          }),
        key: ['alertTriggerSysId', 'alertRulesSysId']
      });
    } else {
      throw new Error('alertsDataLoader should be a function');
    }
  }

  ngOnInit() {}

  onRowExpanding(rowKeys) {
    this.dataGrid.instance.collapseAll(-1);
    this.selectedAlertIds = rowKeys;
  }
}
