import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
import * as fpFlatMap from 'lodash/fp/flatMap';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';
import { QueryDSL } from 'src/app/models';

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
  @Input() public sipQuery: QueryDSL;
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

  checkColumn(value, sipQuery) {
    let enableByRowPercentage = false;
    if (this.analysisType !== 'chart') {
      return true;
    }
    const isGroupBy = this.getGroupByPresent(sipQuery);
    if (['column', 'bar', 'stack', 'combo'].includes(this.analysisSubtype)) {
      if (isGroupBy) {
        if (value === 'percentagebyrow' && !this.enablePercentByRow) {
          return false;
        }
        enableByRowPercentage = true;
      }
    } else {
      if (value === 'percentagebyrow') {
        return false;
      }
    }
    if (!isGroupBy && this.aggregate === 'percentagebyrow') {
      this.aggregate = 'percentage';
    }

    if (isGroupBy && this.enablePercentByRow) {
      return true;
    }
    return value === 'percentagebyrow' && !isGroupBy && !enableByRowPercentage
      ? false
      : true;
  }

  getGroupByPresent(sipQuery: QueryDSL) {
    return (
      fpPipe(
        fpFlatMap(artifact => artifact.fields),
        fpFilter(({ area }) => {
          return area === 'g';
        })
      )(sipQuery.artifacts).length > 0
    );
  }
}
