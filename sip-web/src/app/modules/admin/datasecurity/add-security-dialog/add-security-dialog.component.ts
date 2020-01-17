import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DataSecurityService } from './../datasecurity.service';
import * as get from 'lodash/get';

@Component({
  selector: 'add-secuirty-dialog',
  templateUrl: './add-security-dialog.component.html',
  styleUrls: ['./add-security-dialog.component.scss']
})
export class AddSecurityDialogComponent {
  public securityGroup = {};
  public errorState: boolean;
  public errorMessage: string;

  constructor(
    private _dialogRef: MatDialogRef<AddSecurityDialogComponent>,
    private _userAssignmentService: DataSecurityService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      mode: 'edit' | 'create';
      securityGroupName;
      description;
    }
  ) {}

  submit() {
    this._userAssignmentService
      .addSecurityGroup(this.data)
      .then(response => {
        if (get(response, 'valid')) {
          this._dialogRef.close(response);
        }
      })
      .catch(err => {
        if (!get(err.error, 'valid')) {
          this.errorState = !get(err.error, 'valid');
          this.errorMessage = get(err.error, 'validityMessage');
        }
      });
  }
}
