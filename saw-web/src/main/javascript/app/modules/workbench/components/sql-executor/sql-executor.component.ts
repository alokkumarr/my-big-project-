
import { Component, Inject, ViewChild, OnInit, Input, AfterViewInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
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
export class SqlExecutorComponent implements OnInit {
  private artifacts: any;
  private gridConfig: Array<any>;
  private showProgress: boolean = false;
  private gridData = new BehaviorSubject([]);
  private datasetID: string = '';
  private datasetDetails: Array<any>;
  private appliedActions: Array<any> = SQL_AQCTIONS;
  private scriptHeight: number = 40;
  private previewHeight: number = 60;

  constructor(
    public dialogRef: MatDialogRef<SqlExecutorComponent>,
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private notify: ToastService,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) {
    if (get(data, 'id')) {
      this.datasetID = data.id;
    }
  }

  @ViewChild('sqlscript') private scriptComponent: SqlScriptComponent;

  ngOnInit() {
    this.getPageData();
  }

  getPageData(): void {
    this.showProgress = true;
    this.workBench.getDatasetDetails(this.datasetID).subscribe(data => {
      this.showProgress = false;
      this.artifacts = data.artifacts;
    });
  }

  runScript(): void {
    this.scriptComponent.executeQuery();
  }

  sendDataToPreview(data) {
    this.gridData.next(data);
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
          this.triggerSQL();
        }
      });
  }

  toggleViewMode(fullScreenPreview) {
    this.previewHeight = fullScreenPreview ? 100 : 60;
    this.scriptHeight = fullScreenPreview ? 0 : 40;
  }

  triggerSQL() {
    const payload = {
      'name': 'test_sql',
      'input': 'test_parser',
      'component': 'sql',
      'configuration': {
        'script': 'CREATE TABLE test_sql AS SELECT 1'
      }
    }
    this.workBench.triggerParser(payload).subscribe(data => {
      //this.dialogRef.close();

    })
  }

  previewAction(action) {
    this.scriptComponent.viewAction(action);
  }
}
