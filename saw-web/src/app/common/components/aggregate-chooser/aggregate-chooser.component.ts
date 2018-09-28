import {
  Component,
  Input,
  Output,
  OnInit,
  EventEmitter
} from '@angular/core';
import * as filter from 'lodash/filter';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';

const style = require('./aggregate-chooser.component.scss');
@Component({
  selector: 'aggregate-chooser-u',
  templateUrl: './aggregate-chooser.component.html',
  styles: [style]
})
export class AggregateChooserComponent implements OnInit {
  @Output() public change: EventEmitter<string> = new EventEmitter();
  @Input() public aggregate: string;
  @Input() public type: AnalysisType;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  public aggregates;

  ngOnInit() {
    if (NUMBER_TYPES.includes(this.type)) {
      this.aggregates = AGGREGATE_TYPES;
    } else {
      this.aggregates = filter(AGGREGATE_TYPES, type => {
        return type.value === 'count';
      });
    }
  }

  onAggregateChange(value) {
    this.change.emit(value);
  }
}
