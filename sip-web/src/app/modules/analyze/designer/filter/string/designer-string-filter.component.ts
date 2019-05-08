import {
  Component,
  OnInit,
  OnChanges,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import { MatChipInputEvent } from '@angular/material';
import { ENTER, COMMA } from '@angular/cdk/keycodes';
import * as filter from 'lodash/filter';
import * as compact from 'lodash/compact';
import { FilterModel } from '../../types';
import { STRING_FILTER_OPERATORS } from '../../../consts';

const SEMICOLON = 186;

export const isValid = (model: FilterModel = {}) => {
  return (
    model.operator &&
    model.modelValues &&
    filter(model.modelValues, v => v).length > 0
  );
};

@Component({
  selector: 'designer-string-filter',
  templateUrl: 'designer-string-filter.component.html'
})
export class DesignerStringFilterComponent implements OnInit, OnChanges {
  @Output()
  public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public separatorKeysCodes = [ENTER, COMMA, SEMICOLON];
  public OPERATORS = STRING_FILTER_OPERATORS;
  public tempValue = '';

  init() {
    if (!this.filterModel) {
      this.filterModel = {
        modelValues: [],
        operator: ''
      };
      this.tempValue = '';
    } else {
      this.tempValue = this.filterModel.modelValues[0] || '';
    }
  }

  ngOnInit() {
    this.init();
  }

  ngOnChanges() {
    this.init();
  }

  onFilterModelChange() {
    this.filterModel.modelValues = compact(this.filterModel.modelValues);
    this.filterModelChange.emit(this.filterModel);
  }

  add(event: MatChipInputEvent) {
    const input = event.input;
    const value = event.value;
    if (value) {
      this.filterModel.modelValues = [
        ...this.filterModel.modelValues,
        value.trim()
      ];
      this.onFilterModelChange();
    }
    if (input) {
      input.value = '';
    }
  }

  remove(index) {
    this.filterModel.modelValues = filter(
      this.filterModel.modelValues,
      (_, i) => i !== index
    );
    this.onFilterModelChange();
  }

  onPresetSelected(value) {
    this.tempValue = '';
    this.filterModel.modelValues = [];
    this.filterModel.operator = value;
    this.onFilterModelChange();
  }

  onValueChange(value) {
    this.filterModel.modelValues = [];
    this.filterModel.modelValues.push(value);
    this.onFilterModelChange();
  }
}
