import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  FilterModel
} from '../../types';

const template = require('./designer-number-filter.component.html');

export const OPERATORS = [{
  value: 'GT',
  label: 'Greater than'
}, {
  value: 'LT',
  label: 'Less than'
}, {
  value: 'GTE',
  label: 'Greater than or equal to'
}, {
  value: 'LTE',
  label: 'Less than or equal to'
}, {
  value: 'EQ',
  label: 'Equal to'
}, {
  value: 'NEQ',
  label: 'Not equal to'
}, {
  value: 'BTW',
  label: 'Between'
}
];

@Component({
  selector: 'designer-number-filter',
  template
})
export class DesignerNumberFilterComponent {
  @Output() public filterModelChange: EventEmitter<FilterModel> = new EventEmitter();
  @Input() public filterModel: FilterModel;

  public OPERATORS = OPERATORS;

  ngOnInit() {
    if (!this.filterModel) {
      this.filterModel = {};
    }
  }

  onFilterModelChange() {
    this.filterModelChange.emit(this.filterModel);
  }

  onValueChange(value) {
    this.filterModel.value = parseFloat(value);
    this.onFilterModelChange();
  }

  onOtherValueChange(value) {
    this.filterModel.otherValue = parseFloat(value);
    this.onFilterModelChange();
  }

  onOperatorChange(operator) {
    this.filterModel.operator = operator;
    if (this.filterModel.operator !== 'BTW') {
      this.filterModel.otherValue === null;
    }
    this.onFilterModelChange();
  }
}
