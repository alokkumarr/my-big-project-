import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';

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
  @Input() analysisSubtype: string;
  @Input() enablePercentByRow: boolean;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  public aggregates;

  ngOnInit() {
    if (NUMBER_TYPES.includes(this.columnType)) {
      this.aggregates = this.isAggregateEligible();
    } else {
      this.aggregates = filter(AGGREGATE_TYPES, type => {
        return type.value === 'count' || type.value === 'distinctCount';
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
    let enableByRowPercentage = false;
    if (this.analysisType !== 'chart') {
      return true;
    }
    const isGroupBy = this.getGroupByPresent(sqlBuilder);
    if (['column', 'bar', 'stack', 'combo'].includes(this.analysisSubtype)) {
      if (isGroupBy) {
        if (value === 'percentageByRow' && !this.enablePercentByRow) {
          return false;
        }
        enableByRowPercentage = true;
      }
    } else {
      if (value === 'percentageByRow') {
        return false;
      }
    }
    if (!isGroupBy && this.aggregate === 'percentageByRow') {
      this.aggregate = 'percentage';
    }

    if (isGroupBy && this.enablePercentByRow) {
      return true;
    }
    return (value === 'percentageByRow' && !isGroupBy && !enableByRowPercentage) ? false : true;
  }

  getGroupByPresent(sqlBuilder) {
    return fpPipe(
      fpFilter(({ checked }) => {
        return (
          checked === 'g'
        );
      })
    )(sqlBuilder.nodeFields).length > 0;
  }
}
