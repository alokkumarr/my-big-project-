import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DskFiltersService } from './../services/dsk-filters.service';

import * as get from 'lodash/get';
import * as debounce from 'lodash/debounce';
import * as cloneDeep from 'lodash/cloneDeep';
import * as forEach from 'lodash/forEach';
import * as isArray from 'lodash/isArray';
import * as isEmpty from 'lodash/isEmpty';

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
  filterQuery;
  debouncedValidator = debounce(this.validateFilterGroup.bind(this), 200);
  constructor(
    private _dialogRef: MatDialogRef<DskFilterDialogComponent>,
    private datasecurityService: DskFiltersService,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      groupSelected;
      filterGroup: DSKFilterGroup;
      mode;
      filters;
    }
  ) {
    console.log(this.data);
    this.datasecurityService.clearDSKEligibleFields();
    this.operation = this.data.filterGroup ? 'Update' : 'Add';
    this.dskFilterObject = this.data.mode === 'ANALYZE'
      ? this.getFilterArray(this.data.filters) : this.data.filterGroup || cloneDeep(defaultFilters);
  }

  ngOnInit() {}

  getFilterArray(filters) {
    if (isEmpty(filters)) {
      return cloneDeep(defaultFilters);
    }

    const filter = filters[0];
    if (filter.filters) {
      filter.booleanQuery = filter.filters;
      delete filter.filters;
    }
    forEach(filter.booleanQuery, obj => {
      if (obj.filters) {
        obj.booleanQuery = obj.filters;
        delete obj.filters;
      }
      if (isArray(obj)) {
        this.changeIndexToFilters(obj);
      }
    })
    console.log(filter);
    return filter;
  }

  validateFilterGroup() {
    this.errorState = !this.datasecurityService.isDSKFilterValid(
      this.dskFilterObject,
      true
    );

    if (this.data.mode === 'ANALYZE') {
      return;
    }

    if (this.errorState) {
      this.previewString = '';
    } else {
      this.previewString = this.datasecurityService.generatePreview(
        this.dskFilterObject, this.data.mode
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

  changeIndexToFilters(dskObject) {
    if (dskObject.booleanQuery) {
      dskObject.filters = dskObject.booleanQuery;
      delete dskObject.booleanQuery;
    }
    forEach(dskObject.booleanQuery, obj => {
      if (obj.booleanQuery) {
        obj.filters = obj.booleanQuery;
        delete obj.booleanQuery;
      }
      if (isArray(obj)) {
        this.changeIndexToFilters(obj);
      }
    })
  }

  submit() {
    if (this.data.mode === 'ANALYZE') {
      this.filterQuery = cloneDeep(this.dskFilterObject);
      this.changeIndexToFilters(this.filterQuery);
      this._dialogRef.close([this.filterQuery]);
    } else {
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
}
