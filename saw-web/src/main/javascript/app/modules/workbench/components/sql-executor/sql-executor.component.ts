
import { Component, Inject, ViewChild, OnInit, Input, AfterViewInit, OnDestroy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { UIRouter } from '@uirouter/angular';

import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { ToastService } from '../../../../common/services/toastMessage.service';

import * as get from 'lodash/get';
import { SQL_AQCTIONS } from '../../sample-data';

import { SqlScriptComponent } from './query/sql-script.component';
import { DetailsDialogComponent } from './dataset-details-dialog/details-dialog.component';
import { WorkbenchService } from '../../services/workbench.service';

const template = require('./sql-executor.component.html');
require('./sql-executor.component.scss');

@Component({
  selector: 'sql-executor',
  template
})
export class SqlExecutorComponent implements OnInit, OnDestroy {
  private artifacts: any;
  private gridConfig: Array<any>;
  private gridData = new BehaviorSubject([]);
  private dsMetadata: any;
  private datasetDetails: Array<any>;
  private appliedActions: Array<any> = SQL_AQCTIONS;
  private scriptHeight: number = 75;
  private previewHeight: number = 25;
  private query: string = '';

  constructor(
    private router: UIRouter,
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private notify: ToastService
  ) {}

  @ViewChild('sqlscript') private scriptComponent: SqlScriptComponent;

  ngOnInit() {
    this.getPageData();
  }

  ngOnDestroy() {
    this.workBench.removeDataFromLS('dsMetadata');
  }

  getPageData(): void {
    this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
    this.workBench.getDatasetDetails('this.datasetID').subscribe(data => {
      this.artifacts = data.artifacts;
    });
  }

  runScript(): void {
    this.scriptComponent.executeQuery();
  }

  sendDataToPreview(data) {
    this.gridData.next(data);
  }

  getQuery(data) {
    this.query = data;
  }

  openSaveDialog() {
    const detailsDialogRef = this.dialog.open(DetailsDialogComponent, {
      hasBackdrop: false,
      width: '400px',
      height: '300px'
    });

    detailsDialogRef
      .afterClosed()
      .subscribe(data => {
        if (data !== false) {
          this.datasetDetails = data;
          this.scriptComponent.onCreateEmitter();
          this.triggerSQL(data);
        }
      });
  }

  toggleViewMode(fullScreenPreview) {
    this.previewHeight = fullScreenPreview ? 100 : 60;
    this.scriptHeight = fullScreenPreview ? 0 : 40;
  }

  triggerSQL(data) {
    const payload = {
      'name': data.name,
      'input': this.dsMetadata.system.name,
      'component': 'sql',
      'configuration': {
        'script': this.query
      }
    }
    this.workBench.triggerParser(payload).subscribe(data => {
      this.notify.info('SQL_Executor_triggered_successfully', 'Creating Dataset', { hideDelay: 9000 });
    });
    this.router.stateService.go('workbench.datasets');
  }

  previewAction(action) {
    this.scriptComponent.viewAction(action);
  }

  backToDS() {
    this.router.stateService.go('workbench.datasets');
  }
}
