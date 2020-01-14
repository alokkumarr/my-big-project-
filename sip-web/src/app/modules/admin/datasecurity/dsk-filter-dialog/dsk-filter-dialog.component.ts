import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DataSecurityService } from './../datasecurity.service';
import * as get from 'lodash/get';
import * as debounce from 'lodash/debounce';
import * as cloneDeep from 'lodash/cloneDeep';
import { DSKFilterGroup } from '../dsk-filter.model';
import { defaultFilters } from '../dsk-filter-group/dsk-filter-group.component';

@Component({
  selector: 'add-attribute-dialog',
  templateUrl: './dsk-filter-dialog.component.html',
  styleUrls: ['./dsk-filter-dialog.component.scss']
})
export class DskFilterDialogComponent implements OnInit {
  public attribute = {};
  dskFilterObject: DSKFilterGroup;
  errorState = true;
  operation: 'Update' | 'Add' = 'Add';
  previewString = '';
  errorMessage;
  debouncedValidator = debounce(this.validateFilterGroup.bind(this), 200);
  constructor(
    private _dialogRef: MatDialogRef<DskFilterDialogComponent>,
    private datasecurityService: DataSecurityService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      groupSelected;
      filterGroup: DSKFilterGroup;
    }
  ) {
    this.datasecurityService.clearDSKEligibleFields();
    this.operation = this.data.filterGroup ? 'Update' : 'Add';
    this.dskFilterObject = this.data.filterGroup || cloneDeep(defaultFilters);
  }

  ngOnInit() {}

  validateFilterGroup() {
    this.errorState = !this.datasecurityService.isDSKFilterValid(
      this.dskFilterObject,
      true
    );

    if (this.errorState) {
      this.previewString = '';
    } else {
      this.previewString = this.datasecurityService.generatePreview(
        this.dskFilterObject
      );
    }
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
