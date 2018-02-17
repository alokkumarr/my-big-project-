declare function require(string): string;

import { Component, Inject, ViewChild, OnInit, Input, AfterViewInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { DxDataGridComponent } from 'devextreme-angular';
import { dxDataGridService } from '../../../../common/services/dxDataGrid.service';
import { ToastService } from '../../../../common/services/toastMessage.service'

import * as get from 'lodash/get';
import { WorkbenchService } from '../../services/workbench.service';

const template = require('./sql-executor.component.html');
require('./sql-executor.component.scss');

@Component({
  selector: 'sql-executor',
  template
})
export class SqlExecutorComponent implements OnInit, AfterViewInit {
  private myHeight: Number;
  private artifacts: any;
  private gridConfig: Array<any>;
  private showProgress: boolean = false;
  private userProject: string = 'project2';
  private datasetID: string = '';

  constructor(
    public dialogRef: MatDialogRef<SqlExecutorComponent>,
    public dialog: MatDialog,
    private dxDataGrid: dxDataGridService,
    private workBench: WorkbenchService,
    private notify: ToastService,
    @Inject(MAT_DIALOG_DATA) private data: any
  ) {
    if (get(data, 'id')) {
      this.datasetID = data.id;
    }
  }
  @ViewChild(DxDataGridComponent) dataGrid: DxDataGridComponent;

  ngOnInit() {
    this.getPageData();
    this.gridConfig = this.getGridConfig();
    this.myHeight = window.screen.availHeight - 340;
  }

  ngAfterViewInit() {
    this.dataGrid.instance.option(this.gridConfig);
  }

  getPageData(): void {
    this.showProgress = true;
    this.workBench.getDatasetDetails(this.userProject, this.datasetID).subscribe(data => {
      this.showProgress = false;
      this.artifacts = data.artifacts;
      this.reloadDataGrid(data.artifacts[0].columns);
    });
  }

  getGridConfig() {
    const dataSource = [];
    const columns = [{
      caption: 'Field Name',
      dataField: 'name',
      allowSorting: true,
      alignment: 'left',
      width: '70%',
      dataType: 'string'
    }, {
      caption: 'Type',
      dataField: 'type',
      dataType: 'string'
    }];

    return this.dxDataGrid.mergeWithDefaultConfig({
      dataSource,
      columns,
      columnAutoWidth: false,
      wordWrapEnabled: false,
      searchPanel: {
        visible: true,
        width: '250px',
        placeholder: 'Search...'
      },
      height: '100%',
      width: '100%',
      filterRow: {
        visible: true,
        applyFilter: 'auto'
      },
      headerFilter: {
        visible: false
      },
      sorting: {
        mode: 'none'
      },
      scrolling: {
        showScrollbar: 'always',
        mode: 'virtual'
      },
      showRowLines: false,
      showBorders: false,
      rowAlternationEnabled: false,
      showColumnLines: true,
      selection: {
        mode: 'none'
      }
    });
  }

  reloadDataGrid(data) {
    this.dataGrid.instance.option('dataSource', data);
    this.dataGrid.instance.refresh();
    this.dataGrid.instance.endCustomLoading();
  }

  onResize(event) {
    this.myHeight = window.screen.availHeight - 340;
  }
}
