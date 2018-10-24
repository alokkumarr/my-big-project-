import { Component, Inject } from '@angular/core';
import { UserAssignmentService } from './../userassignment.service';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

@Component({
  selector: 'delete-dialog',
  templateUrl: './delete-dialog.component.html'
})
export class DeleteDialogComponent {

  constructor(
    private _dialogRef: MatDialogRef<DeleteDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data
  ) {}
}
