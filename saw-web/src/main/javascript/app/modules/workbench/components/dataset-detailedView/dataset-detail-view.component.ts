
import { Component, OnInit, AfterViewInit, OnDestroy } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { UIRouter } from '@uirouter/angular';

import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { WorkbenchService } from '../../services/workbench.service';

import * as isUndefined from 'lodash/isUndefined';

const template = require('./dataset-detail-view.component.html');
require('./dataset-detail-view.component.scss');

@Component({
  selector: 'dataset-detail-view',
  template,
  styles: []
})

export class DatasetDetailViewComponent implements OnInit, OnDestroy {
  private dsMetadata;
  private timer;
  private timerSubscription;
  private poll: boolean;
  private interval = 5000;
  private previewData: Array<any>;

  constructor(
    private router: UIRouter,
    private dxDataGrid: dxDataGridService,
    private workBench: WorkbenchService
  ) { }

  ngOnInit() {
    this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
    this.triggerPreview();

  }

  ngOnDestroy() {
    if (this.poll) {
      this.stopPolling();
    }
    this.workBench.removeDataFromLS('dsMetadata');
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }

  triggerPreview() {
    this.workBench.triggerDatasetPreview(this.dsMetadata.system.name).subscribe((data) => {
      if (!isUndefined(data.id)) {
        this.startPolling(data.id);
      }
    });
  }

  getPreview(id) {
    this.workBench.getDatasetPreviewData(id).subscribe((data) => {
      if (isUndefined(data.status)) {
        this.previewData = data.rows;
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
    this.timer = Observable.timer(0, this.interval);
    this.timerSubscription = this.timer.subscribe(() => {
      this.getPreview(id);
    });
    this.poll = true;
  }

  stopPolling() {
    this.timerSubscription.unsubscribe();
    this.poll = false;
  }
}
