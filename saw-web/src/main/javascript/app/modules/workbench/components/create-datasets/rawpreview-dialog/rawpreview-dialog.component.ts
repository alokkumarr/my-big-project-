declare function require(string): string;

import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import * as get from 'lodash/get';
import * as take from 'lodash/take';

const template = require('./rawpreview-dialog.component.html');

@Component({
  selector: 'rawpreview-dialog',
  template
})

export class RawpreviewDialogComponent implements OnInit {
  private title = '';
  private message = 'No Data';
  
  constructor(
    private dialogRef: MatDialogRef<RawpreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private params: any
  ) {
    if (get(params, 'rawData')) {
      this.message = take(params.rawData, 50);
    }

    if (get(params, 'title')) {
      this.title = params.title;
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