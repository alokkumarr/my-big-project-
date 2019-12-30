import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DataSecurityService } from './../datasecurity.service';
import * as get from 'lodash/get';
import { DSKFilterGroup } from '../dsk-filter.model';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './add-attribute-dialog.component.html',
  styleUrls: ['./add-attribute-dialog.component.scss']
})
export class AddAttributeDialogComponent {
  public attribute = {};
  dskFilters$: Observable<DSKFilterGroup>;
  dskFilterObject: DSKFilterGroup;
  errorState: boolean;
  errorMessage;
  constructor(
    private _dialogRef: MatDialogRef<AddAttributeDialogComponent>,
    private datasecurityService: DataSecurityService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      mode: 'edit' | 'create';
      attributeName;
      value;
      groupSelected;
    }
  ) {
    this.dskFilters$ = this.datasecurityService
      .getFiltersFor(data.groupSelected.secGroupSysId)
      .pipe(
        tap(filters => {
          this.dskFilterObject = filters;
        })
      );
  }

  hasWhiteSpace(field) {
    return /\s/g.test(field);
  }

  updateFilter(filter: DSKFilterGroup) {
    this.dskFilterObject = filter;
    console.log(this.dskFilterObject);
  }

  submit() {
    if (this.hasWhiteSpace(this.data.attributeName)) {
      this.errorState = true;
      this.errorMessage = 'Field Name cannot contain spaces';
      return false;
    }
    this.datasecurityService
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
