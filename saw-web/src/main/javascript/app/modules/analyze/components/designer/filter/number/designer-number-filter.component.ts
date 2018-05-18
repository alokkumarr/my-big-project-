import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FilterModel } from '../../types';
import * as isFinite from 'lodash/isFinite';

const template = require('./designer-number-filter.component.html');
require('./designer-number-filter.component.scss');

export const OPERATORS = [
  {
    value: 'GT',
    label: 'Greater than'
  },
  {
    value: 'LT',
    label: 'Less than'
  },
  {
    value: 'GTE',
    label: 'Greater than or equal to'
  },
  {
    value: 'LTE',
    label: 'Less than or equal to'
  },
  {
    value: 'EQ',
    label: 'Equal to'
  },
  {
    value: 'NEQ',
    label: 'Not equal to'
  },
  {
    value: 'BTW',
    label: 'Between'
  }
];

@Component({
  selector: 'designer-number-filter',
  template
})
export class DesignerNumberFilterComponent {
  @Output()
  public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public OPERATORS = OPERATORS;

  init() {
    if (!this.filterModel) {
      this.filterModel = {
        operator: 'EQ'
      };
    }
  }

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    this.init();
  }

  onFilterModelChange() {
    this.filterModelChange.emit(this.filterModel);
  }

  onValueChange(value) {
    const parsed = parseFloat(value);
    if (isFinite(parsed)) {
      this.filterModel.value = parsed;
      this.onFilterModelChange();
    }
  }

  onOtherValueChange(value) {
    const parsed = parseFloat(value);
    if (isFinite(parsed)) {
      this.filterModel.otherValue = parsed;
      this.onFilterModelChange();
    }
  }

  onOperatorChange(operator) {
    this.filterModel.operator = operator;
    if (this.filterModel.operator !== 'BTW') {
      this.filterModel.otherValue === null;
    }
    this.onFilterModelChange();
  }
}
