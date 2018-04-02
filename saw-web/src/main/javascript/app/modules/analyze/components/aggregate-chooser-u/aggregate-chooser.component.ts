declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';

import {AGGREGATE_TYPES, DEFAULT_AGGREGATE_TYPE, AGGREGATE_TYPES_OBJ} from '../../consts';

const template = require('./aggregate-chooser.component.html');
require('./aggregate-chooser.component.scss');
@Component({
  selector: 'aggregate-chooser-u',
  template
})
export class AggregateChooserComponent {
  @Output() public change: EventEmitter<string> = new EventEmitter();
  @Input() public aggregate: string;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  onAggregateChange(value) {
    this.change.emit(value);
  }
}
