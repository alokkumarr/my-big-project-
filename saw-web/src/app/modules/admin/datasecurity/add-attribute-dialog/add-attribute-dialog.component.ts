import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';
import * as get from 'lodash/get';

require('./add-attribute-dialog.component.scss');

@Component({
  selector: 'add-attribute-dialog',
  template: './add-attribute-dialog.component.html'
})
export class AddAttributeDialogComponent {
  public attribute = {};
  errorState: boolean;
  errorMessage;
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create',
      attributeName,
      value,
      groupSelected
    }
  ) {}

  submit() {
    const request = {
      attributeName: this.data.attributeName.trim(),
      value: this.data.value,
      securityGroupName: this.data.groupSelected
    };
    this._userAssignmentService.addAttributetoGroup(request, this.data.mode).then(response => {
      if (get(response, 'valid')) {
        this._dialogRef.close(get(response, 'valid'));
      } else {
        this.errorState = !get(response, 'valid');
        this.errorMessage = get(response, 'validityMessage');
      }
    });
  }
}
