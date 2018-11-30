import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'confirm-action-dialog',
  templateUrl: './confirm-action-dialog.component.html'
})
export class ConfirmActionDialogComponent implements OnInit {
  constructor(private dialogRef: MatDialogRef<ConfirmActionDialogComponent>) {}

  ngOnInit() {}

  onNoClick(): void {
    this.dialogRef.close(false);
  }

  onYesClick(): void {
    this.dialogRef.close(true);
  }
}
