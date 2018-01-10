import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import * as get from 'lodash/get';

const template = require('./confirm-dialog.component.html');

@Component({
  selector: 'confirm-dialog',
  template
})

export class ConfirmDialogComponent implements OnInit {
  private title = '';
  private message = 'Are you sure you want to proceed';
  private actionButton = 'Proceed';
  private actionColor = 'primary';

  constructor(
    private dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private params: any
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

  ngOnInit() { }

  closeDashboard(confirm = false) {
    this.dialogRef.close(confirm);
  }

  confirm() {
    this.closeDashboard(true);
  }
}
