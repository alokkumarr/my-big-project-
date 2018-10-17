import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';

const template = require('./add-attribute-dialog.component.html');
require('./add-attribute-dialog.component.scss');

@Component({
  selector: 'add-attribute-dialog',
  template
})
export class AddAttributeDialogComponent {
  public attribute = {};
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    @Inject(MAT_DIALOG_DATA) public data: {
      mode: 'edit' | 'create'
    }
  ) {}

  submit() {
    console.log(this.data);
    console.log(this.attribute);
    const request = {
      attributeName: this.data.attributeName,
      value: this.data.value,
      securityGroupName: this.data.groupSelected
    }
    this._userAssignmentService.addAttributetoGroup(request, this.data.mode).then(response => {
      console.log(response);
      if (response.valid) {
        this._dialogRef.close(response.valid);
      } else {
        this.errorState = !response.valid;
        this.errorMessage = response.validityMessage;
    });
  }
}
