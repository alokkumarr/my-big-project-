
import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { timer } from 'rxjs';
import { Router } from '@angular/router';

import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { WorkbenchService } from '../../services/workbench.service';
import { DxDataGridComponent } from 'devextreme-angular';

import * as isUndefined from 'lodash/isUndefined';

const template = require('./dataset-detail-view.component.html');
const style = require('./dataset-detail-view.component.scss');

@Component({
  selector: 'dataset-detail-view',
  template,
  styles: [
    `:host {
      'class': 'dataset-detail-view'
    }`,
    style
  ]
})

export class DatasetDetailViewComponent implements OnInit, OnDestroy {
  private dsMetadata;
  private timer;
  private timerSubscription;
  private poll: boolean;
  private interval = 5000;
  private previewData: Array<any> = [];
  private previewStatus: string;

  constructor(
    private router: Router,
    private dxDataGrid: DxDataGridService,
    private workBench: WorkbenchService
  ) {
    this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
  }

  @ViewChild('dpGrid') dataGrid: DxDataGridComponent;

  ngOnInit() {
    if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
      this.previewStatus = 'queued';
      this.triggerPreview();
    } else {
      this.previewStatus = 'failed';
    }
  }

  ngOnDestroy() {
    if (!isUndefined(this.timerSubscription) && !this.timerSubscription.isStopped) {
      this.stopPolling();
    }
    this.workBench.removeDataFromLS('dsMetadata');
  }

  backToDS() {
    this.router.navigate(['workbench', 'dataobjects']);
  }

  triggerPreview() {
    this.workBench.triggerDatasetPreview(this.dsMetadata.system.name).subscribe((data) => {
      this.previewStatus = 'queued';
      if (!isUndefined(data.id)) {
        this.startPolling(data.id);
      }
    });
  }

  getPreview(id) {
    this.workBench.getDatasetPreviewData(id).subscribe((data) => {
      this.previewStatus = data.status;
      if (this.previewStatus === 'success') {
        this.previewData = data.rows;
        setTimeout(() => {
          this.dataGrid.instance.refresh();
        });
        this.stopPolling();
      } else if (this.previewStatus === 'failed') {
        this.stopPolling();
      }
    });
  }

  /**
   * Calls list datasets api onInit and every 10 seconds or whatever set interval
   *
   * @memberof DatasetsComponent
  */
  startPolling(id) {
    this.timer = timer(0, this.interval);
    this.timerSubscription = this.timer.subscribe(() => {
      this.getPreview(id);
    });
    this.poll = true;
  }

  stopPolling() {
    this.timerSubscription.unsubscribe();
    this.poll = false;
  }

  tabChanged = (event): void => {
    if (event.index === 1 && this.previewStatus === 'success') {
      this.dataGrid.instance.refresh();
    }
  }
}
