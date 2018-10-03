import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnInit,
  OnChanges
} from '@angular/core';
import { FilterModel } from '../../types';
import {
  BETWEEN_NUMBER_FILTER_OPERATOR,
  NUMBER_FILTER_OPERATORS
} from '../../../consts';

import * as isFinite from 'lodash/isFinite';
import * as unset from 'lodash/unset';

export const isValid = (model: FilterModel) => {
  model = model || {};

  return (
    model.operator &&
    isFinite(model.value) &&
    (model.operator !== BETWEEN_NUMBER_FILTER_OPERATOR.value
      ? true
      : isFinite(model.otherValue))
  );
};

@Component({
  selector: 'designer-number-filter',
  templateUrl: './designer-number-filter.component.html',
  styleUrls: ['./designer-number-filter.component.scss']
})
export class DesignerNumberFilterComponent implements OnInit, OnChanges {
  @Output()
  public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public OPERATORS = NUMBER_FILTER_OPERATORS;
  public BETWEEN_NUMBER_FILTER_OPERATOR = BETWEEN_NUMBER_FILTER_OPERATOR;

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
    if (value === '') {
      unset(this.filterModel, 'value');
      this.onFilterModelChange();
    }
    const parsed = parseFloat(value);
    if (isFinite(parsed)) {
      this.filterModel.value = parsed;
      this.onFilterModelChange();
    }
  }

  onOtherValueChange(value) {
    if (value === '') {
      unset(this.filterModel, 'otherValue');
      this.onFilterModelChange();
    }
    const parsed = parseFloat(value);
    if (isFinite(parsed)) {
      this.filterModel.otherValue = parsed;
      this.onFilterModelChange();
    }
  }

  onOperatorChange(operator) {
    this.filterModel.operator = operator;
    if (this.filterModel.operator !== BETWEEN_NUMBER_FILTER_OPERATOR.value) {
      this.filterModel.otherValue === null;
    }
    this.onFilterModelChange();
  }
}
