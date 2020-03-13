import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { DskFiltersService } from './../services/dsk-filters.service';

import * as get from 'lodash/get';
import * as debounce from 'lodash/debounce';
import * as cloneDeep from 'lodash/cloneDeep';
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
      booleanCriteria;
    }
  ) {
    this.datasecurityService.clearDSKEligibleFields();
    this.operation = this.data.filterGroup ? 'Update' : 'Add';
    this.dskFilterObject = this.fetch(this.data, this.data.mode);
  }

  ngOnInit() {}

  fetch(data, mode) {
    switch (mode) {
      case 'ANALYZE':
        if (isEmpty(data.filters)) {
          return cloneDeep(defaultFilters);
        } else {
          console.log(this.data);
          if (this.data.filters[0].filters) {
            return this.changeIndexToNames2(this.data.filters, 'fiters', 'booleanQuery');
          } else {
            const oldFormatFilters = cloneDeep(this.data.filters);
            this.data.filters = [];
            this.data.filters.push({
              booleanCriteria: this.data.booleanCriteria,
              filters: oldFormatFilters
            })
            return this.changeIndexToNames2(this.data.filters, 'fiters', 'booleanQuery');
          }
        }
      case 'DSK':
        return data.filterGroup || cloneDeep(defaultFilters);
    }
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

  //Need to para'se the replace funtion;

  changeIndexToNames(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"booleanQuery":/g, '"filters":');
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson;
  }

  changeIndexToNames2(dskObject, source, target) {
    const convertToString = JSON.stringify(dskObject);
    const replaceIndex = convertToString.replace(/"filters":/g, '"booleanQuery":');
    const convertToJson = JSON.parse(replaceIndex);
    return convertToJson[0];
  }

  submit() {
    if (this.data.mode === 'ANALYZE') {
      this.filterQuery = cloneDeep(this.dskFilterObject);
      const result = this.changeIndexToNames(this.filterQuery, 'booleanQuery', 'filters' );
      this._dialogRef.close([result]);
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
