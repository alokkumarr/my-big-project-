import { Component, Input, Output, OnInit, EventEmitter } from '@angular/core';
import * as filter from 'lodash/filter';
import * as fpFilter from 'lodash/fp/filter';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpMap from 'lodash/fp/map';
import * as fpFlatMap from 'lodash/fp/flatMap';

import {
  AGGREGATE_TYPES,
  AGGREGATE_TYPES_OBJ,
  NUMBER_TYPES
} from '../../consts';
import { AnalysisType } from '../../types';
import { QueryDSL, ArtifactColumnDSL } from 'src/app/models';

@Component({
  selector: 'aggregate-chooser-u',
  templateUrl: './aggregate-chooser.component.html',
  styleUrls: ['./aggregate-chooser.component.scss']
})
export class AggregateChooserComponent implements OnInit {
  @Output() public change: EventEmitter<string> = new EventEmitter();
  @Input() public aggregate: string;
  @Input() public columnType: string;
  @Input() public artifactColumn: ArtifactColumnDSL;
  @Input() public analysisType: AnalysisType;
  @Input() public sipQuery: QueryDSL;
  @Input() analysisSubtype: string;
  @Input() enablePercentByRow: boolean;

  public AGGREGATE_TYPES = AGGREGATE_TYPES;
  public AGGREGATE_TYPES_OBJ = AGGREGATE_TYPES_OBJ;

  public aggregates;

  static isAggregateEligible(analysisType: string) {
    return filter(AGGREGATE_TYPES, aggregate => {
      if (aggregate.valid.includes(analysisType)) {
        return true;
      }
    });
  }

  static getGroupByPresent(fields: ArtifactColumnDSL[]) {
    return (
      fpFilter(({ area }) => {
        return area === 'g';
      })(fields).length > 0
    );
  }

  static supportsPercentByRow(analysisSubtype: string) {
    return ['column', 'bar', 'stack', 'combo'].includes(analysisSubtype);
  }

  static isAggregateValid(
    value,
    artifactColumn: ArtifactColumnDSL,
    fields: ArtifactColumnDSL[],
    analysisType: string,
    analysisSubtype: string
  ) {
    let enableByRowPercentage = false;

    /* In scenario where multiple instances of same column has been added,
       we don't want to allow an aggregate to show if it's already been used
       by *another* column.
    */
    const hasAggregateBeenUsed = fpPipe(
      fpFilter(
        field =>
          field.columnName === artifactColumn.columnName &&
          field.dataField !== artifactColumn.dataField
      ),
      fpMap(field => field.aggregate)
    )(fields).includes(value);

    if (analysisType !== 'chart') {
      return ['pivot', 'map'].includes(analysisType)
        ? !hasAggregateBeenUsed
        : true;
    }

    const isGroupBy = AggregateChooserComponent.getGroupByPresent(fields);
    if (['column', 'bar', 'stack', 'combo'].includes(analysisSubtype)) {
      if (isGroupBy) {
        if (
          value === 'percentagebyrow' &&
          !AggregateChooserComponent.supportsPercentByRow(analysisSubtype)
        ) {
          return false;
        }
        enableByRowPercentage = true;
      }
    } else {
      if (value === 'percentagebyrow') {
        return false;
      }
    }

    if (
      isGroupBy &&
      AggregateChooserComponent.supportsPercentByRow(analysisSubtype)
    ) {
      return true && !hasAggregateBeenUsed;
    }
    return value === 'percentagebyrow' && !isGroupBy && !enableByRowPercentage
      ? false
      : true && !hasAggregateBeenUsed;
  }

  ngOnInit() {
    if (NUMBER_TYPES.includes(this.columnType)) {
      this.aggregates = AggregateChooserComponent.isAggregateEligible(
        this.analysisType
      );
    } else {
      this.aggregates = filter(AGGREGATE_TYPES, type =>
        ['count', 'distinctcount'].includes(type.value)
      );
    }
  }

  trackByIndex(index) {
    return index;
  }

  onAggregateChange(value) {
    this.change.emit(value);
  }

  checkColumn(value, sipQuery: QueryDSL) {
    const fields = fpFlatMap(a => a.fields, sipQuery.artifacts);
    const isGroupBy = AggregateChooserComponent.getGroupByPresent(fields);

    if (!isGroupBy && this.aggregate === 'percentagebyrow') {
      this.aggregate = 'percentage';
      this.change.emit(this.aggregate);
    }

    return AggregateChooserComponent.isAggregateValid(
      value,
      this.artifactColumn,
      fields,
      this.analysisType,
      this.analysisSubtype
    );
  }
}
