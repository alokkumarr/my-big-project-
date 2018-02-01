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

@Component({
  selector: 'designer-string-filter',
  template
})
export class DesignerStringFilterComponent {
  @Output() public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public separatorKeysCodes = [ENTER, COMMA, SEMICOLON];

  ngOnInit() {
    if (!this.filterModel) {
      this.filterModel = {
        modelValues: []
      };
    }
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

}
