declare function require(string): string;

import { Component, Inject, ViewChild, OnInit, Input, AfterViewInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { ToastService } from '../../../../common/services/toastMessage.service'

import * as get from 'lodash/get';
import { SqlScriptComponent } from './query/sql-script.component';
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
  private userProject: string = 'project2';
  private datasetID: string = '';

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
    this.workBench.getDatasetDetails(this.userProject, this.datasetID).subscribe(data => {
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
}
