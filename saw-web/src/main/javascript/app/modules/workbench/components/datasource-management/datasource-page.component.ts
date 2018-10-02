import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material';

import { WorkbenchService } from '../../services/workbench.service';
import { ToastService } from '../../../../common/services/toastMessage.service';
import { CreateSourceDialogComponent } from './createSource-dialog/createSource-dialog.component';

const template = require('./datasource-page.component.html');
require('./datasource-page.component.scss');

@Component({
  selector: 'datasource-page',
  template,
  styles: []
})
export class DatasourceComponent implements OnInit, OnDestroy {
  constructor(
    public dialog: MatDialog,
    private workBench: WorkbenchService,
    private _toastMessage: ToastService
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
}
