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
  dataLoader: (options: {}) => Promise<{ data: any[]; totalCount: number }>;

  constructor() {}

  @Input('dataLoader')
  set setDataLoader(
    dataLoader: (options: {}) => Promise<{ data: any[]; totalCount: number }>
  ) {
    if (isFunction(dataLoader)) {
      this.dataLoader = dataLoader;
      this.data = new CustomStore({
        load: options => this.dataLoader(options)
      });
      this.remoteOperations = { paging: true };
      this.paging = { pageSize: 25, pageIndex: 0 };
    } else {
      throw new Error('Data loader requires a Function');
    }
  }

  ngOnInit() {}
}
