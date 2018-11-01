import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { UserAssignmentService } from './../userassignment.service';
import * as get from 'lodash/get';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './add-attribute-dialog.component.html',
  styleUrls: ['./add-attribute-dialog.component.scss']
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

  hasWhiteSpace(field) {
    return /\s/g.test(field);
  }

  submit() {
    if (this.hasWhiteSpace(this.data.attributeName)) {
      this.errorState = true;
      this.errorMessage = 'Field Name cannot contain spaces';
      return false;
    }
    this._userAssignmentService.attributetoGroup(this.data).then(response => {
      if (get(response, 'valid')) {
        this.errorState = false;
        this._dialogRef.close(get(response, 'valid'));
      }
    }).catch(err => {
      if (!get(err.error, 'valid')) {
        this.errorState = !get(err.error, 'valid');
        this.errorMessage = get(err.error, 'validityMessage');
      }
    });
  }
}
