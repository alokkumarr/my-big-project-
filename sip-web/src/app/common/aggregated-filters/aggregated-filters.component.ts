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
    console.log(this.data, get(this.data, 'artifacts[0].artifactName') || get(this.data, 'artifacts[0].artifactsName'));
    this.filter.artifactsName = get(this.data, 'artifacts[0].artifactName') || get(this.data, 'artifacts[0].artifactsName');
    this.cols = this.data.artifacts[0].columns;
    console.log(this.cols);
  }

  columnsSelect(column) {
    console.log(column);
    this.filter.columnName = column;
    console.log(this.filter);

    this.filter.type = fpPipe(
      fpFilter(({ columnName }) => {
        return columnName === column;
      })
    )(this.data.artifacts[0].columns)[0].type;

    this.filterChange.emit(this.filter);
  }

  aggregateSelect(aggregate) {
    console.log(aggregate);
    this.filter.aggregate = aggregate;
    this.filter.isAggregationFilter = true;
    console.log(this.filter);
    this.filterChange.emit(this.filter);
  }

  onFilterModelChange(model) {
    console.log(model);
    this.filter.model = model
    console.log(this.filter);
    this.filterChange.emit(this.filter);
  }

  onRuntimeCheckboxToggle(checked) {
    if (checked) {
      this.filter.aggregate = null;
      delete this.filter.model;
    }
    this.filter.isRuntimeFilter = checked;
  }

  onOptionalCheckboxToggle(checked) {
    this.filter.isOptional = checked;
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
