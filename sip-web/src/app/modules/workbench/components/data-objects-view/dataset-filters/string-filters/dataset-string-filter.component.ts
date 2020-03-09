import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import * as isEmpty from 'lodash/isEmpty';
import * as startCase from 'lodash/startCase';

@Component({
  selector: 'dataset-string-filter',
  templateUrl: './dataset-string-filter.component.html',
  styleUrls: ['./dataset-string-filter.component.scss']
})
export class DatasetStringFilterComponent implements OnInit {
  public filterFormGroup: FormGroup;
  public filterList;
  public filterLabel;
  public dropDownLabel;
  public isMultiSelect;
  @Output() filterChange = new EventEmitter<any>();
  @Output() filterRemoved = new EventEmitter<any>();
  @Output() emptyFilter = new EventEmitter<any>();

  @Input('resetFilters') set resetFilter(data) {
    if (data) {
      this.filterFormGroup.patchValue({
        filter: ''
      });
    }
  }

  @Input('filterList') set setFilterList(data) {
    if (!isEmpty(data)) {
      this.filterList = data;
    }
  }

  @Input('filterLabel') set setFilterLabel(data) {
    this.filterLabel = data;
  }

  @Input('label') set setLabel(data) {
    this.dropDownLabel = startCase(data) || 'Select a value';
  }

  @Input('isMultiSelect') set setMultiSelect(data) {
    this.isMultiSelect = data;
  }
  constructor(private fb: FormBuilder) {
    this.createFilterForm();
  }
  ngOnInit() {}

  createFilterForm() {
    this.filterFormGroup = this.fb.group({
      filter: ['']
    });

    this.filterFormGroup.valueChanges.subscribe(({ filter }) => {
      if (!isEmpty(filter)) {
        this.filterChange.emit({
          data: filter,
          filterType: this.dropDownLabel.toLowerCase()
        });
      } else {
        this.dropDownLabel
          ? this.filterRemoved.emit({
              name: 'removeFilter',
              filterType: this.dropDownLabel.toLowerCase()
            })
          : ''; // Do Nothing in else
      }
    });
  }

  /* // Use this function to remove/reset individual filter.
  removeFilter() {
    this.filterFormGroup.patchValue({
      filter: ''
    });
    this.filterRemoved.emit({
      name: 'resetFilter',
      filterType: this.dropDownLabel.toLowerCase()
    });
  } */
}
