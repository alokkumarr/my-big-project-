import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
// import { Artifact, Filter } from './../../modules/analyze/designer/types';
// import { ArtifactDSL } from '../../models';

// import * as groupBy from 'lodash/groupBy';
import * as get from 'lodash/get';
import * as fpPipe from 'lodash/fp/pipe';
import * as fpFilter from 'lodash/fp/filter';
// import * as fpPipe from 'lodash/fp/pipe';
// import * as fpFlatMap from 'lodash/fp/flatMap';
// import * as fpFilter from 'lodash/fp/filter';
// import * as isEmpty from 'lodash/isEmpty';
// import * as isUndefined from 'lodash/isUndefined';
// import * as map from 'lodash/map';
// import * as reduce from 'lodash/reduce';

import {
  filterAggregatesByAnalysisType,
  filterAggregatesByDataType
} from 'src/app/common/consts';
// import { NUMBER_TYPES, DATE_TYPES } from './../consts';

// const TYPE_MAP = reduce(
//   [
//     ...map(NUMBER_TYPES, type => ({ type, generalType: 'number' })),
//     ...map(DATE_TYPES, type => ({ type, generalType: 'date' })),
//     { type: 'string', generalType: 'string' }
//   ],
//   (typeMap, { type, generalType }) => {
//     typeMap[type] = generalType;
//     return typeMap;
//   },
//   {}
// );


@Component({
  selector: 'aggregated-filters',
  templateUrl: './aggregated-filters.component.html',
  styleUrls: ['./aggregated-filters.component.scss']
})
export class AggregatedFiltersComponent implements OnInit {
  @Input() data;
  @Input() filter;
  cols;
  @Output() public removeRequest: EventEmitter<null> = new EventEmitter();
  @Output() public filterChange: EventEmitter<null> = new EventEmitter();
  constructor() {}

  ngOnInit() {
    this.filter.artifactsName = get(this.data, 'artifacts[0].artifactName') || get(this.data, 'artifacts[0].artifactsName');

    if (this.data.isInRuntimeMode) {
      if (this.filter.isRuntimeFilter) {
        if (this.filter.type === 'string' || this.filter.type === 'date') {
          this.filter.aggregate = 'count';
        } else {
          this.filter.aggregate = 'sum';
        }
        delete this.filter.model;
      }
    }

    this.cols = this.data.artifacts[0].columns;
  }

  columnsSelect(column) {
    this.filter.columnName = column;

    this.filter.type = fpPipe(
      fpFilter(({ columnName }) => {
        return columnName === column;
      })
    )(this.data.artifacts[0].columns)[0].type;

    this.filterChange.emit(this.filter);
  }

  aggregateSelect(aggregate) {
    this.filter.aggregate = aggregate;
    this.filter.isAggregationFilter = true;
    this.filterChange.emit(this.filter);
  }

  onFilterModelChange(model) {
    this.filter.model = model
    this.filterChange.emit(this.filter);
  }

  onRuntimeCheckboxToggle(checked) {
    if (checked) {
      this.filter.aggregate = null;
      delete this.filter.model;
    }
    this.filter.isRuntimeFilter = checked;
    this.filterChange.emit(this.filter);
  }

  onOptionalCheckboxToggle(checked) {
    this.filter.isOptional = checked;
    this.filterChange.emit(this.filter);
  }

  remove() {
    this.removeRequest.emit();
  }

  get supportedAggregates() {
    const aggregatesForAnalysis = filterAggregatesByAnalysisType(
      this.data.analysisType
    ).filter(
      aggregate => !['percentage', 'percentagebyrow'].includes(aggregate.value)
    );
    return this.filter.type
      ? filterAggregatesByDataType(this.filter.type, aggregatesForAnalysis)
      : aggregatesForAnalysis;
  }
}
