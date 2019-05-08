import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import * as get from 'lodash/get';

@Component({
  selector: 'confirm-dialog',
  templateUrl: 'confirm-dialog.component.html'
})
export class ConfirmDialogComponent implements OnInit {
  public title = ''; // tslint:disable-line
  public message = 'Are you sure you want to proceed'; // tslint:disable-line
  public actionButton = 'Proceed'; // tslint:disable-line
  public actionColor = 'primary'; // tslint:disable-line

  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public params: any // tslint:disable-line
  ) {
    if (get(params, 'message')) {
      this.message = params.message;
    }

    if (get(params, 'actionButton')) {
      this.actionButton = params.actionButton;
    }

    if (get(params, 'actionColor')) {
      this.actionColor = params.actionColor;
    }
  }

  ngOnInit() {}

  closeDashboard(confirm = false) {
    this.dialogRef.close(confirm);
  }

  confirm() {
    this.closeDashboard(true);
  }
}
