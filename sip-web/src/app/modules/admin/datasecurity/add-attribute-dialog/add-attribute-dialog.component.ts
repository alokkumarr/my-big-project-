import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import {
  UserAssignmentService,
  DskEligibleField
} from './../userassignment.service';
import { JwtService } from 'src/app/common/services';

import * as get from 'lodash/get';
import * as toString from 'lodash/toString';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './add-attribute-dialog.component.html',
  styleUrls: ['./add-attribute-dialog.component.scss']
})
export class AddAttributeDialogComponent {
  public attribute = {};
  public dskEligibleFields: Array<DskEligibleField> = [];
  public filteredEligibleFields: Array<DskEligibleField>;
  errorState: boolean;
  errorMessage;
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    private _userAssignmentService: UserAssignmentService,
    private jwtService: JwtService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      mode: 'edit' | 'create';
      attributeName;
      value;
      groupSelected;
    }
  ) {
    this.loadAutocompletions();
  }

  loadAutocompletions() {
    this._userAssignmentService
      .getEligibleDSKFieldsFor(
        this.jwtService.customerId,
        this.jwtService.productId
      )
      .subscribe(fields => {
        this.dskEligibleFields = fields;
        this.filterAutocompleteFields();
      });
  }

  filterAutocompleteFields() {
    const filterValue = toString(this.data.attributeName).toLowerCase();
    this.filteredEligibleFields = this.dskEligibleFields.filter(option =>
      option.displayName.toLowerCase().includes(filterValue)
    );
  }

  hasWhiteSpace(field) {
    return /\s/g.test(field);
  }

  submit() {
    if (this.hasWhiteSpace(this.data.attributeName)) {
      this.errorState = true;
      this.errorMessage = 'Field Name cannot contain spaces';
      return false;
    }
    this._userAssignmentService
      .attributetoGroup(this.data)
      .then(response => {
        if (get(response, 'valid')) {
          this.errorState = false;
          this._dialogRef.close(get(response, 'valid'));
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
