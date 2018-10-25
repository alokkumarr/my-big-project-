import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';
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
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create',
      securityGroupName,
      description
    }
  ) {}

  submit() {
    console.log(this.data);
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
