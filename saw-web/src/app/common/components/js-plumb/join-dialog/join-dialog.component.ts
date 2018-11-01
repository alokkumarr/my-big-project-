import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import * as clone from 'lodash/clone';

import { Join, JoinCriterion, JoinChangeEvent } from '../types';

@Component({
  selector: 'join-dialog',
  templateUrl: './join-dialog.component.html',
  styleUrls: ['./join-dialog.component.scss']
})
export class JoinDialogComponent {
  public left: JoinCriterion;
  public right: JoinCriterion;
  public join: Join;

  constructor(
    public _dialogRef: MatDialogRef<JoinDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      join: Join;
    }
  ) {
    this.left = this.data.join.criteria[0];
    this.right = this.data.join.criteria[1];
    this.join = clone(this.data.join);
  }

  onSelect(type) {
    this.join.type = type;
  }

  isActive(type) {
    if (type === this.join.type) {
      return 'active';
    }
    return '';
  }

  deleteJoin() {
    const result: JoinChangeEvent = {
      action: 'delete',
      join: this.data.join
    };
    this._dialogRef.close(result);
  }

  save() {
    const result: JoinChangeEvent = {
      action: 'save',
      join: this.join
    };
    this._dialogRef.close(result);
  }

  cancel() {
    this._dialogRef.close();
  }
}
