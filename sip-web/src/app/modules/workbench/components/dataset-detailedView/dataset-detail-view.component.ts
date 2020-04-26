import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { timer } from 'rxjs';
import { Router } from '@angular/router';

import { DxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { WorkbenchService } from '../../services/workbench.service';
import { DxDataGridComponent } from 'devextreme-angular/ui/data-grid';

import { isUndefined, forEach } from 'lodash';

@Component({
  selector: 'dataset-detail-view',
  templateUrl: './dataset-detail-view.component.html',
  styleUrls: ['./dataset-detail-view.component.scss']
})
export class DatasetDetailViewComponent implements OnInit, OnDestroy {
  public dsMetadata;
  public timer;
  public timerSubscription;
  public poll: boolean;
  public interval = 5000;
  public previewData: Array<any> = [];
  public previewStatus: string;

  constructor(
    public router: Router,
    public dxDataGrid: DxDataGridService,
    public workBench: WorkbenchService
  ) {
    this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
  }

  @ViewChild('dpGrid', { static: false }) dataGrid: DxDataGridComponent;

  ngOnInit() {
    if (this.dsMetadata.asOfNow.status === 'SUCCESS') {
      this.previewStatus = 'queued';
      this.getPreview();
    } else {
      this.previewStatus = 'failed';
    }
  }

  ngOnDestroy() {
    if (
      !isUndefined(this.timerSubscription) &&
      !this.timerSubscription.isStopped
    ) {
      // this.stopPolling();
    }
    this.workBench.removeDataFromLS('dsMetadata');
  }

  backToDS() {
    this.router.navigate(['workbench', 'dataobjects']);
  }

  triggerPreview() {
    this.workBench
      .triggerDatasetPreview(this.dsMetadata.system.name)
      .subscribe(data => {
        this.previewStatus = 'queued';
        if (!isUndefined(data.id)) {
          //this.startPolling(data.id);
        }
      });
  }

  getPreview() {
    this.workBench
      .retrievePreview(this.dsMetadata.system.name)
      .subscribe(data => {
        this.previewStatus = data.status;
        if (this.previewStatus === 'success') {
          this.previewData = JSON.parse(data.rows);
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
      //this.getPreview(id);
    });
    this.poll = true;
  }

  stopPolling() {
    this.timerSubscription && this.timerSubscription.unsubscribe();
    this.poll = false;
  }

  tabChanged(event): void {
    if (event.index === 1 && this.previewStatus === 'success') {
      this.dataGrid.instance.refresh();
    }
  }

  /**
   * Sets the grid column caption same as dataField for the grid.
   * Columns were not aligned properly. So Making sure that columns are aligned to left.
   * Added as part of SIP-9094.
   * @param columns
   */
  onCustomizeColumns(columns) {
    forEach(columns, col => {
      col.caption = col.dataField;
      col.alignment = 'left';
    });
  }
}
