declare const require: any;
import {
  Component,
  Input,
  Output,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';

import {
  AGGREGATE_TYPES,
  DEFAULT_AGGREGATE_TYPE,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';

const template = require('./aggregate-chooser.component.html');
require('./aggregate-chooser.component.scss');
@Component({
  selector: 'aggregate-chooser-u',
  template
})
export class AggregateChooserComponent {
  @Output() public change: EventEmitter<string> = new EventEmitter();
  @Input() public aggregate: string;
  @Input() public type: AnalysisType;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  public aggregates;

  ngOnInit() {
    if (this.type) {
      this.aggregates = filter(AGGREGATE_TYPES, type => {
        return type.value === 'count' &&
          NUMBER_TYPES.includes(this.type);
      });
    } else {
      this.aggregates = AGGREGATE_TYPES;
    }
  }

  onAggregateChange(value) {
    this.change.emit(value);
  }
}
