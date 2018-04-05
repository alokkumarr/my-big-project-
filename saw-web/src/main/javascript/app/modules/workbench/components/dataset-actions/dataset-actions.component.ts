
import { Component, OnInit, Input } from '@angular/core';
import { MatDialog } from '@angular/material';

import { SqlExecutorComponent } from '../sql-executor/sql-executor.component';

const template = require('./dataset-actions.component.html');
require('./dataset-actions.component.scss');
@Component({
  selector: 'dataset-actions',
  template,
  styles: []
})

export class DatasetActionsComponent implements OnInit {
  @Input() id: any;

  constructor(
    public dialog: MatDialog
  ) { }

  ngOnInit() { }

  openSQLEditor(): void {
    this.dialog.open(SqlExecutorComponent, {
      panelClass: 'full-screen-dialog',
      autoFocus: false,
      data: {
        id: this.id
      }
    });
  }
}
