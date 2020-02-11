import { Component, OnInit, Output, EventEmitter, Input } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';

import * as isEmpty from 'lodash/isEmpty';

@Component({
  selector: 'dataset-string-filter',
  templateUrl: './dataset-string-filter.component.html',
  styleUrls: ['./dataset-string-filter.component.scss']
})
export class DatasetStringFilterComponent implements OnInit {
  public filterFormGroup: FormGroup;
  public filterList;
  @Output() typeFilterChange = new EventEmitter<any>();

  @Input('resetFilters') set resetFilter(data) {
    if (data) {
      this.filterFormGroup.patchValue({
        value: ''
      });
    }
  }

  @Input('typeFilterList') set setFilterList(data) {
    if (!isEmpty(data)) {
      this.filterList = data;
    }
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
        this.typeFilterChange.emit({ name: 'dsStringFilter', data: value });
      }
    });
  }
}
