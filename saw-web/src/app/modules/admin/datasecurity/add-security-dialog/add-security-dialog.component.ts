import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';
import * as get from 'lodash/get';

require('./add-security-dialog.component.scss');

@Component({
  selector: 'add-secuirty-dialog',
  template: './add-security-dialog.component.html'
})
export class AddSecurityDialogComponent {
  public securityGroup = {};
  public errorState: boolean;
  public errorMessage: string;

  constructor(
    private _dialogRef: MatDialogRef<AddSecurityDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}

  submit() {
    this._userAssignmentService.addSecurityGroup(this.data).then(response => {
      if (get(response, 'valid')) {
        this._dialogRef.close(get(response, 'valid'));
      } else {
        this.errorState = !get(response, 'valid');
        this.errorMessage = get(response, 'validityMessage');
      }
    });
  }
}
