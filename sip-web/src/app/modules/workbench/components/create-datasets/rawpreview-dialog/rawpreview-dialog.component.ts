import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import * as get from 'lodash/get';
import * as take from 'lodash/take';

@Component({
  selector: 'rawpreview-dialog',
  templateUrl: 'rawpreview-dialog.component.html'
})
export class RawpreviewDialogComponent implements OnInit {
  public title = ''; // tslint:disable-line
  public message = 'No Data'; // tslint:disable-line

  constructor(
    public dialogRef: MatDialogRef<RawpreviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public params: any // tslint:disable-line
  ) {
    if (get(params, 'rawData')) {
      this.message = take(params.rawData, 50);
    }

    if (get(params, 'title')) {
      this.title = params.title;
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
