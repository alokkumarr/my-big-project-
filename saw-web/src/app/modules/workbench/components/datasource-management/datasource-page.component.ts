import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { MatSnackBar } from '@angular/material';

import { sourceTypes } from '../../wb-comp-configs';

import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { CreateSourceDialogComponent } from './createSource-dialog/createSource-dialog.component';
import { CreateRouteDialogComponent } from './create-route-dialog/create-route-dialog.component';
import { TestConnectivityComponent } from './test-connectivity/test-connectivity.component';

import { SAMPLE_SOURCE_DATA } from '../../sample-data';

@Component({
  selector: 'datasource-page',
  templateUrl: './datasource-page.component.html',
  styleUrls: ['./datasource-page.component.scss']
})
export class DatasourceComponent implements OnInit, OnDestroy {
  sourceData: any = SAMPLE_SOURCE_DATA;
  sources = sourceTypes;
  selectedSourceType: string = 'sftp';
  selectedSourceData: any;

  constructor(
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private _toastMessage: ToastService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {}

  ngOnDestroy() {}

  sourceSelectedType(source) {
    this.selectedSourceType = source;
  }

  createSource() {
    const dateDialogRef = this.dialog.open(CreateSourceDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '60%',
      width: '70%',
      minWidth: '600px',
      minHeight: '600px',
      maxWidth: '900px',
      panelClass: 'sourceDialogClass'
    });

    dateDialogRef.afterClosed().subscribe(data => {});
  }

  testConnection() {
    this.snackBar.openFromComponent(TestConnectivityComponent, {
      horizontalPosition: 'center',
      panelClass: ['mat-elevation-z9', 'testConnectivityClass']
    });
  }

  onSourceSelectionChanged(event) {
    this.selectedSourceData = event.selectedRowsData[0];
  }

  defaultSelectHandler(e) {
    // Selects the first visible row
    e.component.selectRowsByIndexes([0]);
  }

  onToolbarPreparing(e) {
    e.toolbarOptions.items.unshift({
      location: 'before',
      template: 'sourceTypeTemplate'
    });
  }

  onRoutesToolbarPreparing(e) {
    e.toolbarOptions.items.unshift({
      location: 'before',
      template: 'nameTemplate'
    });
  }

  editSource() {}

  createRoute() {
    const dateDialogRef = this.dialog.open(CreateRouteDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '60%',
      width: '70%',
      minWidth: '600px',
      minHeight: '600px',
      maxWidth: '900px',
      panelClass: 'sourceDialogClass'
    });

    dateDialogRef.afterClosed().subscribe(data => {});
  }
}
