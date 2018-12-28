import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as isUndefined from 'lodash/isUndefined';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';
import * as forEach from 'lodash/forEach';

@Component({
  selector: 'aggregate-chooser-u',
  templateUrl: './aggregate-chooser.component.html',
  styleUrls: ['./aggregate-chooser.component.scss']
})
export class AggregateChooserComponent implements OnInit {
  @Output() public change: EventEmitter<string> = new EventEmitter();
  @Input() public aggregate: string;
  @Input() public columnType: string;
  @Input() public analysisType: AnalysisType;
  @Input() public sqlBuilder;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  public aggregates;

  ngOnInit() {
    if (NUMBER_TYPES.includes(this.columnType)) {
      this.aggregates = this.isAggregateEligible();
    } else {
      this.aggregates = filter(AGGREGATE_TYPES, type => {
        return type.value === 'count';
      });
    }
  }

  trackByIndex(index) {
    return index;
  }

  onAggregateChange(value) {
    this.change.emit(value);
  }

  isAggregateEligible() {
    return filter(AGGREGATE_TYPES, aggregate => {
      if (aggregate.valid.includes(this.analysisType)) {
        return true;
      }
    });
  }

  checkColumn(value, sqlBuilder) {
    let isGroupByPresent = false;
    if (isUndefined(sqlBuilder.dataFields)) {
      return;
    }
    forEach(sqlBuilder.nodeFields, node => {
      if (node.checked === 'g') {
        isGroupByPresent = true;
      }
    });
    if (!isGroupByPresent) {
      if (this.aggregate === 'percentageByRow') {
        this.aggregate = 'percentage';
      }
    }
    if (value === 'percentageByRow' && !isGroupByPresent) {
      return false;
    }
    return true;
  }
}
