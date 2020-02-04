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
  @Output() filterChange = new EventEmitter<any>();
  @Output() filterRemoved = new EventEmitter<any>();

  @Input('resetFilters') set resetFilter(data) {
    if (data) {
      this.filterFormGroup.patchValue({
        value: ''
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

  constructor(private fb: FormBuilder) {
    this.createFilterForm();
  }
  ngOnInit() {}

  createFilterForm() {
    this.filterFormGroup = this.fb.group({
      value: ['']
    });

    this.filterFormGroup.valueChanges.subscribe(({ value }) => {
      if (this.filterFormGroup.valid && !isEmpty(value)) {
        this.filterChange.emit({ data: value });
      }
    });
  }

  /*
  Use this function to remove/reset individual filter.
  removeFilter() {
    this.filterFormGroup.patchValue({
      value: ''
    });
    this.filterRemoved.emit({ filterType: this.dropDownLabel.toLowerCase() });
  } */
}
