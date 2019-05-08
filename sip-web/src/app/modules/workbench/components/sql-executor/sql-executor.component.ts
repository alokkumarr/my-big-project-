import { Component, ViewChild, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';
import { ToastService } from '../../../../common/services/toastMessage.service';

import * as endsWith from 'lodash/endsWith';

import { SQL_AQCTIONS } from '../../sample-data';

import { SqlScriptComponent } from './query/sql-script.component';
import { DetailsDialogComponent } from './dataset-details-dialog/details-dialog.component';
import { WorkbenchService } from '../../services/workbench.service';

@Component({
  selector: 'sql-executor',
  templateUrl: './sql-executor.component.html',
  styleUrls: ['./sql-executor.component.scss']
})
export class SqlExecutorComponent implements OnInit, OnDestroy {
  public artifacts = [];
  public gridConfig: Array<any>;
  public gridData = new BehaviorSubject([]);
  public dsMetadata: any;
  public datasetDetails: Array<any>;
  public appliedActions: Array<any> = SQL_AQCTIONS;
  public scriptHeight = 100;
  public previewHeight = 0;
  public query = '';

  constructor(
    public router: Router,
    public dialog: MatDialog,
    public workBench: WorkbenchService,
    public notify: ToastService
  ) {}

  @ViewChild('sqlscript') public scriptComponent: SqlScriptComponent;

  ngOnInit() {
    this.getPageData();
  }

  ngOnDestroy() {
    this.workBench.removeDataFromLS('dsMetadata');
  }

  getPageData(): void {
    this.dsMetadata = this.workBench.getDataFromLS('dsMetadata');
    this.constructArtifactForEditor();
    // this.workBench.getDatasetDetails('this.datasetID').subscribe(data => {
    //   this.artifacts = data.artifacts;
    // });
  }

  constructArtifactForEditor() {
    const table = {
      artifactName: this.dsMetadata.system.name,
      columns: this.dsMetadata.schema.fields
    };
    this.artifacts.push(table);
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

    detailsDialogRef.afterClosed().subscribe(data => {
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

  /**
   * Constructs the payload for SQL executor component.
   *
   * @param {any} data
   * @memberof SqlExecutorComponent
   */
  triggerSQL(data) {
    /**
     * Temporary workaround to construct user friendly SQL script.
     * Will be handled in BE in upcoming release
     */
    const appendedScript = `CREATE TABLE ${data.name} AS ${this.query}`;
    const script =
      endsWith(appendedScript, ';') === true
        ? `${appendedScript}`
        : `${appendedScript};`;
    const payload = {
      name: data.name,
      input: this.dsMetadata.system.name,
      component: 'sql',
      configuration: {
        script: script
      }
    };
    this.workBench.triggerParser(payload).subscribe(_ => {
      this.notify.info(
        'SQL_Executor_triggered_successfully',
        'Creating Dataset',
        { hideDelay: 9000 }
      );
    });
    this.router.navigate(['workbench', 'dataobjects']);
  }

  previewAction(action) {
    this.scriptComponent.viewAction(action);
  }

  backToDS() {
    this.router.navigate(['workbench', 'dataobjects']);
  }
}
