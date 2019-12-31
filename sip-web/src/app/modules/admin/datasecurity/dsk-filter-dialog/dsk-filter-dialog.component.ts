import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DataSecurityService } from './../datasecurity.service';
import * as get from 'lodash/get';
import * as debounce from 'lodash/debounce';
import { DSKFilterGroup } from '../dsk-filter.model';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './dsk-filter-dialog.component.html',
  styleUrls: ['./dsk-filter-dialog.component.scss']
})
export class DskFilterDialogComponent {
  public attribute = {};
  dskFilters$: Observable<DSKFilterGroup>;
  dskFilterObject: DSKFilterGroup;
  errorState = true;
  errorMessage;
  debouncedValidator = debounce(this.validateFilterGroup.bind(this), 200);
  constructor(
    private _dialogRef: MatDialogRef<DskFilterDialogComponent>,
    private datasecurityService: DataSecurityService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
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

  validateFilterGroup() {
    this.errorState = !this.datasecurityService.isDSKFilterValid(
      this.dskFilterObject
    );
  }

  hasWhiteSpace(field) {
    return /\s/g.test(field);
  }

  updateFilter(filter: DSKFilterGroup) {
    this.dskFilterObject = filter;
    this.debouncedValidator();
  }

  submit() {
    this.datasecurityService
      .updateDskFiltersForGroup(
        this.data.groupSelected.secGroupSysId,
        this.dskFilterObject
      )
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
