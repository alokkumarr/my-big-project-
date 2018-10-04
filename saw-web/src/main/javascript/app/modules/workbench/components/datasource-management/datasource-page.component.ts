import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';
import { MatSnackBar } from '@angular/material';

import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { CreateSourceDialogComponent } from './createSource-dialog/createSource-dialog.component';
import { TestConnectivityComponent } from './test-connectivity/test-connectivity.component';

import { SAMPLE_SOURCE_DATA } from '../../sample-data';
const template = require('./datasource-page.component.html');
require('./datasource-page.component.scss');

@Component({
  selector: 'datasource-page',
  template,
  styles: []
})
export class DatasourceComponent implements OnInit, OnDestroy {
  private gridData: any = SAMPLE_SOURCE_DATA;

  constructor(
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private _toastMessage: ToastService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {}

  ngOnDestroy() {}

  createSource() {
    const dateDialogRef = this.dialog.open(CreateSourceDialogComponent, {
      hasBackdrop: true,
      autoFocus: false,
      closeOnNavigation: true,
      disableClose: true,
      height: '50%',
      width: '70%',
      minWidth: '600px',
      minHeight: '500px',
      maxWidth: '900px',
      maxHeight: '700px',
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

  onToolbarPreparing(e) {
    // e.toolbarOptions.items.unshift({
    //   location: 'before',
    //   template: 'sourceNameTemplate'
    // });
  }
}
