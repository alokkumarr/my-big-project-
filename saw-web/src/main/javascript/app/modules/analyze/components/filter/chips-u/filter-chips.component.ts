declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import {
  Filter
} from '../../../types';

const template = require('./filter-chips.component.html');
require('./filter-chips.component.scss');

@Component({
  selector: 'filter-chips-u',
  template
})
export class FilterChipsComponent {
  @Output() remove: EventEmitter<number>= new EventEmitter();
  @Output() removeAll: EventEmitter<null>= new EventEmitter();
  @Input() filters: Filter[];
  @Input() readonly: boolean;

  getDisplayName(filter) {
    return filter.column ? filter.column.alias || filter.column.displayName : filter.columnName;
  }

  onRemove(index) {
    this.remove.emit(index);
  }

  onRemoveAll() {
    this.removeAll.emit();
  }
}
