declare const require: any;

import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {MatChipInputEvent} from '@angular/material';
import {ENTER, COMMA} from '@angular/cdk/keycodes';
import * as filter from 'lodash/filter';
import {
  FilterModel
} from '../../types';

const template = require('./designer-string-filter.component.html');

const SEMICOLON = 186;

export const OPERATORS = [{
  value: 'EQ',
  label: 'Equals'
}, {
  value: 'NEQ',
  label: 'Not equal'
}, {
  value: 'ISIN',
  label: 'Is in'
}, {
  value: 'ISNOTIN',
  label: 'Is not in'
}, {
  value: 'CONTAINS',
  label: 'Contains'
}, {
  value: 'SW',
  label: 'Starts with'
}, {
  value: 'EW',
  label: 'Ends with'
}];

@Component({
  selector: 'designer-string-filter',
  template
})

export class DesignerStringFilterComponent {
  @Output() public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public separatorKeysCodes = [ENTER, COMMA, SEMICOLON];
  public OPERATORS = OPERATORS;
  public tempValue = '';

  init() {
    if (!this.filterModel) {
      this.filterModel = {
        modelValues : [],
        operator: ''
      };
      this.tempValue = '';
    } else {
      this.tempValue = this.filterModel.modelValues[0];
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

  add(event: MatChipInputEvent) {
    let input = event.input;
    let value = event.value;
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
    this.filterModel.modelValues = filter(this.filterModel.modelValues, (_, i) => i !== index);
    this.onFilterModelChange();
  }

  onPresetSelected(value) {
    this.tempValue = '';
    this.filterModel.modelValues = [];
    this.filterModel.operator = value;
  }

  onValueChange(value) {
    this.filterModel.modelValues = [];
    this.filterModel.modelValues.push(value);
    this.onFilterModelChange();
  }
}
