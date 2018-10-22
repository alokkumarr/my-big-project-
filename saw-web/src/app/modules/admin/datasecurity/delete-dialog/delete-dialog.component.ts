import { Component, Inject } from '@angular/core';
import { UserAssignmentService } from './../userassignment.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

const template = require('./delete-dialog.component.html');
//require('./security-group.component.scss');

@Component({
  selector: 'delete-dialog',
  template
})
export class DeleteDialogComponent {

  constructor(
    private _dialogRef: MatDialogRef<DeleteDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}
}
