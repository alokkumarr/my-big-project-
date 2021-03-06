import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';

import { Join, JoinChangeEvent } from '../types';
import { JoinDialogComponent } from '../join-dialog';
import * as clone from 'lodash/clone';

@Component({
  selector: 'js-plumb-join-label-u',
  templateUrl: './js-plumb-join-label.component.html',
  styleUrls: ['./js-plumb-join-label.component.scss']
})
export class JsPlumbJoinLabelComponent {
  @Input() join: Join;
  @Output() change: EventEmitter<JoinChangeEvent> = new EventEmitter();
  public _jsPlumbInst: any;

  constructor(public _dialog: MatDialog) {}

  getIcon() {
    return `icon-${this.join.type}-join`;
  }

  getIdentifier() {
    const [source, target] = this.join.criteria;
    return `${source.tableName}:${source.columnName}-${this.join.type}-${target.tableName}:${target.columnName}`;
  }

  openJoinModal() {
    const data = { join: this.join };
    this._dialog
      .open(JoinDialogComponent, {
        width: 'auto',
        height: 'auto',
        data
      } as MatDialogConfig)
      .afterClosed()
      .subscribe((result: JoinChangeEvent) => {
        this.join = clone(result.join);
        this.change.emit(result);
      });
  }
}
